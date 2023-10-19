package com.abikebuk;

import com.mojang.brigadier.context.CommandContext;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;

public class Util {
    public static boolean isPlayerRegistered(String uuid){
        return Globals.mongo.runOnCollection((col) -> {
            FindIterable<Document> res = col.find(Filters.eq("UUID", uuid))
                    .projection(Projections.include("password"));
            try{
                return !res.first().getString("password").isEmpty();
            }catch (NullPointerException e){ // catches res.first() == null which mean there is no user with player's UUID
                return false;
            }
        });
    }

    public static void updatePlayerData(String playerUUID, Bson data){
        Globals.mongo.runOnCollection((col) ->{
            col.updateOne(
                    Filters.eq("UUID", playerUUID),
                    data
            );
        });
    }

    public static String getRandomNumericPassword(int size){
        if (size < 1) throw new IllegalArgumentException("Password size must be higher than 1");
        return String.format(
                "%0" + size + "d",
                (int) Math.round(Math.random() * Math.pow(10, size))
                );
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

    public static boolean isPlayerConnected(CommandContext<ServerCommandSource> context, String player){
        return List.of(context.getSource().getServer().getPlayerNames()).contains(player);
    }
}
