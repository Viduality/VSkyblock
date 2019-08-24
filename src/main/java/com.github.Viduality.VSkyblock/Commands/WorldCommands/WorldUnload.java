package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;

public class WorldUnload implements AdminSubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private WorldManager wm = new WorldManager();

    @Override
    public void execute(Player player, String args) {
        if (player.hasPermission("VSkyblock.UnloadWorld")) {
            if (wm.getAllWorlds().contains(args)) {
                if (wm.getLoadedWorlds().contains(args)) {
                    if (wm.unloadWorld(args)) {
                        ConfigShorts.messagefromString("WorldUnloaded", player);
                    } else {
                        ConfigShorts.custommessagefromString("WorldFailedToUnload", player, args);
                    }
                } else {
                    ConfigShorts.messagefromString("NoLoadedWorldFound", player);
                }
            } else {
                ConfigShorts.custommessagefromString("NoWorldFound", player, args);
            }
        } else {
            ConfigShorts.messagefromString("PermissionLack", player);
        }
    }
}
