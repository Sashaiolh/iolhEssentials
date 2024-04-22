package org.sashaiolh.iolhessentials.SocialSpy.Utils;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class PlayerUtils {
    public static ServerPlayer getPlayerByUUID(MinecraftServer server, UUID playerUUID) {
        return server.getPlayerList().getPlayer(playerUUID);
    }
}
