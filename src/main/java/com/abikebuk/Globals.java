package com.abikebuk;

import com.abikebuk.config.ConfigModel;
import com.abikebuk.database.Mongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Globals class
 * Contains variables available globally
 */
public class Globals {
    /**
     * Mongo instance
     */
    public static Mongo mongo;
    /**
     * Logger instance
     */
    public static final Logger logger = LoggerFactory.getLogger("edim");
    /**
     * Configs variables
     */
    public static ConfigModel conf;
}
