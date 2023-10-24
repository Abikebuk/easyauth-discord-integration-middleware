# EasyAuth Discord Integration Middleware (EDIM)
This mods allows the use of Discord commands through the Discord Integration mod to run EasyAuth command on Minecraft by extending some of the EasyAuth commands.  The goal of this mod is to add a layer of protection on servers by forcing players to register on a specific Discord Server thus filter and control player getting on the server.

## Disclaimer
This mod requires some basic Discord bot and MongoDB knowledge.

## Features
This mod 
## Dependencies 
The only true dependency is [EasyAuth](https://www.curseforge.com/minecraft/mc-mods/easyauth) that must use MongoDB (see [here](https://github.com/NikitaCartes/EasyAuth/wiki/Config)).  As such it can be considered as a EasyAuth extension.

[Discord Integration](https://www.curseforge.com/minecraft/mc-mods/dcintegration-fabric) is not required but it is around what this mod is build on. However, anything that can run Minecraft command from a Discord bot command should work.  

Optionally, [LuckyPerms](https://luckperms.net/) can be used to moderate which command can be used by players (ie. remove ``/register`` to force users to register through Discord instead)

## Config File
A configuration file ``config/edim.json`` will be automatically generated on server startup and will result in a crash because it will not be able to connect to MongoDB.  
[EasyAuth](https://www.curseforge.com/minecraft/mc-mods/easyauth) and [Discord Integration](https://www.curseforge.com/minecraft/mc-mods/dcintegration-fabric) must be configured accordingly to work with this mod.

```json
{
  "mongoConnectionUrl" : "<YourMongoDBConnectionUrl>",
  "mongoDatabase" : "easyauth",
  "mongoEasyAuthCollection" : "players",
  "mongoEdimCollection" : "edim",
  "commandRoot" : "edim",
  "commandRegister" : "register",
  "commandListPlayers" : "listPlayers",
  "randomPasswordLength" : 4
}
```

| Field                   | Default Value | Comment                                                                                                                                                                                                                                                       | 
|-------------------------|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| mongoConnectionUrl      |               | Only ``mongoConnectionUrl`` is required to be filled manually and should usually be the same as the one in ``mods/EasyAuth/config.json`` from the field ``MongoDBConnectionString``. Not setting it up or adding a wrong url will probably result in a crash. |
| mongoDatabase           | easyauth      | Same default value as EasyAuth.                                                                                                                                                                                                                               |
| mongoEasyAuthCollection | players       | Same default value as EasyAuth.                                                                                                                                                                                                                               |
| mongoEdimCollection     | edim          | Default value is ``edim`` but could be anything else.                                                                                                                                                                                                         |
| commandRoot             | edim          | Root command which could by anything else. With default values, it is reflected by the command ``/edim``                                                                                                                                                      |
| commandRegister         | register      | Name of the subcommand to register players. With default values, it is reflected by the command ``/edim register <player>``                                                                                                                                   |
| commandListPlayers      | listPlayers   | Name of the subcommand to get the list of connected players. With default value, it is reflected by the command ``/edim listPlayers``                                                                                                                         |
| randomPasswordLength    | 4             | Length of the random password given to the player when registering through this mod's register command                                                                                                                                                        |

## Changelog 
Version ``1.1.0`` :
* Adds generative command building (makes a less syntax heavy code to generate commands). Command can be built one one line and is tenfold more understandable this way.
* Moves command layout into a multi-layout command format. Instead of ``/bot_register <username>`` it is now ``/edim register <username>``. Every new commands will be added to the root command ``/edim``.
* Divides EDIM data into a second MongoDB collection in use in parallel with EasyAuth collection. EasyAuth overwrites each entry completely (= doesn't keep updated / custom elements) making EasyAuth collection uneditable to EDIM. This second collection will allow EDIM to extend some of EasyAuth features.
* Adds Indexes on UUID to both EasyAuth & EDIM collections
* Adds new configurable argument in ``edim.json``
* Adds new command ``edim listPlayers`` which lists online players
* Adds ``@JsonIgnoreProperties(ignoreUnknown=true)`` on ``ConfigModel.java`` permitting a more aggressive overwrite of the config file. Renamed fields are removed and replaced by the new ones. It doesn't keep field's value in that case. Renames to use with caution.
* Fixes ``Util.isPlayerRegistered`` which would return an exception when player is not registered (NullPointerException). Returns ``false`` now.
* Fixes ``Util.getPlayerUUID`` which would return an exception when player is not connected (NullPointerException). Returns ``null`` now.
* Fixes (?) registration command which would not work when run through a Discord Integration. Discord would show a wrong message (always the same one) even though the code runs fine server side with the expected result. I suspect ``useLocalCommands = true`` in ``config/Discord-Integration.toml`` which would activate some sort of caching. I have rewritten the registration method but I'm 100% sure it should have been working the way it was before. Worst case it changed nothing.
* Cleaner project structure. Still needs lot of work on that. (Services need some rework~)
Version ``1.0.0`` : 
* Adds ``bot_register <userName>`` command allowing an user to register. It gives a random numeric password with its length defined by ``randomPasswordLength``. The user can only register if he is connected on the minecraft Server.  
* Base version also includes its own config file handler and a basic MongoDB interface.