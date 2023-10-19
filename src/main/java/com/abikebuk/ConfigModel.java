package com.abikebuk;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

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
    public String commandListPlayers= "listPlayers";

    @JsonSetter(nulls = Nulls.SKIP)
    public int randomPasswordLength = 4;}
