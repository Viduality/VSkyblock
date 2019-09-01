package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.entity.Player;

public class WorldSetSpawnpoint implements AdminSubCommand {

    private WorldManager wm = new WorldManager();


    @Override
    public void execute(Player player, String args, String option1, String option2) {
        if (player.hasPermission("VSkyblock.SetWorldSpawnpoint")) {
            if (wm.getLoadedWorlds().contains(player.getWorld().getName())) {
                wm.setSpawnLocation(player.getLocation());
                ConfigShorts.custommessagefromString("SetNewSpawnpoint", player, player.getWorld().getName());
            } else {
                ConfigShorts.custommessagefromString("NoWorldFound", player, player.getWorld().getName());
            }
        } else {
            ConfigShorts.messagefromString("PermissionLack", player);
        }
    }
}
