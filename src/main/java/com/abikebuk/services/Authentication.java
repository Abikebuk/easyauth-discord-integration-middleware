package com.abikebuk.services;

import com.abikebuk.Globals;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mongodb.client.model.Updates;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class Authentication {
    public static int register(CommandContext<ServerCommandSource> context){
        String playerArg = StringArgumentType.getString(context,"player");
        Globals.logger.info(String.format("Registration of user %s ...", playerArg));
        String playerUUID = Globals.Util.getPlayerUUID(context, playerArg);
        Globals.logger.info(String.format("Player UUID found : %s", playerUUID == null ? "null" : playerUUID));
        String resultMessage;
        if(playerUUID == null){ // Same result as Util.isPlayerConnected
            Globals.logger.info("Player not connected : Cancelling registration");
            resultMessage = "You must be connected to be able to register! Or maybe you mistyped your name...";
        }
        else if(Globals.Util.isPlayerRegistered(playerUUID)){
            Globals.logger.info("Player already registered : Cancelling registration");
            resultMessage = "You are already registered!";
        }
        else {
            String randomPassword = Globals.Util.getRandomNumericPassword(Globals.conf.randomPasswordLength);
            context.getSource().getServer().getCommandManager().executeWithPrefix(context.getSource(), String.format("auth register %s %s", playerUUID, randomPassword));
            Globals.logger.info(String.format("Registered %s with password %s", playerUUID, randomPassword));
            resultMessage = String.format(
                    "Your account with the username %s has been registered with the password > %s < ! " +
                            "Don't forget to change your password by using /account changePassword {oldPassword} {newPassword} in game.",
                    playerArg,
                    randomPassword
            );
            // Force save in order to get Mongodb update from EasyAuth
            addDataOnRegistration(context, playerArg, playerUUID, randomPassword);
        }
        String finalResultMessage = resultMessage;
        context.getSource().sendFeedback(() -> Text.of(finalResultMessage), false);
        context.getSource().getServer().getCommandManager().executeWithPrefix(context.getSource(), "save-all");
        return 0;
    }

    private static void addDataOnRegistration(CommandContext<ServerCommandSource> context, String playerName, String playerUUID, String password){
        // Data registration doesn't need to be synchronous
        new Thread(() -> {
            boolean isOffline = Globals.Util.isPlayerInOfflineMode(context, playerName);
            Globals.Util.updatePlayerData(
                    playerUUID,
                    Updates.combine(
                            Updates.set("UUID", playerUUID),
                            Updates.set("offline", isOffline),
                            Updates.set("playerName", playerName),
                            Updates.set("firstPassword", password)
                    ));
        }).start();
    }
}
