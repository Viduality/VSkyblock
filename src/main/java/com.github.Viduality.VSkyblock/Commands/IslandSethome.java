package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class IslandSethome implements SubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private WorldManager wm = new WorldManager();
    private DatabaseWriter databaseWriter = new DatabaseWriter();


    @Override
    public void execute(DatabaseCache databaseCache) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Player player = databaseCache.getPlayer();
                if (databaseCache.isIslandowner()) {
                    String island = databaseCache.getIslandname();
                    if (island.equals(player.getWorld().getName())) {
                        if (player.getFallDistance() == 0) {
                            Location loc = player.getLocation();
                            loc.setY(Math.ceil(loc.getY()));
                            wm.setSpawnLocation(loc);
                            databaseWriter.setIslandSpawn(databaseCache.getIslandId(), loc);
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
            }
        });
    }
}
