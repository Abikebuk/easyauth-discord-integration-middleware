package com.abikebuk.config;

import com.abikebuk.Globals;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

public class ConfigurationFileHandler {
    public static final String filePath = "./config/edim.json";
    private static final File confFile = new File(filePath);
    private static final ObjectMapper mapper = new ObjectMapper();
    public static ConfigModel loadConfig() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        if (!confFile.exists()) return createDefaultFile();
        return readConfFile();
    }

    private static ConfigModel createDefaultFile() {
        ConfigModel conf = new ConfigModel();
        try {
            mapper.writeValue(confFile, conf);
            Globals.logger.info("Configuration file created. Don't forget to read the documentation to populate the missing configuration values.");
            return conf;
        } catch (IOException e) {
            Globals.logger.error("Could not create configuration file!");
            e.printStackTrace();
            return null;
        }
    }

    private static ConfigModel readConfFile(){
        try{
            ConfigModel res = mapper.readValue(confFile, ConfigModel.class);
            // Updates missing values to default one
            // This does not append but rewrite the whole file thus field order might change
            mapper.writeValue(confFile, res);
            return res;
        } catch (JsonMappingException e) {
            Globals.logger.error("Could not read configuration file! (JsonMappingException)");
            e.printStackTrace();
        } catch (JsonParseException e) {
            Globals.logger.error("Could not read configuration file! (JsonParseException)");
            e.printStackTrace();
        } catch (IOException e) {
            Globals.logger.error("Could not read configuration file! (IOException)");
            e.printStackTrace();
        }
        return null;
    }
}