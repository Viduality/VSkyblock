package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class IslandVisit implements SubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();


    @Override
    public void execute(DatabaseCache databaseCache) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Player player = databaseCache.getPlayer();
                OfflinePlayer target = plugin.getServer().getOfflinePlayer(databaseCache.getArg());
                if (player != target) {
                    if (target.isOnline()) {
                        Player onlinetarget = plugin.getServer().getPlayer(databaseCache.getArg());
                        String uuid = onlinetarget.getUniqueId().toString();
                        databaseReader.getislandidfromplayer(uuid, new DatabaseReader.CallbackINT() {
                            @Override
                            public void onQueryDone(int result) {
                                int islandid = result;
                                databaseReader.getIslandMembers(result, new DatabaseReader.CallbackList() {
                                    @Override
                                    public void onQueryDone(List<String> result) {
                                        if (!result.contains(player.getName())) {
                                            databaseReader.isislandvisitable(islandid, new DatabaseReader.CallbackBoolean() {
                                                @Override
                                                public void onQueryDone(boolean result) {
                                                    if (result) {
                                                        player.teleport(onlinetarget);
                                                    } else {
                                                        ConfigShorts.messagefromString("CannotVisitIsland", player);
                                                    }
                                                }
                                            });
                                        } else {
                                            ConfigShorts.messagefromString("VisitYourself", player);
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        ConfigShorts.custommessagefromString("PlayerNotOnline", player, player.getName(), databaseCache.getArg());
                    }
                } else {
                    ConfigShorts.messagefromString("VisitYourself", player);
                }
            }
        });
    }
}
