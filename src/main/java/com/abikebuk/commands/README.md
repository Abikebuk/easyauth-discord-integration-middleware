# com.abikebuk.commands
this package aims to translate Fabric's API function to build a Minecraft command into a simpler, readable and understandable code.

## Fabric API way to make command
In Fabric, to make any command you are required to use a syntax heavy code to make any command.  
The Fabric command creation looks like that (taken from Fabric's documentation [here](https://fabricmc.net/wiki/tutorial:command_examples)): 
```java
CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
    dispatcher.register(literal("broadcast")
          .requires(source -> source.hasPermissionLevel(2))
          .then(argument("color", ColorArgumentType.color())
              .then(argument("message", greedyString())
                  .executes(ctx -> broadcast(ctx.getSource(), getColor(ctx, "color"), getString(ctx, "message")))))); 
```
It presents lot of different classes and wrapped ``then()`` to make just one command, complemented by a reference documentation partitionned on 3 different websites, 
long intricate classes names and almost no examples on most of what you need to build a command, makes a very bad coding experience.  

## How-To-Use
This package features two different way to build a command with the first being the detail of the structure behind the second which the transformation of the first in a more user friendly code.

### First Method : 
Easiest way to build a command
```java
CommandBuilder builder = new CommandBuilder();
builder.addCommand("command subCommand ?subCommandArgument", commandFunction); // "?" defines the command as an argument
CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
    dispatcher.register(
      builder.generate();
    ));
```
As written in the code snippet, ``?`` prefixed to a commandString will define it as an argument. Final name of the subcommand is the same without ``?``.  
``Example : command subCommand ?argument`` will be translated to ``/command subCommand <argument>`` on Minecraft console. 
## Second method

This method is more complicated than the first and represents the data structure around the command building which is a tree. Maybe someone find it more useful to use this way.
```java
// Create each commands and sub-commands
CommandNode command = new CommandNode("command", null);
CommandNode subCommand = new CommandNode("subCommand", null);
CommandNode subCommandArgument = new CommandNode("?subCommandArgument", commandFunction); // "?" defines the command as an argument
// Link each command within each other
subcommand.addSubCommand(subCommandArgument); // Links : subcommand subCommandArgument 
command.addSubCommand(subCommand); // Links : command subcommand subCommandArgument
new CommandBuilder builder = new CommandBuilder(command);
CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
    dispatcher.register(
            builder.generate();
    ));
```
## Command's Function
Command Function is the exact same as Fabric's API. The function signature is ``int foo(CommandContext<ServerCommandSource> context)`` which can be use like that: 
```java
int hello(CommandContext<ServerCommandSource> context){
    String argument = StringArgumentType.getString(context,"subCommandArgument"); // No "?" in front of the name
    context.getSource().sendFeedback(() -> Text.of("Hello World : " + argument));
}

[...]

CommandBuilder builder = new CommandBuilder();
builder.addCommand("command subCommand ?subCommandArgument", this::hello);
CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
    dispatcher.register(
        builder.generate();
    ));
```
Or in a lambda context : 
```java
CommandBuilder builder = new CommandBuilder();
builder.addCommand("command subCommand ?subCommandArgument", 
    context -> {
        String argument = StringArgumentType.getString(context,"subCommandArgument"); // No "?" in front of the name
        context.getSource().sendFeedback(() -> Text.of("Hello World : " + argument)); 
    }
);
CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
    dispatcher.register(
      builder.generate();
    ));
```