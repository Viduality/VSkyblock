package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldUnload implements AdminSubCommand {

    private WorldManager wm = new WorldManager();

    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender.hasPermission("VSkyblock.UnloadWorld")) {
            if (wm.getAllWorlds().contains(args)) {
                if (wm.getLoadedWorlds().contains(args)) {
                    if (wm.unloadWorld(args)) {
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
