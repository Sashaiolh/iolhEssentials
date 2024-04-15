package org.sashaiolh.iolhessentials;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PermissionUtils {
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
}
