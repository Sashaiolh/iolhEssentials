package org.sashaiolh.iolhessentials.Commands;


import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.sashaiolh.iolhessentials.ConfigManager;

import static org.sashaiolh.iolhessentials.IolhEssentials.configManager;
import org.sashaiolh.iolhessentials.IolhEssentials;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



public class ReloadCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("essentials")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.literal("reload")
                                        .then(Commands.literal("config")
                                                .executes(context -> {
                                                    IolhEssentials.registerConfigs();
                                                    sendMessage(context.getSource().getPlayerOrException(), "Конфиг бота был перезагружен.");
                                                    return 1;
                                                })
                                        )
                        )
        );
    }

    private static void sendMessage(ServerPlayer player, String message) {
        player.displayClientMessage(Component.literal(message), false);
    }
}

