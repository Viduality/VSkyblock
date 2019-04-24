package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class IslandSethome implements SubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();


    @Override
    public void execute(DatabaseCache databaseCache) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Player player = databaseCache.getPlayer();
                if (databaseCache.isIslandowner()) {
                    String island = databaseCache.getIslandname();
                    if (island.equals(player.getWorld().getName())) {
                        if (!(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType()).equals(Material.AIR)) {
                            plugin.getMV().getCore().getMVWorldManager().getMVWorld(island).setSpawnLocation(player.getLocation());
                            ConfigShorts.messagefromString("SethomeSuccess", player);
                        } else {
                            ConfigShorts.messagefromString("MidAir", player);
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
