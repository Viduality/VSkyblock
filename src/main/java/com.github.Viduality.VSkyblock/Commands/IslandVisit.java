package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.List;

public class IslandVisit implements SubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private WorldManager wm = new WorldManager();


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
                                                        String island = "VSkyblockIsland_" + islandid;
                                                        if (!wm.getSpawnLocation(island).getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
                                                            if (wm.getSpawnLocation(island).getBlock().getType().equals(Material.AIR)) {
                                                                if (wm.getSpawnLocation(island).getBlock().getRelative(BlockFace.UP).getType().equals(Material.AIR)) {
                                                                    if (!wm.getSpawnLocation(island).getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.LAVA)) {
                                                                        if (!wm.getSpawnLocation(island).getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.MAGMA_BLOCK)) {
                                                                            if (!wm.getSpawnLocation(island).getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.WITHER_ROSE)) {
                                                                                player.teleport(wm.getSpawnLocation(island));
                                                                                databaseReader.getIslandMembers(islandid, new DatabaseReader.CallbackList() {
                                                                                    @Override
                                                                                    public void onQueryDone(List<String> result) {
                                                                                        for (String member : result) {
                                                                                            OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(member);
                                                                                            if (offlinePlayer.isOnline()) {
                                                                                                Player onlinePlayer = (Player) offlinePlayer;
                                                                                                ConfigShorts.custommessagefromString("PlayerVisitingYourIsland", onlinePlayer, player.getName());
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                });
                                                                            } else {
                                                                                ConfigShorts.messagefromString("IslandSpawnNotSafe", player);
                                                                            }
                                                                        } else {
                                                                            ConfigShorts.messagefromString("IslandSpawnNotSafe", player);
                                                                        }
                                                                    } else {
                                                                        ConfigShorts.messagefromString("IslandSpawnNotSafe", player);
                                                                    }
                                                                } else {
                                                                    ConfigShorts.messagefromString("IslandSpawnNotSafe", player);
                                                                }
                                                            } else {
                                                                ConfigShorts.messagefromString("IslandSpawnNotSafe", player);
                                                            }
                                                        } else {
                                                            ConfigShorts.messagefromString("IslandSpawnNotSafe", player);
                                                        }
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
