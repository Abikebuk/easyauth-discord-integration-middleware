# EasyAuth Discord Integration Middleware (EDIM)
This mods allows the use of Discord commands through the Discord Integration mod to run EasyAuth command on Minecraft by extending some of the EasyAuth commands.  The goal of this mod is to add a layer of protection on servers by forcing players to register on a specific Discord Server thus filter player.

**README is WIP**

## Disclaimer
This mod requires some basic Discord bot and MongoDB knowledge.

## Dependencies 
The only dependency is EasyAuth

## Config File
A configuration file ``config/edim.json`` will be automatically generated on server startup and will result in a crash because it will not be able to connect to MongoDB.
The default config is [EasyAuth](https://www.curseforge.com/minecraft/mc-mods/easyauth).  
However this mod is also built arround [Discord Integration Fabric](https://www.curseforge.com/minecraft/mc-mods/dcintegration-fabric) which is the recommended option but you could always make your own bot interface.

```json
{
  "mongoConnectionUrl" : "",
  "mongoDatabase" : "easyauth",
  "mongoCollection" : "players",
  "registrationCommandName" : "bot_registration",
  "randomPasswordLength" : 4
}
```
Only ``mongoConnectionUrl`` is required and should be the same as the one in ``mods/EasyAuth/config.json`` in the field ``MongoDBConnectionString``

## Changelog 
version ``1.0.0`` : Adds ``bot_register <userName>`` command allowing an user to register. It gives a random numeric password with its length defined by ``randomPasswordLength``. The user can only register if he is connected on the minecraft Server.  
