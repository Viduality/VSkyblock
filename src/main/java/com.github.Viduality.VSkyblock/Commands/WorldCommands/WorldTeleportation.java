package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;

public class WorldTeleportation implements AdminSubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private WorldManager wm = new WorldManager();

    private VSkyblock worldTeleportation;


    @Override
    public void execute(Player player, String args) {
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
