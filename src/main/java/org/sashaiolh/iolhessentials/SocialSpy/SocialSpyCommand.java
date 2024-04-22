package org.sashaiolh.iolhessentials.SocialSpy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.List;

public class SocialSpyCommand {
    // SuggestionProvider для автодополнения по никнеймам игроков
    private static final SuggestionProvider<CommandSourceStack> PLAYER_SUGGESTIONS = (context, builder) -> {
        List<ServerPlayer> players = context.getSource().getServer().getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            builder.suggest(player.getName().getString());
        }
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("socialspy")
                        .requires(source -> source.hasPermission(4))
                        .executes(context -> socialspy(context, null))  // Без аргумента - на себя
                        .then(
                                Commands.argument("nickname", StringArgumentType.string())  // Аргумент для ввода никнейма
                                        .suggests(PLAYER_SUGGESTIONS)  // Автодополнение по никнеймам
                                        .executes(context -> {
                                            String nickname = StringArgumentType.getString(context, "nickname");
                                            return socialspy(context, nickname);
                                        })
                        )
        );
    }

    private static int socialspy(CommandContext<CommandSourceStack> context, String nickname) throws CommandSyntaxException {
        ServerPlayer sourcePlayer = context.getSource().getPlayerOrException();
        try {
            ServerPlayer targetPlayer;
            if (nickname == null) {  // Если никнейм не указан, используем исполнителя
                targetPlayer = context.getSource().getPlayerOrException();
            } else {  // Иначе ищем игрока по никнейму
                targetPlayer = context.getSource().getServer().getPlayerList().getPlayerByName(nickname);
            }

            String messageToSource = "§a";

            String nicknameToSocialSpy = "";

            if (!(targetPlayer == null)) {
                 nicknameToSocialSpy = targetPlayer.getName().getString();
            }
            else {
                if(!(nickname==null)) nicknameToSocialSpy = nickname;
            }


            if(!SocialSpyUsersManager.isUserInConfig(nicknameToSocialSpy)) {
                SocialSpyUsersManager.addSocialSpyUser(nicknameToSocialSpy);
                messageToSource = "§aSocialSpy включен для " + nicknameToSocialSpy;
            }
            else {
                SocialSpyUsersManager.removeSocialSpyUser(nicknameToSocialSpy);
                messageToSource = "§aSocialSpy §cвыключен §aдля " + nicknameToSocialSpy;
            }

            sendMessage(sourcePlayer, messageToSource);

            if (!(targetPlayer == null)) {

            }
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("An error occurred: " + e.getMessage()));
            return 0;
        }
    }

    private static void sendMessage(ServerPlayer player, String message) {
        player.displayClientMessage(Component.literal(message), false);
    }
}
