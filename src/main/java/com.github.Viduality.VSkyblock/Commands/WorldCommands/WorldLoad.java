package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldLoad implements AdminSubCommand {

    private WorldManager wm = new WorldManager();

    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender.hasPermission("VSkyblock.LoadWorld")) {
            if (wm.getAllWorlds().contains(args)) {
                if (wm.getUnloadedWorlds().contains(args)) {
                    if (wm.loadWorld(args)) {
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
