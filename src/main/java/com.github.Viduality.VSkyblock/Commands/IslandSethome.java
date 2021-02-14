package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class IslandSethome implements SubCommand {

    private final VSkyblock plugin;

    public IslandSethome(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(DatabaseCache databaseCache) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Player player = databaseCache.getPlayer();
            if (databaseCache.isIslandowner()) {
                String island = databaseCache.getIslandname();
                if (island.equals(player.getWorld().getName())) {
                    if (player.getFallDistance() == 0) {
                        Location loc = player.getLocation();
                        loc.setY(Math.ceil(loc.getY()));
                        plugin.getWorldManager().setSpawnLocation(loc);
                        plugin.getDb().getWriter().setIslandSpawn(databaseCache.getIslandId(), loc);
                        Island.islandhomes.put(databaseCache.getIslandname(), loc);
                        ConfigShorts.messagefromString("SethomeSuccess", player);
                    } else {
                        ConfigShorts.messagefromString("PlayerFalling", player);
                    }
                } else {
                    ConfigShorts.messagefromString("NotAtPlayersIsland", player);
                }
            } else {
                ConfigShorts.messagefromString("NotIslandOwner", player);
            }
        });
    }
}
