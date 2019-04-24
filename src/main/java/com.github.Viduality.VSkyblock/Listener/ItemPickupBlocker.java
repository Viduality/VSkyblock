package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class ItemPickupBlocker implements Listener {


    @EventHandler
    public void itemPickupBlocker(PlayerPickupItemEvent playerPickupItemEvent) {
        Player player = playerPickupItemEvent.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (!player.hasPermission("VSkyIgnoreProtected")) {
            if (!(player.getWorld().getName().equals(Island.playerislands.get(uuid))) || player.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                playerPickupItemEvent.setCancelled(true);
            }
        }
    }
}
