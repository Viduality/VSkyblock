package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractBlocker implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();


    @EventHandler
    public void interactEvent(PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();
        if (player.getWorld().getEnvironment() != World.Environment.NETHER
                && !player.getWorld().getName().equals(plugin.getConfig().getString("SpawnWorld"))
                && !player.getWorld().getName().equals(Island.playerislands.get(player.getUniqueId().toString()))
                && !player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (playerInteractEvent.getClickedBlock() != null
                    && playerInteractEvent.getClickedBlock().getType().isInteractable()) {
                playerInteractEvent.setCancelled(true);
                ConfigShorts.messagefromString("InteractBlocker", player);
            } else if (playerInteractEvent.getAction() == Action.PHYSICAL
                    || playerInteractEvent.getItem() == null
                    || !playerInteractEvent.getItem().getType().isEdible()) {
                playerInteractEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void interactEntityEvent(PlayerInteractEntityEvent playerInteractEntityEvent) {
        handle(playerInteractEntityEvent);
    }

    @EventHandler
    public void interactatEntityEvent(PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
        handle(playerInteractAtEntityEvent);
    }

    private void handle(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() != World.Environment.NETHER
                && !player.getWorld().getName().equals(Island.playerislands.get(player.getUniqueId().toString()))
                && !player.hasPermission("VSkyblock.IgnoreProtected")) {
            event.setCancelled(true);
        }
    }
}
