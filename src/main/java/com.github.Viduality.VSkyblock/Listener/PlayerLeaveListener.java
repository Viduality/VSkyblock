package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Utilitys.IslandCacheHandler;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    private final VSkyblock plugin;

    public PlayerLeaveListener(VSkyblock plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();
        Location loc = player.getLocation();
        if (IslandCacheHandler.playerislands.containsKey(player.getUniqueId())) {
            String island = IslandCacheHandler.playerislands.get(player.getUniqueId());
            IslandCacheHandler.playerislands.remove(player.getUniqueId());
            if (!IslandCacheHandler.playerislands.containsValue(island)) {
                if (!plugin.getWorldManager().getAutoLoad(island)) {
                    IslandCacheHandler.emptyloadedislands.put(island, plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        plugin.getWorldManager().unloadWorld(island);
                        IslandCacheHandler.islandhomes.remove(island);
                    }, 20 * 60));
                }
            }
        }
        if (plugin.scoreboardmanager.doesobjectiveexist("deaths")) {
            if (plugin.scoreboardmanager.hasPlayerScore(player.getName(), "deaths")) {
                int currentcount = plugin.scoreboardmanager.getPlayerScore(player.getName(), "deaths");
                plugin.getDb().getWriter().updateDeathCount(player.getUniqueId(), currentcount);
            }
        }
        saveLocation(player, loc);
    }


    private void saveLocation(Player player, Location loc) {
        plugin.getDb().getWriter().savelastLocation(player.getUniqueId(), loc);
    }
}
