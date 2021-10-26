package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Utilitys.IslandCacheHandler;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemDropBlocker implements Listener {


    @EventHandler
    public void itemDropBlocker(PlayerDropItemEvent playerDropItemEvent) {
        Player player = playerDropItemEvent.getPlayer();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (!(player.getWorld().getName().equals(IslandCacheHandler.playerislands.get(player.getUniqueId())) || player.getWorld().getEnvironment().equals(World.Environment.NETHER))) {
                playerDropItemEvent.setCancelled(true);
                ConfigShorts.messagefromString("DropItem", player);
            }
        }
    }
}
