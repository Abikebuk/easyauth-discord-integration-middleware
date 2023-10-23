package com.abikebuk.services;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;

import java.util.List;
import java.util.Objects;

public class MinecraftInterface {
    public static boolean isPlayerConnected(CommandContext<ServerCommandSource> context, String player){
        return List.of(context.getSource().getServer().getPlayerNames()).contains(player);
    }

    public static boolean isPlayerInOfflineMode(CommandContext<ServerCommandSource> context, String player){
        String playerUUID = getPlayerUUID(context, player);
        String offlinePlayerUUID = Uuids.getOfflinePlayerUuid(player).toString();
        return Objects.equals(playerUUID, offlinePlayerUUID);
    }

    public static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> context, String playerName){
        return context.getSource().getServer().getPlayerManager().getPlayer(playerName);
    }

    public static String getPlayerUUID(CommandContext<ServerCommandSource> context, String playerName){
        try {
            return getPlayer(context, playerName).getUuid().toString();
        }catch(NullPointerException e){ // getPlayer(context, playerName) returns null when no connected player is found
            return null;
        }
    }
}
