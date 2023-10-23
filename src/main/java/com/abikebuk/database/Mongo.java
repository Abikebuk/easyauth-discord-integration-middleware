package com.abikebuk.database;

import com.abikebuk.Globals;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import com.mongodb.client.model.Indexes;
import org.bson.Document;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Mongo class.
 * Mongo database access class.
 */
public class Mongo {
    /**
     * Database name
     */
    private final String database;
    /**
     * EasyAuth collection name
     */
    private final String easyAuthCollection;
    /**
     * EDIM collection name
     */
    private final String edimCollection;
    /**
     * ServerApi (Mongo configuration related)
     */
    private final ServerApi serverApi;
    /**
     * Mongo settings
     */
    private final MongoClientSettings settings;

    /**
     * Constructor
     * Creates a Mongo connection taking in consideration EDIM's config file
     * Thus it needs to be run after "ConfigurationFileHandler.loadConfig()"
     */
    public Mongo() {
        database = Globals.conf.mongoDatabase;
        easyAuthCollection = Globals.conf.mongoEasyAuthCollection;
        edimCollection = Globals.conf.mongoEdimCollection;
        this.serverApi = this.getServerApi();
        this.settings = this.getSettings();

        // There should be only one UUID per registration
        // Somehow EasyAuth doesn't do that ~
        this.getEasyAuthCollection().createIndex(Indexes.ascending("UUID"));
        this.getEdimCollection().createIndex(Indexes.ascending("UUID"));
    }

    /**
     * Generate a ServerApi
     * @return a ServerApi required to create a Mongo instance
     */
    private ServerApi getServerApi() {
        return ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
    }

    /**
     * Generate Mongo settings
     * @return Settings required to create a Mongo instance
     */
    private MongoClientSettings getSettings() {
        return MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(Globals.conf.mongoConnectionUrl))
                .serverApi(serverApi)
                .build();
    }

    /**
     * Returns the Mongo client
     * @return The client
     */
    private MongoClient getClient(){
        return MongoClients.create(this.settings);
    }

    /**
     * Returns the Mongo database
     * @return The Mongo database
     */
    private MongoDatabase getDatabase(){
        return this.getClient().getDatabase(this.database);
    }

    /**
     * Returns the EasyAuth collection
     * @return The EasyAuth collection
     */
    private MongoCollection<Document> getEasyAuthCollection(){
        return this.getDatabase().getCollection(this.easyAuthCollection);
    }

    /**
     * Retuns the EDIM collection
     * @return The EDIM collection
     */
    private MongoCollection<Document> getEdimCollection(){
        return this.getDatabase().getCollection(this.edimCollection);
    }

    /* Interfaces */

    /**
     * Runs a function on the EasyAuth collection
     * Exists to avoid writing error catching everytime.
     * @param func Function which will use the Mongo collection
     * @return A boolean returned by the function
     */
    public boolean runOnEasyAuthCollection(Function<MongoCollection<Document>, Boolean> func){
        try{
            return func.apply(this.getEasyAuthCollection());
        } catch (Exception e){
            Globals.logger.error(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Runs a function on the EasyAuth collection
     * Exists to avoid writing error catching everytime.
     * @param func Function which will use the Mongo collection
     */
    public void runOnEasyAuthCollection(Consumer<MongoCollection<Document>> func){
        try{
            func.accept(this.getEasyAuthCollection());
        } catch (Exception e){
            Globals.logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Runs a function on the EDIM collection
     * Exists to avoid writing error catching everytime.
     * @param func Function which will use the Mongo collection
     * @return A boolean returned by the function
     */
    public boolean runOnEdimCollection(Function<MongoCollection<Document>, Boolean> func){
        try{
            return func.apply(this.getEasyAuthCollection());
        } catch (Exception e){
            Globals.logger.error(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Runs a function on the EasyAuth collection
     * Exists to avoid writing error catching everytime.
     * @param func Function which will use the Mongo collection
     */
    public void runOnEdimCollection(Consumer<MongoCollection<Document>> func){
        try{
            func.accept(this.getEdimCollection());
        } catch (Exception e){
            Globals.logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Runs a function on the database
     * Exists to avoid writing error catching everytime.
     * @param func Function which will use the Mongo database
     * @return A boolean returned by the function
     */
    public boolean runOnDatabase(Function<MongoDatabase, Boolean> func){
        try{
            return func.apply(this.getDatabase());
        } catch (Exception e){
            Globals.logger.error(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Runs a function on the database
     * Exists to avoid writing error catching everytime.
     * @param func Function which will use the Mongo database
     */
    public void runOnDatabase(Consumer<MongoDatabase> func){
        try{
            func.accept(this.getDatabase());
        } catch (Exception e){
            Globals.logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
}