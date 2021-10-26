package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SubCommand implements TabExecutor {

    protected VSkyblock plugin;
    private final String name;
    private final String[] aliases;
    private Map<String, SubCommand> subCommands = new LinkedHashMap<>();

    protected SubCommand(VSkyblock plugin, String name, String... aliases) {
        this.plugin = plugin;
        this.name = name;
        this.aliases = aliases;
    }

    abstract boolean execute(CommandSender sender, String... args);

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            SubCommand subCommand = getSubCommand(args[0]);
            if (subCommand != null) {
                return subCommand.onCommand(sender, cmd, args[0], Arrays.copyOfRange(args, 1, args.length));
            }
        }
        if (!execute(sender, args)) {
            ConfigShorts.messagefromString("FalseInput", sender);
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            return subCommands.keySet().stream()
                    .sorted(String::compareToIgnoreCase)
                    .collect(Collectors.toList());
        }

        SubCommand subCommand = getSubCommand(args[0]);
        if (subCommand != null) {
            return subCommand.onTabComplete(sender, command, args[0], Arrays.copyOfRange(args, 1, args.length));
        } else if (args.length == 1) {
            return subCommands.keySet().stream()
                    .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(args[0]))
                    .sorted(String::compareToIgnoreCase)
                    .collect(Collectors.toList());
        }
        return null;
    }

    public void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.name.toLowerCase(Locale.ROOT), subCommand);
        for (String alias : subCommand.aliases) {
            subCommands.putIfAbsent(alias.toLowerCase(Locale.ROOT), subCommand);
        }
    }

    private SubCommand getSubCommand(String name) {
        return subCommands.get(name.toLowerCase(Locale.ROOT));
    }
}
