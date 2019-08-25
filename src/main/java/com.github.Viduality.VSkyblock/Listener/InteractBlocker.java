package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractBlocker implements Listener {



    @EventHandler
    public void interactEvent(PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (!player.getWorld().getName().equals(Island.playerislands.get(uuid))) {
                if (playerInteractEvent.getClickedBlock() != null) {
                    if (playerInteractEvent.getClickedBlock().getType().isInteractable()) {
                        playerInteractEvent.setCancelled(true);
                        ConfigShorts.messagefromString("InteractBlocker", player);
                    }
                }
            }
        }
    }
}
