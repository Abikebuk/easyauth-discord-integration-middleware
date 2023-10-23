package com.abikebuk;

import com.abikebuk.commands.CommandBuilder;
import com.abikebuk.config.ConfigurationFileHandler;
import com.abikebuk.services.*;
import com.abikebuk.database.Mongo;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

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

	private void registerCommand(){
		CommandBuilder builder = new CommandBuilder();
		builder.addCommand("edim register ?player", 4, Authentication::register);
		builder.addCommand("edim listPlayers", 4, PlayerUtil::getConnectedPlayerName);
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(builder.generate()));
	}
}