package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.Commands.WorldCommands.AdminSubCommand;
import com.github.Viduality.VSkyblock.DefaultFiles;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import org.bukkit.command.CommandSender;

public class ReloadBlockValues implements AdminSubCommand {
    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender.hasPermission("VSkyblock.ReloadBlockValues")) {
            DefaultFiles.reloadValues();
            ConfigShorts.messagefromString("ReloadedBlockValues", sender);
        } else {
            ConfigShorts.messagefromString("PermissionLack", sender);
        }
    }
}
