package com.abikebuk;

import com.abikebuk.config.ConfigModel;
import com.abikebuk.database.Mongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Globals {
    public static Mongo mongo;
    public static final Logger logger = LoggerFactory.getLogger("edim");
    public static ConfigModel conf;

}
