package com.abikebuk.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

/**
 * Class ConfigModel
 * Model of the config file ``config/edim.json``.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ConfigModel {
    @JsonSetter(nulls = Nulls.SKIP)
    public String mongoConnectionUrl = "";
    @JsonSetter(nulls = Nulls.SKIP)
    public String mongoDatabase = "easyauth";
    @JsonSetter(nulls = Nulls.SKIP)
    public String mongoEasyAuthCollection = "players";
    @JsonSetter(nulls = Nulls.SKIP)
    public String mongoEdimCollection = "edim";
    @JsonSetter(nulls = Nulls.SKIP)
    public String commandRoot = "edim";
    @JsonSetter(nulls = Nulls.SKIP)
    public String commandRegister = "register";
    @JsonSetter(nulls = Nulls.SKIP)
    public String commandUnregister = "unregister";

    @JsonSetter(nulls = Nulls.SKIP)
    public String commandListPlayers= "listPlayers";
    @JsonSetter(nulls = Nulls.SKIP)
    public String commandGetUUID = "getUuid";
    @JsonSetter(nulls = Nulls.SKIP)
    public int randomPasswordLength = 4;
}
