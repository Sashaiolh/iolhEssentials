package org.sashaiolh.iolhessentials.SocialSpy;

import com.ibm.icu.lang.UScript;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.sashaiolh.iolhessentials.IolhEssentials;
import org.sashaiolh.iolhessentials.PermissionUtils;
import org.sashaiolh.iolhessentials.SocialSpy.Utils.Command;
import org.sashaiolh.iolhessentials.SocialSpy.Utils.SocialSpyUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.sashaiolh.iolhessentials.SocialSpy.SpyCommandsConfigManager.spyCommands;

@Mod.EventBusSubscriber
public class SocialSpy {

    public static LuckPerms luckPerms;

    public static boolean matchRegex(Pattern pattern, String message) {
        Matcher matcher = pattern.matcher(message);
        return matcher.find();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCommand(CommandEvent event) {
        CommandSourceStack source = event.getParseResults().getContext().getSource();
        String command = event.getParseResults().getReader().getString();


        StringBuilder regexBuilder = new StringBuilder();
        regexBuilder.append("^(");
        for (Command spyCommand : spyCommands) {
            regexBuilder.append(spyCommand.getCommand());
            regexBuilder.append("|");
        }
        regexBuilder.deleteCharAt(regexBuilder.toString().length() - 1); // Удаляем последнюю вертикальную черту
        regexBuilder.append(")");// Конец регекса
        String patternString = regexBuilder.toString();

        Pattern pattern = Pattern.compile(patternString);


        if (matchRegex(pattern, command)) {
            luckPerms = LuckPermsProvider.get();
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();


            LocalTime timeNow = LocalTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedTime = timeNow.format(timeFormatter);
            String timeInMessage = "§8[" + "§7" + formattedTime + "§8] ";
            String socialspyPrefix = IolhEssentials.configManager.getConfig("socialspyPrefix");


            List<SocialSpyUser> socialSpyUsers = SocialSpyUsersManager.getSocialSpyUsers();

            for (SocialSpyUser socialSpyUser : socialSpyUsers) {
                ServerPlayer targetPlayer = server.getPlayerList().getPlayerByName(socialSpyUser.getNickname());
                if (!(targetPlayer == null)) {
                    if(!(targetPlayer.getUUID()==source.getPlayer().getUUID())) {
//                        User user = luckPerms.getPlayerAdapter(ServerPlayer.class).getUser(targetPlayer);
//                        if (PermissionUtils.hasPermission(user, "essentials.socialspy")) {
                            final String[] nicknameColorCode = {""};
                            nicknameColorCode[0] = IolhEssentials.helpopPexConfigManager.getConfig("defaultColor");

                            int totalChecks = IolhEssentials.helpopPexConfigManager.getAllKeys().size();
                            AtomicInteger finishedChecks = new AtomicInteger(0);

                            for (String key : IolhEssentials.helpopPexConfigManager.getAllKeys()) {
                                PermissionUtils.isUser(Objects.requireNonNull(source.getPlayer()).getUUID(), key, isUser -> {
                                    if (isUser) {
                                        nicknameColorCode[0] = IolhEssentials.helpopPexConfigManager.getConfig(key);
                                    }
                                    // Увеличиваем счетчик завершенных проверок
                                    if (finishedChecks.incrementAndGet() == totalChecks) {
                                        // Все проверки завершены, теперь можно отобразить сообщение
                                        String resultMessage = timeInMessage + socialspyPrefix + " " + "§" + nicknameColorCode[0] + source.getTextName() + "" + "§f: " + "§f/" + command;
                                        Timer timer = new Timer();
                                        timer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                targetPlayer.displayClientMessage(Component.literal(resultMessage), false);
                                            }
                                        }, 350);
                                    }
                                });
                            }
//                        }
                    }
                }
            }
        }
    }
}
