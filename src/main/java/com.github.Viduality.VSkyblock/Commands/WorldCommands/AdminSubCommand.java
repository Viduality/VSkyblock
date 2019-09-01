package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import org.bukkit.entity.Player;

public interface AdminSubCommand {
    void execute(Player player, String args, String option1, String option2);
}
