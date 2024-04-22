package org.sashaiolh.iolhessentials.Commands.Helpop;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.sashaiolh.iolhessentials.IolhEssentials;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.sashaiolh.iolhessentials.PermissionUtils;

public class HelpopCommand {
    public static LuckPerms luckPerms;
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        List<String> arguments = new ArrayList<>();

        dispatcher.register(
                Commands.literal("helpop")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("message", StringArgumentType.greedyString())

                            .executes(context -> {
                                    String message = StringArgumentType.getString(context, "message");

                                luckPerms = LuckPermsProvider.get();
                                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                                List<ServerPlayer> playersWithPermission = PermissionUtils.getPlayersWithPermission(luckPerms, server, "command.helpop");
                                for (ServerPlayer player : playersWithPermission) {
                                    LocalTime timeNow = LocalTime.now();
                                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                                    String formattedTime = timeNow.format(timeFormatter);
                                    String timeInMessage = "§8[" + "§7" + formattedTime + "§8] ";
                                    String helpopPrefix = IolhEssentials.configManager.getConfig("helpopPrefix");


                                    final String[] nicknameColorCode = {""};
                                    nicknameColorCode[0] = IolhEssentials.helpopPexConfigManager.getConfig("defaultColor");

                                    int totalChecks = IolhEssentials.helpopPexConfigManager.getAllKeys().size();
                                    AtomicInteger finishedChecks = new AtomicInteger(0);

                                    for (String key : IolhEssentials.helpopPexConfigManager.getAllKeys()) {
                                        PermissionUtils.isUser(player.getUUID(), key, isUser -> {
                                            if (isUser) {
                                                nicknameColorCode[0] = IolhEssentials.helpopPexConfigManager.getConfig(key);
                                            }
                                            // Увеличиваем счетчик завершенных проверок
                                            if (finishedChecks.incrementAndGet() == totalChecks) {
                                                // Все проверки завершены, теперь можно отобразить сообщение
                                                String resultMessage = timeInMessage + helpopPrefix + "§" + nicknameColorCode[0] + " " + player.getName().getString() + "§8: §f" + message;
                                                player.displayClientMessage(Component.literal(resultMessage), false);
                                            }
                                        });
                                    }


                                }
                                    return 1;
                                })


                        )
        );

    }


}
