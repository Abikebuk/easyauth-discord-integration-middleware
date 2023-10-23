package com.abikebuk.services;

import com.abikebuk.Globals;
import com.abikebuk.Util;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * Authentication class
 * Services centered around authentication with EasyAuth
 */
public class Authentication {
    /**
     * Register a player in EasyAuth database and populate edim's own database. Requires a "player" argument.
     * Registration will send back a message to the entity calling this method and give him a random password.
     * This command will register the player only if the player is connected in game and not already registered in the database.
     * It will do nothing otherwise and give a corresponding message corresponding to the case.
     * Requires a "player" argument.
     * @param context element given by argument/literal executes method.
     * @return a value to give to Fabric
     */
    public static int register(CommandContext<ServerCommandSource> context){
        String playerArg = StringArgumentType.getString(context,"player");
        Globals.logger.info(String.format("Registration of user %s ...", playerArg));
        String playerUUID = MinecraftInterface.getPlayerUUID(context, playerArg);
        Globals.logger.info(String.format("Player UUID found : %s", playerUUID == null ? "null" : playerUUID));
        String resultMessage;
        if(playerUUID == null){ // Same result as Util.isPlayerConnected
            Globals.logger.info("Player not connected : Cancelling registration");
            resultMessage = "You must be connected to be able to register! Or maybe you mistyped your name...";
        }
        else if(DBInterface.isPlayerRegistered(playerUUID)){
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
            addDataOnRegistration(context, playerArg, playerUUID, randomPassword);
        }
        String finalResultMessage = resultMessage;
        context.getSource().sendFeedback(() -> Text.of(finalResultMessage), false);
        context.getSource().getServer().getCommandManager().executeWithPrefix(context.getSource(), "save-all");
        return 0;
    }

    /**
     * Adds a few data complementary to the one produced by EasyAuth in EDIM's database.
     * Database are separated because of how EasyAuth manages its MongoDB database which overwrite everything on update.
     * @param context element given by argument/literal executes method.
     * @param playerName In game name of the player
     * @param playerUUID UUID of the player
     * @param password First password generated
     */
    private static void addDataOnRegistration(CommandContext<ServerCommandSource> context, String playerName, String playerUUID, String password){
        // Data registration doesn't need to be synchronous
        new Thread(() -> {
            boolean isOffline = MinecraftInterface.isPlayerInOfflineMode(context, playerName);
            DBInterface.updatePlayerData(
                    playerUUID,
                    Updates.combine(
                            Updates.set("UUID", playerUUID),
                            Updates.set("offline", isOffline),
                            Updates.set("playerName", playerName),
                            Updates.set("firstPassword", password)
                    ));
        }).start();
    }

    /**
     * Removes a player from the database
     * Requires a "player" argument
     * @param context element given by argument/literal executes method.
     * @return a value to give to Fabric
     */
    public static int unregister(CommandContext<ServerCommandSource> context){
        String playerName = StringArgumentType.getString(context, "player");
        String uuid = DBInterface.getPlayerUUID(playerName);
        if(uuid != null){
            context.getSource().sendFeedback(() -> Text.of(String.format("There is no player named %s", playerName)), false);
            return 1;
        }
        context.getSource().getServer().getCommandManager().executeWithPrefix(context.getSource(), String.format("auth remove %s", uuid));
        Globals.mongo.runOnEdimCollection(col ->{
            col.deleteOne(Filters.eq("UUID", uuid));
        });
        return 0;
    }
}