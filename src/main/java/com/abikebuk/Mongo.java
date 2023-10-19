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
    private String database;
    private String easyAuthCollection;
    private String edimCollection;
    private ServerApi serverApi;
    private MongoClientSettings settings;

    public Mongo() {
        database = Globals.conf.mongoDatabase;
        easyAuthCollection = Globals.conf.mongoEasyAuthCollection;
        edimCollection = Globals.conf.mongoEdimCollection;
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

    private MongoCollection<Document> getEasyAuthCollection(){
        return this.getDatabase().getCollection(this.easyAuthCollection);
    }

    private MongoCollection<Document> getEdimCollection(){
        return this.getDatabase().getCollection(this.edimCollection);
    }

    /* Interfaces */
    public boolean runOnEasyAuthCollection(Function<MongoCollection<Document>, Boolean> func){
        try{
            return func.apply(this.getEasyAuthCollection());
        } catch (Exception e){
            Globals.logger.error(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void runOnEasyAuthCollection(Consumer<MongoCollection<Document>> func){
        try{
            func.accept(this.getEasyAuthCollection());
        } catch (Exception e){
            Globals.logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean runOnEdimCollection(Function<MongoCollection<Document>, Boolean> func){
        try{
            return func.apply(this.getEasyAuthCollection());
        } catch (Exception e){
            Globals.logger.error(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void runOnEdimCollection(Consumer<MongoCollection<Document>> func){
        try{
            func.accept(this.getEdimCollection());
        } catch (Exception e){
            Globals.logger.error(e.getMessage());
            e.printStackTrace();
        }
    }    public boolean runOnDatabase(Function<MongoDatabase, Boolean> func){
        try{
            return func.apply(this.getDatabase());
        } catch (Exception e){
            Globals.logger.error(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void runOnDatabase(Consumer<MongoDatabase> func){
        try{
            func.accept(this.getDatabase());
        } catch (Exception e){
            Globals.logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
}