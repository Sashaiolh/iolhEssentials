package org.sashaiolh.iolhessentials.Commands.Aliases;

import com.electronwill.nightconfig.core.utils.StringUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class AliasRegistry {
    private static final Map<String, String> aliases = new HashMap<>();
    private static final File file = new File("config/commandaliases.txt");

    public static int runAlias(CommandContext<CommandSourceStack> context) {
        String input = context.getInput();
        String command = input.substring(input.indexOf(context.getNodes().get(0).getNode().getName()));
        String commandToRun = getCommandWithArguments(command);
        context.getSource().getServer().getCommands().performPrefixedCommand(context.getSource(), commandToRun);
        return 1;
    }

    private static String getCommandWithArguments(String command) {
        List<String> nodes = StringUtils.split(command, ' ');
        String test = "";

        //Iterate until the command matches alias, this allows for postfixing
        for (Iterator<String> iterator = nodes.iterator(); iterator.hasNext(); ) {
            test = test + " " + iterator.next();
            test = test.trim();
            iterator.remove();
            if (aliases.containsKey(test)) {
                break;
            }
        }

        // replace %note% with next node
        String com = aliases.get(test);
        while (com.contains("%")) {
            int start = com.indexOf("%");
            int end = com.indexOf("%", start + 1);
            String begin = com.substring(0, start);
            String ending = com.substring(end + 1);
            if (!nodes.isEmpty()) {
                com = begin + nodes.get(0) + ending;
                nodes.remove(0);
            } else {
                break;
            }
        }
        //append remaining nodes
        StringBuilder args = new StringBuilder();
        for (String node : nodes) {
            args.append(" ").append(node);
        }
        return com + args;
    }

    public static void registerAliases(CommandDispatcher<CommandSourceStack> dispatcher) {
        readAliases();
        aliases.forEach((key, value) -> {
            try {
                dispatcher.register(
                        Commands.literal(key)
                                .redirect(dispatcher.findNode(Arrays.asList(value.split("\\s+"))))
                );
            } catch (Exception e) {
                System.out.println("Error registering aliases: " + e.getMessage());
            }
        });
    }


    public static void addAliases(String name, String cmd) {
        aliases.put(name, cmd);
        writeAliases();
    }
    private static void writeAliases() {
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        aliases.forEach((k, v) -> {
            String toWrite = k + " | " + v + System.lineSeparator();
            try {
                Files.write(file.toPath(), toWrite.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void readAliases() {
        List<String> lines = new ArrayList<>();
        if (file.exists()) {
            try {
                lines = Files.readAllLines(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        aliases.clear();
        for (String line : lines) {
            List<String> strings = StringUtils.split(line, '|');
            for (int i = 0; i < strings.size(); i++) {
                strings.set(i, strings.get(i).trim());
            }
            aliases.put(strings.get(0), strings.get(1));
        }
    }


}