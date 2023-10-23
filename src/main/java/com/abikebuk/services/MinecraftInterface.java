package com.abikebuk.services;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;

import java.util.List;
import java.util.Objects;

/**
 * Class MinecraftInterface
 * Interface class with the Minecraft instance. Contains services centered around the Minecraft instance
 */
public class MinecraftInterface {
    /**
     * Checks if a player is connected on the Minecraft instance.
     * @param context element given by argument/literal executes method.
     * @param playerName In game name of the player
     * @return true if the targeted player is connected. false otherwise.
     */
    public static boolean isPlayerConnected(CommandContext<ServerCommandSource> context, String playerName){
        return List.of(context.getSource().getServer().getPlayerNames()).contains(playerName);
    }

    /**
     * Checks if a player is in "offline mode" (cracked client)
     * Returns false if player is not connected.
     * @param context element given by argument/literal executes method.
     * @param playerName In game name of the player
     * @return true of the player is in offline mode. false otherwise or if the player isn't connected
     */
    public static boolean isPlayerInOfflineMode(CommandContext<ServerCommandSource> context, String playerName){
        String playerUUID = getPlayerUUID(context, playerName);
        String offlinePlayerUUID = Uuids.getOfflinePlayerUuid(playerName).toString();
        return Objects.equals(playerUUID, offlinePlayerUUID);
    }

    /**
     * Return a player from it's name
     * @param context element given by argument/literal executes method.
     * @param playerName In game name of the player
     * @return A player entity. Null if not found
     */
    public static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> context, String playerName){
        return context.getSource().getServer().getPlayerManager().getPlayer(playerName);
    }

    /**
     * Fetch a player's UUID from the minecraft instance.
     * To not confuse with DBInterface.getPlayerUUID which will fetch UUID from database
     * @param context element given by argument/literal executes method.
     * @param playerName In game name of the player
     * @return The player's UUID. null if not found.
     */
    public static String getPlayerUUID(CommandContext<ServerCommandSource> context, String playerName){
        try {
            return getPlayer(context, playerName).getUuid().toString();
        }catch(NullPointerException e){ // getPlayer(context, playerName) returns null when no connected playerName is found
            return null;
        }
    }

    /**
     * Sends the list of the connected player on the Minecraft instance.
     * @param context element given by argument/literal executes method.
     * @return a value to give to Fabric
     */
    public static int getConnectedPlayerName(CommandContext<ServerCommandSource> context){
        context.getSource().sendFeedback(() -> Text.of(String.join(
                ", ",
                context.getSource().getServer().getPlayerNames()
        )), true);
        return 0;
    }
}
