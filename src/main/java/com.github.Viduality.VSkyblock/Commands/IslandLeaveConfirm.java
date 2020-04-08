package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;

public class IslandLeaveConfirm implements SubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseWriter databaseWriter = new DatabaseWriter();
    private WorldManager wm = new WorldManager();


    @Override
    public void execute(DatabaseCache databaseCache) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Player player = databaseCache.getPlayer();
                if (Island.leavemap.asMap().containsKey(player.getUniqueId())) {
                    databaseWriter.leavefromIsland(player.getUniqueId());
                    databaseWriter.resetChallenges(player.getUniqueId());
                    ConfigShorts.messagefromString("LeftIsland", player);
                    player.getInventory().clear();
                    player.getEnderChest().clear();
                    Island.leavemap.asMap().remove(player.getUniqueId());
                    Island.playerislands.remove(player.getUniqueId());
                    if (!Island.playerislands.containsValue(databaseCache.getIslandname())) {
                        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                wm.unloadWorld(databaseCache.getIslandname());
                            }
                        });
                    }
                } else {
                    ConfigShorts.messagefromString("LeaveFirst", player);
                }
            }
        });
    }
}
