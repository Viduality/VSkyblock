package com.github.Viduality.VSkyblock.Listener;

import com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class PhantomSpawn implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();

    @EventHandler
    public void onPhantomSpawn(CreatureSpawnEvent creatureSpawnEvent) {
        if (creatureSpawnEvent.getEntityType().equals(EntityType.PHANTOM)) {
            Phantom phantom = (Phantom) creatureSpawnEvent.getEntity();
            if (phantom.getSpawningEntity() != null) {
                Player player = plugin.getServer().getPlayer(phantom.getSpawningEntity());
                if (player != null && player.getBedSpawnLocation() == null) {
                    creatureSpawnEvent.setCancelled(true);
                }
            }
        }
    }
}
