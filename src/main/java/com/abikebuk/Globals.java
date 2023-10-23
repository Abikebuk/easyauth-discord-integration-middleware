package com.abikebuk;

import com.abikebuk.config.ConfigModel;
import com.abikebuk.database.Mongo;
import com.mojang.brigadier.context.CommandContext;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class Globals {
    public static Mongo mongo;
    public static final Logger logger = LoggerFactory.getLogger("edim");
    public static ConfigModel conf;
}
