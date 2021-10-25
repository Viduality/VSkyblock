package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class IslandRestart implements SubCommand {

    private final VSkyblock plugin;

    public IslandRestart(VSkyblock plugin) {
        this.plugin = plugin;
    }


    @Override
    public void execute(DatabaseCache databaseCache) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Player player = plugin.getServer().getPlayer(databaseCache.getPlayer().getUniqueId());
            if (player != null) {
                if (databaseCache.isIslandowner()) {
                    plugin.getDb().getReader().hasIslandMembers(databaseCache.getIslandId(), hasislandmembers -> {
                        if (!hasislandmembers) {
                            Island.restartmap.put(player.getUniqueId(), 1);
                            ConfigShorts.messagefromString("ConfirmRestart", player);
                        } else {
                            ConfigShorts.messagefromString("HasIslandMembers", player);
                        }
                    });
                } else {
                    ConfigShorts.messagefromString("NotIslandOwner", player);
                }
            }
        });
    }
}
