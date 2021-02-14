package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;

public class WorldUnload implements AdminSubCommand {

    public final VSkyblock plugin;

    public WorldUnload(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender.hasPermission("VSkyblock.UnloadWorld")) {
            if (plugin.getWorldManager().getAllWorlds().contains(args)) {
                if (plugin.getWorldManager().getLoadedWorlds().contains(args)) {
                    if (plugin.getWorldManager().unloadWorld(args)) {
                        ConfigShorts.messagefromString("WorldUnloaded", sender);
                    } else {
                        ConfigShorts.custommessagefromString("WorldFailedToUnload", sender, args);
                    }
                } else {
                    ConfigShorts.custommessagefromString("NoLoadedWorldFound", sender, args);
                }
            } else {
                ConfigShorts.custommessagefromString("NoWorldFound", sender, args);
            }
        } else {
            ConfigShorts.messagefromString("PermissionLack", sender);
        }
    }
}
