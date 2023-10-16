package com.abikebuk;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mongodb.client.model.Updates;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Uuids;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import java.util.Objects;


public class Edim implements ModInitializer {
	@Override
	public void onInitialize() {
		// Static variables initialization
		Globals.conf = ConfigurationFileHandler.loadConfig();
		Globals.mongo = new Mongo();

		registerCommand();
		runPostInitializationChecks();
	}

	private void runPostInitializationChecks(){
		Globals.LOGGER.info(Globals.conf.mongoCollection);
		if (Globals.conf.mongoConnectionUrl.isEmpty()){
			Globals.LOGGER.error(String.format("mongoConnectionUrl in %s is empty. This mod cannot work without it. Check the mod GitHub for more information.", ConfigurationFileHandler.filePath));
		}
	}
	private void addDataOnRegistration(CommandContext<ServerCommandSource> context, String playerName, String playerUUID, String password, boolean isOffline){
		new Thread(() ->{
			while(!Util.isPlayerRegistered(playerUUID)){
				Globals.LOGGER.info("Retrying to fetch data for player " + playerUUID);
				try {
					context.getSource().getServer().getCommandManager().executeWithPrefix(context.getSource(), "save-all");
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
			Util.updatePlayerData(
					playerUUID,
					Updates.combine(
							Updates.set("offline", isOffline),
							Updates.set("playerName", playerName),
							Updates.set("firstPassword", password)
					));
		}).start();

	}
	private int registerBotExecution(CommandContext<ServerCommandSource> context){
		String playerArg = StringArgumentType.getString(context,"player");
		String playerUUID = "";
		try {
			playerUUID = Util.getPlayerUUID(context, playerArg);
		}catch (Exception e){
			context.getSource().sendFeedback(() -> Text.of("You must be connected to be able to register! Or maybe you mistyped your name..." ), false);
			return 0;
		}
		if(Util.isPlayerRegistered(playerUUID)){
			context.getSource().sendFeedback(() -> Text.of(String.format("You are already registered!")), false);
		}else {
			String offlinePlayerUUID = Uuids.getOfflinePlayerUuid(playerArg).toString();
			boolean isOffline = Objects.equals(offlinePlayerUUID, playerUUID);
			String randomPassword = Util.getRandomNumericPassword(Globals.conf.randomPasswordLength);
			context.getSource().getServer().getCommandManager().executeWithPrefix(context.getSource(), String.format("auth register %s %s", playerUUID, randomPassword));
			this.addDataOnRegistration(context, playerArg, playerUUID, randomPassword, isOffline);
			context.getSource().sendFeedback(() -> Text.of(
					String.format("Your account with the username %s has been registered with the password > %s < ! Don't forget to change your password by using /account changePassword {oldPassword} {newPassword} in game.  ", playerArg, randomPassword)
			), false);
		}
		return 1;
	}
	private void registerCommand(){
		final String name = "bot_register";
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(
					CommandManager.literal(name)
							.requires(source -> source.hasPermissionLevel(4))
							.then(CommandManager.argument("player", StringArgumentType.string())
									.executes(this::registerBotExecution))
			);
		});
	}
}

