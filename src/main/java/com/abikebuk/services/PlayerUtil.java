package com.abikebuk.services;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class PlayerUtil {
    public static int getConnectedPlayerName(CommandContext<ServerCommandSource> context){
        context.getSource().sendFeedback(() -> Text.of(String.join(
                ", ",
                context.getSource().getServer().getPlayerNames()
        )), true);
        return 0;
    }
}
