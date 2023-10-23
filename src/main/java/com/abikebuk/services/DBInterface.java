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

/**
 * Class DBInterface
 * Interface class with the Mongo database. Contains services centered around the database.
 */
public class DBInterface {
    /**
     * Checks if one player is already registered
     * @param uuid UUID of the player
     * @return true if the player is already registered. false otherwise.
     */
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

    /**
     * Updates a player's data (only EDIM side)
     * @param playerUUID UUID of the player
     * @param data some data
     */
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

    /**
     * Fetch a player's UUID from database.
     * To not confuse with MinecraftInterface.getPlayerUUID which will fetch UUID from game.
     * @param playerName In game name of the player
     * @return The UUID of the player. Null if it doesn't exists in the database
     */
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

    /**
     * Fetch a player's UUID from database.
     * To not confuse with MinecraftInterface.getPlayerUUID which will fetch UUID from game.
     * Requires a "player" argument
     * @param context element given by argument/literal executes method.
     * @return a value to give to Fabric
     */
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