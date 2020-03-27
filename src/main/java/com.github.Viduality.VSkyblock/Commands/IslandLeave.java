package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IslandLeave implements SubCommand{

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();


    @Override
    public void execute(DatabaseCache databaseCache) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Player player = databaseCache.getPlayer();
                if (databaseCache.getIslandId() != 0) {
                    if (databaseCache.isIslandowner()) {
                        databaseReader.hasislandmembers(databaseCache.getIslandId(), new DatabaseReader.CallbackBoolean() {
                            @Override
                            public void onQueryDone(boolean result) {
                                if (!result) {
                                    Island.leavemap.put(UUID.fromString(databaseCache.getuuid()), 1);
                                    ConfigShorts.messagefromString("AcceptLeave", player);
                                } else {
                                    ConfigShorts.messagefromString("HasIslandMembers", player);
                                }
                            }
                        });
                    } else {
                        Island.leavemap.put(UUID.fromString(databaseCache.getuuid()), 1);
                        ConfigShorts.messagefromString("AcceptLeave", player);
                    }
                } else {
                    ConfigShorts.messagefromString("NoIsland", databaseCache.getPlayer());
                }
            }
        });
    }
}
