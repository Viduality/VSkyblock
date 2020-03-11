package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractBlocker implements Listener {



    @EventHandler
    public void interactEvent(PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER) && !Island.playerislands.get(player.getUniqueId().toString()).equals(player.getWorld().getName())) {
                if (playerInteractEvent.getClickedBlock() != null) {
                    if (playerInteractEvent.getClickedBlock().getType().isInteractable()) {
                        playerInteractEvent.setCancelled(true);
                        ConfigShorts.messagefromString("InteractBlocker", player);
                    }
                }
                if (playerInteractEvent.getAction() == Action.PHYSICAL) {
                    playerInteractEvent.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void interactEntityEvent(PlayerInteractEntityEvent playerInteractEntityEvent) {
        Player player = playerInteractEntityEvent.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER) && !Island.playerislands.get(uuid).equals(player.getWorld().getName())) {
                playerInteractEntityEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void interactatEntityEvent(PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
        Player player = playerInteractAtEntityEvent.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER) && !Island.playerislands.get(uuid).equals(player.getWorld().getName())) {
                playerInteractAtEntityEvent.setCancelled(true);
            }
        }
    }
}
