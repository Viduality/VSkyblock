package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class IslandVisit implements SubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private WorldManager wm = new WorldManager();


    @Override
    public void execute(DatabaseCache databaseCache) {
        Player player = databaseCache.getPlayer();
        Player onlinetarget = plugin.getServer().getPlayer(databaseCache.getArg());
        if (player != onlinetarget) {
            if (onlinetarget != null) {
                UUID uuid = onlinetarget.getUniqueId();
                databaseReader.getislandidfromplayer(uuid, islandId -> databaseReader.getIslandMembers(islandId, islandMembers -> {
                    if (!islandMembers.contains(player.getName())) {
                        databaseReader.isislandvisitable(islandId, isVisitable -> {
                            if (isVisitable) {
                                databaseReader.getislandnamefromplayer(uuid, islandName -> {
                                    if (wm.getLoadedWorlds().contains(islandName)) {
                                        Location islandHome = Island.islandhomes.get(islandName);
                                        if (islandHome != null) {
                                            islandHome.getWorld().getChunkAtAsync(islandHome).whenComplete((c, e) -> {
                                                if (e != null) {
                                                    e.printStackTrace();
                                                }
                                                if (c != null) {
                                                    player.teleport(islandHome);
                                                    player.setCanCollide(false);
                                                    for (String memberName : islandMembers) {
                                                        Player onlinePlayer = plugin.getServer().getPlayer(memberName);
                                                        if (onlinePlayer != null) {
                                                            ConfigShorts.custommessagefromString("PlayerVisitingYourIsland", onlinePlayer, player.getName());
                                                        }
                                                    }
                                                } else {
                                                    ConfigShorts.messagefromString("IslandSpawnNotSafe", player);
                                                }
                                            });
                                        }
                                    } else {
                                        ConfigShorts.messagefromString("IslandSpawnNotSafe", player);
                                    }
                                });
                            } else {
                                ConfigShorts.messagefromString("CannotVisitIsland", player);
                            }
                        });
                    } else {
                        ConfigShorts.messagefromString("VisitYourself", player);
                    }
                }));
            } else {
                ConfigShorts.custommessagefromString("PlayerNotOnline", player, player.getName(), databaseCache.getArg());
            }
        } else {
            ConfigShorts.messagefromString("VisitYourself", player);
        }
    }
}
