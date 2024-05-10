package org.sashaiolh.iolhessentials.Commands.Aliases;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.concurrent.CompletableFuture;

public class AliasCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("addAlias")
                .requires(x -> x.hasPermission(4))
                .then(Commands.argument("alias", StringArgumentType.string())
                                .then(Commands.argument("cmd", StringArgumentType.string())
                                        .suggests(COMMAND_LIST_SUGGESTIONS_PROVIDER)
                                        .executes(ctx -> newAlias(ctx, StringArgumentType.getString(ctx, "alias"), StringArgumentType.getString(ctx, "cmd")))));
    }

    private static int newAlias(CommandContext<CommandSourceStack> ctx, String alias, String cmd) {
        AliasRegistry.addAliases(alias, cmd);
        return 1;
    }

    private static final SuggestionProvider<CommandSourceStack> COMMAND_LIST_SUGGESTIONS_PROVIDER =
        AliasCommand::commandListSuggestions;

    private static CompletableFuture<Suggestions> commandListSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        StringBuilder commandSuggest = new StringBuilder(builder.getRemaining().toLowerCase());
        if (commandSuggest.isEmpty()) {
            ctx.getSource().getServer().getCommands().getDispatcher().getRoot().getChildren().forEach(command -> {
                builder.suggest(command.getName().toLowerCase());
            });
        } else {
            ctx.getSource().getServer().getCommands().getDispatcher().getRoot().getChildren().forEach(command -> {
                if (command.getName().toLowerCase().startsWith(commandSuggest.toString())) {
                    builder.suggest(command.getName().toLowerCase());
                }
            });
        }
        return builder.buildFuture();
    }
}