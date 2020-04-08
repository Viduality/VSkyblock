package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class ItemPickupBlocker implements Listener {


    @EventHandler
    public void itemPickupBlocker(EntityPickupItemEvent entityPickupItemEvent) {
        EntityType e = entityPickupItemEvent.getEntityType();
        if (e.equals(EntityType.PLAYER)) {
            Player player = (Player) entityPickupItemEvent.getEntity();
            if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
                if (!(player.getWorld().getName().equals(Island.playerislands.get(player.getUniqueId())) || player.getWorld().getEnvironment().equals(World.Environment.NETHER))) {
                    entityPickupItemEvent.setCancelled(true);
                }
            }
        }

    }
}
