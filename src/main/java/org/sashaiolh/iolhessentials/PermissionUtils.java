package org.sashaiolh.iolhessentials;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class PermissionUtils {
    public static LuckPerms luckPerms;
    public static boolean hasPermission(User user, String permission) {
        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }
    public static void isUser(UUID who, String pex, Consumer<Boolean> callback) {
        LuckPerms luckPerms = LuckPermsProvider.get();

        luckPerms.getUserManager().loadUser(who).thenAccept(user -> {
            Collection<Group> inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
            boolean isUser = inheritedGroups.stream().anyMatch(g -> g.getName().equals(pex));
            callback.accept(isUser);
        }).exceptionally(throwable -> {
            // Обработка ошибки, если что-то пошло не так при загрузке пользователя
            throwable.printStackTrace();
            callback.accept(false); // Возвращаем false в случае ошибки
            return null;
        });
    }
    public static List<ServerPlayer> getPlayersWithPermission(LuckPerms luckPerms, MinecraftServer server, String permission) {
        List<ServerPlayer> playersWithPermission = new ArrayList<>();

        for (User user : luckPerms.getUserManager().getLoadedUsers()) {
            UUID uuid = user.getUniqueId();
            QueryOptions queryOptions = luckPerms.getContextManager().getQueryOptions(user).orElse(QueryOptions.defaultContextualOptions());

            if (user.getCachedData().getPermissionData(queryOptions).checkPermission(permission).asBoolean()) {
                ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                if (player != null) {
                    playersWithPermission.add(player);
                }
            }
        }

        return playersWithPermission;
    }



//    public static void sendMessageToPex(String pex, String message, String format){
//        luckPerms = LuckPermsProvider.get();
//        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
//        List<ServerPlayer> playersWithPermission = PermissionUtils.getPlayersWithPermission(luckPerms, server, "command.helpop");
//        for (ServerPlayer player : playersWithPermission) {
//            LocalTime timeNow = LocalTime.now();
//            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//            String formattedTime = timeNow.format(timeFormatter);
//            String timeInMessage = "§8[" + "§7" + formattedTime + "§8] ";
//            String helpopPrefix = IolhEssentials.configManager.getConfig("helpopPrefix");
//
//
//            final String[] nicknameColorCode = {""};
//            nicknameColorCode[0] = IolhEssentials.helpopPexConfigManager.getConfig("defaultColor");
//
//            int totalChecks = IolhEssentials.helpopPexConfigManager.getAllKeys().size();
//            AtomicInteger finishedChecks = new AtomicInteger(0);
//
//            for (String key : IolhEssentials.helpopPexConfigManager.getAllKeys()) {
//                PermissionUtils.isUser(player.getUUID(), key, isUser -> {
//                    if (isUser) {
//                        System.out.println("123");
//                        nicknameColorCode[0] = IolhEssentials.helpopPexConfigManager.getConfig(key);
//                        System.out.println(IolhEssentials.helpopPexConfigManager.getConfig(key));
//                        System.out.println(nicknameColorCode[0]);
//                    }
//                    // Увеличиваем счетчик завершенных проверок
//                    if (finishedChecks.incrementAndGet() == totalChecks) {
//                        // Все проверки завершены, теперь можно отобразить сообщение
////                        String resultMessage = timeInMessage + helpopPrefix + "§" + nicknameColorCode[0] + " " + player.getName().getString() + "§8: §f" + message;
//                        String resultMessage = format.toString()
//                                .replaceAll("<timeInMessage>",timeInMessage)
//                                .replaceAll("<helpopPrefix>", helpopPrefix)
//                                .replaceAll("<nicknameColor>", nicknameColorCode[0])
//                                .replaceAll("<playerName>", player.getName().getString())
//                                .replaceAll("<message>", message);
//                        player.displayClientMessage(Component.literal(resultMessage), false);
//                    }
//                });
//            }
//
//
//        }
//    }



}
