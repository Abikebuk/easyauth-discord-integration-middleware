package com.abikebuk.services;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.concurrent.atomic.AtomicReference;

import static com.abikebuk.Globals.mongo;

public class DBInterface {
    public static boolean isPlayerRegistered(String uuid){
        return mongo.runOnEasyAuthCollection((col) -> {
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
        UpdateOptions options = new UpdateOptions().upsert(true);
        mongo.runOnEdimCollection((col) ->{
            col.updateOne(
                    Filters.eq("UUID", playerUUID),
                    data,
                    options
            );
        });
    }

    public static String getPlayerUUID(String playerName){
        AtomicReference<String> uuid = new AtomicReference<>();
        mongo.runOnEdimCollection(edim -> {
            Document foundRes = edim.find(Filters.eq("playerName", playerName))
                    .first();
            if(foundRes != null)
                uuid.set(foundRes.getString("UUID"));
        });
        return uuid.get();
    }

    public static int getPlayerUUID(CommandContext<ServerCommandSource> context){
        String playerName = StringArgumentType.getString(context, "player");
        String uuid = getPlayerUUID(playerName);
        if(uuid == null)
            context.getSource().sendFeedback(() -> Text.of(String.format("There is no player found named %s", playerName)), false);
        else
            context.getSource().sendFeedback(() -> Text.of(uuid), false);
        return 0;
    }
}