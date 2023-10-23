package com.abikebuk.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

/**
 * CommandNode class.
 * Data structure for Fabric API's commands.
 */
public class CommandNode {
    /**
     * String name of the command or subcommand
     */
    private String name;
    /**
     * Subcommands attached to this node
     */
    private final ArrayList<CommandNode> subCommands;
    /**
     * function attached to this node
     */
    private Function<CommandContext<ServerCommandSource>, Integer> function;
    /**
     * Permission level value (0 = everyone, 4 = op)
     */
    private int permissionLevel = 0;
    /**
     * Defines if this node is an argument or literal
     */
    private final boolean isArg;

    /**
     * Constructor
     * @param name string name of the command or subcommand
     * @param isArg defines if this node is an argument (if true) or a literal (if false)
     * @param subCommands list of subcommands node
     * @param function function associated to the node
     */
    public CommandNode(@NotNull String name, boolean isArg, @NotNull ArrayList<CommandNode> subCommands, Function<CommandContext<ServerCommandSource>, Integer> function){
        this.name = name.trim();
        this.subCommands = subCommands;
        this.isArg = isArg;
        this.function = function;
    }
    
    /* Explicit declaration of argument */

    /**
     * Constructor
     * @param name string name of the command or subcommand
     * @param isArg defines if this node is an argument (if true) or a literal (if false)
     * @param subCommands list of subcommands node
     */
    public CommandNode(String name, boolean isArg, ArrayList<CommandNode> subCommands){
        this(name, isArg, subCommands, null);
    }

    /**
     * Constructor
     * @param name string name of the command or subcommand
     * @param isArg defines if this node is an argument (if true) or a literal (if false)
     * @param function function associated to the node
     */
    public CommandNode(String name, boolean isArg, Function<CommandContext<ServerCommandSource>, Integer> function){
        this(name, isArg, new ArrayList<>(), function);
    }

    /**
     * Constructor
     * @param name string name of the command or subcommand
     * @param isArg defines if this node is an argument (if true) or a literal (if false)
     */
    public CommandNode(String name, boolean isArg){
        this(name, isArg, new ArrayList<>(), null);
    }
    
    /* Implicit declaration as argument declaration by adding "?" as first character on name */

    /**
     * Constructor
     * Will recognize the node as an argument if the name is prefixed by "?". However it will not retain the "?" character
     * in its final name.
     * ie : "?foo" will make an argument node named "foo"
     *      "foo"  will make a literal node named "foo"
     * @param name string name of the command or subcommand.
     * @param subCommands list of subcommands node
     * @param function function associated to the node
     */
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

    /**
     * Constructor
     * Will recognize the node as an argument if the name is prefixed by "?". However it will not retain the "?" character
     * in its final name.
     * ie : "?foo" will make an argument node named "foo"
     *      "foo"  will make a literal node named "foo"
     * @param name string name of the command or subcommand.
     * @param function function associated to the node
     */
    public CommandNode(String name, Function<CommandContext<ServerCommandSource>, Integer> function){
        this(name, new ArrayList<>(), function);
    }

    /**
     * Constructor
     * Will recognize the node as an argument if the name is prefixed by "?". However it will not retain the "?" character
     * in its final name.
     * ie : "?foo" will make an argument node named "foo"
     *      "foo"  will make a literal node named "foo"
     * @param name string name of the command or subcommand.
     * @param subCommands list of subcommands node
     */
    public CommandNode(String name, ArrayList<CommandNode> subCommands){
        this(name, subCommands, null);
    }

    /**
     * Constructor
     * Will recognize the node as an argument if the name is prefixed by "?". However it will not retain the "?" character
     * in its final name.
     * ie : "?foo" will make an argument node named "foo"
     *      "foo"  will make a literal node named "foo"
     * @param name string name of the command or subcommand.     * @param name
     */
    public CommandNode(String name){
        this(name, new ArrayList<>(), null);
    }

    /* Getters & setters */

    /**
     * Get the node's name
     * @return the node name
     */
    public String getName(){ return this.name; }

    /**
     * Get the node's subcommands list
     * @return the node subcommand list
     */
    public ArrayList<CommandNode> getSubCommands() { return this.subCommands; }

    /**
     * Get the node's associated function
     * @return the node's associated function
     */
    public Function<CommandContext<ServerCommandSource>, Integer> getFunction() { return this.function; }

    /**
     * Checks if the node is an argument
     * @return true if the node is an argument, is a literal otherwise
     */
    public boolean isArgument() { return this.isArg; }

    /**
     * Checks if the node is a literal
     * @return true if the node is a literal, is an argument otherwise
     */
    public boolean isLiteral() { return !this.isArg; }

    /**
     * Get the node's permission level
     * @return the node's permission level
     */
    public int getPermissionLevel() { return this.permissionLevel; }

    /**
     * Get one node's specific subcommand node
     * @param name the string name of one of the node's subcommand node
     * @return the specified subcommand node. null if not found
     */
    public CommandNode getSubCommand(String name){
        for(CommandNode node : this.subCommands){
            if(Objects.equals(node.name, name))
                return node;
        }
        return null;
    }
    /**
     * Add a subcommand node to the list of the node's subcommand
     * @param command A command node
     */
    public void addSubCommand(CommandNode command) { this.subCommands.add(command);}


    /**
     * Change the permission level value of the node
     * @param permissionLevel a value of permission level (0 = everyone, 4 = op)
     */
    public void setPermissionLevel(int permissionLevel) { this.permissionLevel = permissionLevel; }

    /**
     * Change the name of the node
     * @param name a String name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Change the function associated to the node
     * @param function a function
     */
    public void setFunction(Function<CommandContext<ServerCommandSource>, Integer> function){
        this.function = function;
    }
}