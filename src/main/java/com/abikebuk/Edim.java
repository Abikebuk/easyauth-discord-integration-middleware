package com.abikebuk;

import com.abikebuk.commands.CommandBuilder;
import com.abikebuk.commands.CommandNode;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mongodb.client.model.Updates;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.command.ServerCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;


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
		Globals.logger.info(Globals.conf.mongoEasyAuthCollection);
		if (Globals.conf.mongoConnectionUrl.isEmpty()){
			Globals.logger.error(String.format("mongoConnectionUrl in %s is empty. This mod cannot work without it. Check the mod GitHub for more information.", ConfigurationFileHandler.filePath));
		}
	}
	private void addDataOnRegistration(CommandContext<ServerCommandSource> context, String playerName, String playerUUID, String password, boolean isOffline){
		new Thread(() -> {
			while(!Util.isPlayerRegistered(playerUUID)){
				Globals.logger.info("Retrying to fetch data for player " + playerUUID);
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
		Globals.logger.info(String.format("Registration of user %s ...", playerArg));
		String playerUUID = Util.getPlayerUUID(context, playerArg);
		Globals.logger.info(String.format("Player UUID found : %s", playerUUID == null ? "null" : playerUUID));
		String resultMessage;
		if(playerUUID == null){ // Same result as Util.isPlayerConnected
			Globals.logger.info("Player not connected : Cancelling registration");
			resultMessage = "You must be connected to be able to register! Or maybe you mistyped your name...";
		}
		else if(Util.isPlayerRegistered(playerUUID)){
			Globals.logger.info("Player already registered : Cancelling registration");
			resultMessage = "You are already registered!";
		}
		else {
			String randomPassword = Util.getRandomNumericPassword(Globals.conf.randomPasswordLength);
			context.getSource().getServer().getCommandManager().executeWithPrefix(context.getSource(), String.format("auth register %s %s", playerUUID, randomPassword));
			Globals.logger.info(String.format("Registered %s with password %s", playerUUID, randomPassword));
			resultMessage = String.format(
					"Your account with the username %s has been registered with the password > %s < ! " +
							"Don't forget to change your password by using /account changePassword {oldPassword} {newPassword} in game.",
					playerArg,
					randomPassword
			);
			// Force save in order to get Mongodb update from EasyAuth
			context.getSource().getServer().getCommandManager().executeWithPrefix(context.getSource(), "save-all");
		}
		String finalResultMessage = resultMessage;
		context.getSource().sendFeedback(() -> Text.of(finalResultMessage), false);
		return 0;
	}
	private void registerCommand(){
		CommandNode root = createCommand();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(
				new CommandBuilder(root).generate()
			);
		});
	}

	private CommandNode createCommand(){
		// root - level 0 command
		CommandNode root = new CommandNode(Globals.conf.commandRoot);
		root.setPermissionLevel(4);

		// root/getConnectedPlayers - level 1 command
		CommandNode getConnectedPlayers = new CommandNode(Globals.conf.commandListPlayers, context ->{
			context.getSource().sendFeedback(() -> Text.of(String.join(
					"; ",
					context.getSource().getServer().getPlayerNames()
			)), true);
			return 0;
		});

		// root/registrationCommand - level 1 command
		CommandNode registrationCommand = new CommandNode(Globals.conf.commandRegister);
		// root/registrationCommand/?player - level 2 command
		CommandNode registrationCommandArgument = new CommandNode("?player", this::registerBotExecution);
		registrationCommand.addSubCommand(registrationCommandArgument);
		// Add level 1 commands
		root.addSubCommand(getConnectedPlayers);
		root.addSubCommand(registrationCommand);
		return root;
	}


}

