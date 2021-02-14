package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;

public class IslandLeave implements SubCommand{

    private final VSkyblock plugin;

    public IslandLeave(VSkyblock plugin) {
        this.plugin = plugin;
    }


    @Override
    public void execute(DatabaseCache databaseCache) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Player player = databaseCache.getPlayer();
            if (databaseCache.getIslandId() != 0) {
                if (databaseCache.isIslandowner()) {
                    plugin.getDb().getReader().hasislandmembers(databaseCache.getIslandId(), result -> {
                        if (!result) {
                            Island.leavemap.put(databaseCache.getUuid(), 1);
                            ConfigShorts.messagefromString("AcceptLeave", player);
                        } else {
                            ConfigShorts.messagefromString("HasIslandMembers", player);
                        }
                    });
                } else {
                    Island.leavemap.put(databaseCache.getUuid(), 1);
                    ConfigShorts.messagefromString("AcceptLeave", player);
                }
            } else {
                ConfigShorts.messagefromString("NoIsland", databaseCache.getPlayer());
            }
        });
    }
}
