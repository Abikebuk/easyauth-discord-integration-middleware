package com.abikebuk;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import org.bson.Document;

import java.util.function.Consumer;
import java.util.function.Function;

public class Mongo {
    private String database = "easyauth";
    private String collection = "players";

    private ServerApi serverApi;

    private MongoClientSettings settings;

    public Mongo() {
        this.serverApi = this.getServerApi();
        this.settings = this.getSettings();

    }
    private ServerApi getServerApi() {
        return ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
    }

    private MongoClientSettings getSettings() {
        return MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(Globals.conf.mongoConnectionUrl))
                .serverApi(serverApi)
                .build();
    }

    private MongoClient getClient(){
        return MongoClients.create(this.settings);
    }

    private MongoDatabase getDatabase(){
        return this.getClient().getDatabase(this.database);
    }

    private MongoCollection<Document> getCollection(){
        return this.getDatabase().getCollection(this.collection);
    }

    /* Interfaces */
    public boolean runOnCollection(Function<MongoCollection<Document>, Boolean> func){
        try{
            return func.apply(this.getCollection());
        } catch (Exception e){
            Globals.LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void runOnCollection(Consumer<MongoCollection<Document>> func){
        try{
            func.accept(this.getCollection());
        } catch (Exception e){
            Globals.LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean runOnDatabase(Function<MongoDatabase, Boolean> func){
        try{
            return func.apply(this.getDatabase());
        } catch (Exception e){
            Globals.LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void runOnDatabase(Consumer<MongoDatabase> func){
        try{
            func.accept(this.getDatabase());
        } catch (Exception e){
            Globals.LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }
}