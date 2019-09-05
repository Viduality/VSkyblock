package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.entity.Player;

public class WorldTeleportation implements AdminSubCommand {

    private WorldManager wm = new WorldManager();


    @Override
    public void execute(Player player, String args, String option1, String option2) {
        if (player.hasPermission("VSkyblock.Teleportation")) {
            if (wm.getLoadedWorlds().contains(args)) {
                if (player.teleport(wm.getSpawnLocation(args))) {
                    ConfigShorts.custommessagefromString("TeleportedToWorld", player, args);
                } else {
                    ConfigShorts.custommessagefromString("CouldNotTeleportToWorld", player, args);
                }
            } else {
                ConfigShorts.custommessagefromString("NoLoadedWorldFound", player, args);
            }
        } else {
            ConfigShorts.messagefromString("PermissionLack", player);
        }
    }
}
