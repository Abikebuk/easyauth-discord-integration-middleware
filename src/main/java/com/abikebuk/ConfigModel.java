package com.abikebuk;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public class ConfigModel {
    @JsonSetter(nulls = Nulls.SKIP)
    public String mongoConnectionUrl = "";
    @JsonSetter(nulls = Nulls.SKIP)
    public String mongoDatabase = "easyauth";
    @JsonSetter(nulls = Nulls.SKIP)
    public String mongoCollection = "players";
    @JsonSetter(nulls = Nulls.SKIP)
    public String registrationCommandName = "bot_registration";

    @JsonSetter(nulls = Nulls.SKIP)
    public int randomPasswordLength = 4;}
