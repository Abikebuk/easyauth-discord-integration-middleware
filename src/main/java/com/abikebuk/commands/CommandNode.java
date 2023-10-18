package com.abikebuk.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Function;

public class CommandNode {
    private String name;
    private ArrayList<CommandNode> subCommands;
    private Function<CommandContext<ServerCommandSource>, Integer> function;
    private int permissionLevel = 0;
    private boolean isArg;
    public CommandNode(@NotNull String name, boolean isArg, @NotNull ArrayList<CommandNode> subCommands, Function<CommandContext<ServerCommandSource>, Integer> function){
        this.name = name.trim();
        this.subCommands = subCommands;
        this.isArg = isArg;
        this.function = function;
    }
    
    /* Explicit declaration of argument */
    
    public CommandNode(String name, boolean isArg, ArrayList<CommandNode> subCommands){
        this(name, isArg, subCommands, null);
    }
    
    public CommandNode(String name, boolean isArg, Function<CommandContext<ServerCommandSource>, Integer> function){
        this(name, isArg, new ArrayList<>(), function);
    }
    
    public CommandNode(String name, boolean isArg){
        this(name, isArg, null, null);
    }
    
    /* Implicit declaration as argument declaration by adding "?" as first character on name */
    public CommandNode(String name, ArrayList<CommandNode> subCommands, Function<CommandContext<ServerCommandSource>, Integer> function){
        if (name.trim().startsWith("?")) {
           this.name = name.trim().replaceFirst("\\?", "");
           this.isArg = true;
        }else {
            this.name = name.trim();
            this.isArg = false;
        }
        this.subCommands = subCommands;
        this.function = function;
    }

    public CommandNode(String name, Function<CommandContext<ServerCommandSource>, Integer> function){
        this(name, new ArrayList<>(), function);
    }

    public CommandNode(String name, ArrayList<CommandNode> subCommands){
        this(name, subCommands, null);
    }

    public CommandNode(String name){
        this(name, new ArrayList<>(), null);
    }

    /* Getters & setters */
    public String getName(){ return this.name; }
    public ArrayList<CommandNode> getSubCommands() { return this.subCommands; }
    public void addSubCommand(CommandNode command) { this.subCommands.add(command);}
    public Function<CommandContext<ServerCommandSource>, Integer> getFunction() { return this.function; }

    public boolean isArgument() { return this.isArg; }

    public int getPermissionLevel() { return this.permissionLevel; }

    public boolean isLiteral() { return !this.isArg; }

    public void setPermissionLevel(int permissionLevel) { this.permissionLevel = permissionLevel; }
}
