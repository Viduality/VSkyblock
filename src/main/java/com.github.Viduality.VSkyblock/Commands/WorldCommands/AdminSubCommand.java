package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import org.bukkit.command.CommandSender;

public interface AdminSubCommand {
    void execute(CommandSender sender, String args, String option1, String option2);
}
