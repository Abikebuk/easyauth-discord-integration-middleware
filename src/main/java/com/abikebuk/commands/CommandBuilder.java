package com.abikebuk.commands;

import com.abikebuk.Globals;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * CommandBuilder class.
 * Translation layer of the Fabric API's command creation methods.
 * Aims to make command creation easier to understand by removing all the redundant syntax of the Fabric API's functions.
 * Takes advantage of the resemblance of the way to make command with a tree (data structure).
 */
public class CommandBuilder {
    /**
     * Node to generate
     */
    private CommandNode node;

    /**
     * Constructor
     * @param node Command node to generate.
     */
    public CommandBuilder(CommandNode node) {
        this.node = node;
    }

    /**
     * Constructor. Adds a root node with an empty name
     */
    public CommandBuilder() {
        this.node = new CommandNode("");
    }

    /**
     * Generate a command readable by the Fabric API from the CommandNode
     * Root node has to be Literal as required from the Fabric API.
     * Presents duplication with generate(node). Only return type is different.
     * @return a literal command usable by the Fabric API.
     */
    public LiteralArgumentBuilder<ServerCommandSource> generate() {
        if (node.isArgument()) throw new IllegalArgumentException("Root node must be literal");
        LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal(node.getName());
        for (CommandNode subNode : node.getSubCommands()) {
            root = root.then(generate(subNode));
        }

        return node.getFunction() == null ?
                root :
                root.executes(context -> node.getFunction().apply(context));
    }

    /**
     * Generate a command readable by the Fabric API from the CommandNode
     * Used mainly in recursive call from generate() with no argument
     * Can be used to get a ArgumentCommand instead of a LiteralCommand if root node is an Argument
     * @param node a command node
     * @return a literal or argument command that can be used by the Fabric API.
     */
    public static ArgumentBuilder<ServerCommandSource, ?> generate(CommandNode node) {
        // Generate base command
        ArgumentBuilder<ServerCommandSource, ? extends ArgumentBuilder<?, ?>> command;
        command = node.isArgument() ?
                CommandManager.argument(node.getName(), StringArgumentType.string())
                        .requires(source -> source.hasPermissionLevel(node.getPermissionLevel())) :
                CommandManager.literal(node.getName())
                        .requires(source -> source.hasPermissionLevel(node.getPermissionLevel()));
        // Append sub commands to base command
        for (CommandNode subNode : node.getSubCommands()) {
            command = command.then(generate(subNode));
        }
        // Add execution function if exists
        return node.getFunction() == null ?
                command :
                command.executes(context -> node.getFunction().apply(context));
    }

    /**
     * Add a command to generate.
     * Root command has to be the same for every command added.
     * @param command command or subcommand string
     * @param permissionLevel the value of permission level (0 is everyone, 4 is op)
     * @param function function associated with the command
     */
    public void addCommand(String command, int permissionLevel, Function<CommandContext<ServerCommandSource>, Integer> function) {
        String trimmedCommand = command.trim();
        ArrayList<String> subCommands = new ArrayList<>(List.of(trimmedCommand.split("\\s+")));
        if (subCommands.isEmpty())
            throw new IllegalArgumentException("Command cannot be empty or a consecutive list of blank characters");
        String firstSubCommand = subCommands.get(0);
        if (Objects.equals(this.node.getName(), ""))
            this.node.setName(firstSubCommand);
        else if (!Objects.equals(this.node.getName(), firstSubCommand))
            throw new IllegalArgumentException("Root command must always be the same");
        Globals.logger.info("Command Stack parsed : " + String.join(";", subCommands));
        subCommands.remove(0);
        addCommand(this.node, subCommands, permissionLevel, function);
    }

    /**
     * Add a command to generate.
     * Root command has to be the same for every command added.
     * @param command command or subcommand string
     * @param function function associated with the command
     */
    public void addCommand(String command, Function<CommandContext<ServerCommandSource>, Integer> function) {
        addCommand(command, 0, function);
    }

    /**
     * Add a command to generate.
     * Root command has to be the same for every command added.
     * @param command command or subcommand string
     */
    public void addCommand(String command){
        addCommand(command, 0, null);
    }

    /**
     * Add a command to generate.
     * Root command has to be the same for every command added.
     * @param command command or subcommand string
     * @param permissionLevel the value of permission level (0 is everyone, 4 is op)
     */
    public void addCommand(String command, int permissionLevel){
        addCommand(command, permissionLevel, null);
    }

    /**
     * Add a command to generate.
     * Root command has to be the same for every command added.
     * This function is used mainly in a recursive context form other addCommand()
     * @param node current node to edit
     * @param commandStack stack of command string to navigate in
     * @param permissionLevel the value of permission level (0 is everyone, 4 is op)
     * @param function function associated with the last command of the command stack
     */
    private void addCommand(CommandNode node, ArrayList<String> commandStack, int permissionLevel, Function<CommandContext<ServerCommandSource>, Integer> function) {
        Globals.logger.info(String.format("Adding command - %s\nStack left : %s", node.getName(), String.join(";", commandStack)));
        if (commandStack.isEmpty()) {
            node.setFunction(function);
            node.setPermissionLevel(permissionLevel);
            return;
        }
        String nextSubCommand = commandStack.get(0);
        CommandNode subNode = node.getSubCommand(nextSubCommand);
        if (subNode == null) {
            subNode = new CommandNode(nextSubCommand);
            node.addSubCommand(subNode);
        }
        commandStack.remove(0);
        addCommand(subNode, commandStack, permissionLevel, function);
    }
}