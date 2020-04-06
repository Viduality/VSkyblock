package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractBlocker implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();


    @EventHandler
    public void interactEvent(PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (Island.playerislands.get(player.getUniqueId().toString()) != null) {
                if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER) && !Island.playerislands.get(player.getUniqueId().toString()).equals(player.getWorld().getName())) {
                    if (playerInteractEvent.getClickedBlock() != null) {
                        if (playerInteractEvent.getClickedBlock().getType().isInteractable()) {
                            if (!player.getWorld().getName().equals(plugin.getConfig().getString("SpawnWorld"))) {
                                playerInteractEvent.setCancelled(true);
                                ConfigShorts.messagefromString("InteractBlocker", player);
                            }
                        }
                    }
                    if (playerInteractEvent.getAction() == Action.PHYSICAL) {
                        if (!player.getWorld().getName().equals(plugin.getConfig().getString("SpawnWorld"))) {
                            playerInteractEvent.setCancelled(true);
                        }
                    }
                }
            } else {
                if (!player.getWorld().getName().equals(plugin.getConfig().getString("SpawnWorld"))) {
                    playerInteractEvent.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void playerItemUse(PlayerInteractEvent interactEvent) {
        Player player = interactEvent.getPlayer();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (!player.getWorld().getName().equals(Island.playerislands.get(player.getUniqueId().toString())) && !player.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                if (player.getActiveItem() != null) {
                    if (!player.getActiveItem().getType().isEdible()) {
                        interactEvent.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void interactEntityEvent(PlayerInteractEntityEvent playerInteractEntityEvent) {
        Player player = playerInteractEntityEvent.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (Island.playerislands.get(uuid) != null) {
                if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER) && !Island.playerislands.get(uuid).equals(player.getWorld().getName())) {
                    playerInteractEntityEvent.setCancelled(true);
                }
            } else {
                playerInteractEntityEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void interactatEntityEvent(PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
        Player player = playerInteractAtEntityEvent.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (Island.playerislands.get(uuid) != null) {
                if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER) && !Island.playerislands.get(uuid).equals(player.getWorld().getName())) {
                    playerInteractAtEntityEvent.setCancelled(true);
                }
            } else {
                playerInteractAtEntityEvent.setCancelled(true);
            }
        }
    }
}
