package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;

public class WorldLoad implements AdminSubCommand {

    private final VSkyblock plugin;

    public WorldLoad(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender.hasPermission("VSkyblock.LoadWorld")) {
            if (plugin.getWorldManager().getAllWorlds().contains(args)) {
                if (plugin.getWorldManager().getUnloadedWorlds().contains(args)) {
                    if (plugin.getWorldManager().loadWorld(args)) {
                        ConfigShorts.messagefromString("WorldLoaded", sender);
                    } else {
                        ConfigShorts.custommessagefromString("WorldFailedToLoad", sender, args);
                    }
                } else {
                    ConfigShorts.messagefromString("WorldAlreadyLoaded", sender);
                }
            } else {
                ConfigShorts.custommessagefromString("NoWorldFound", sender, args);
            }
        } else {
            ConfigShorts.messagefromString("PermissionLack", sender);
        }
    }
}
