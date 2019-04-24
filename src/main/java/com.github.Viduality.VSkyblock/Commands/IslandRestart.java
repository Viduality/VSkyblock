package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class IslandRestart implements SubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();


    @Override
    public void execute(DatabaseCache databaseCache) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Player player = plugin.getServer().getPlayer(databaseCache.getPlayer().getUniqueId());
                if (databaseCache.isIslandowner()) {
                    databaseReader.hasislandmembers(databaseCache.getIslandId(), new DatabaseReader.CallbackBoolean() {
                        @Override
                        public void onQueryDone(boolean hasislandmembers) {
                            if (!hasislandmembers) {
                                Island.restartmap.put(player.getUniqueId(), 1);
                                ConfigShorts.messagefromString("ConfirmRestart", player);
                            } else {
                                ConfigShorts.messagefromString("HasIslandMembers", player);
                            }
                        }
                    });
                } else {
                    ConfigShorts.messagefromString("NotIslandOwner", player);
                }
            }
        });
    }
}
