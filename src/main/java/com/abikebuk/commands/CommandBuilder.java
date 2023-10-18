package com.abikebuk.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CommandBuilder {
    private CommandNode node;

    public CommandBuilder(CommandNode node){
        this.node = node;
    }

    /**
     *
     * Presents duplication with generate(node). Only return type is different
     * @return
     */
    public LiteralArgumentBuilder<ServerCommandSource> generate(){
        if(node.isArgument()) throw new IllegalArgumentException("Root node must be literal");
        LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal(node.getName());
        for (CommandNode subNode : node.getSubCommands()){
            root = root.then(generate(subNode));
        }

        return node.getFunction() == null ?
                root :
                root.executes(context -> node.getFunction().apply(context));
    }

    private ArgumentBuilder<ServerCommandSource, ?> generate(CommandNode node) {
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
}
