package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.DefaultFiles;
import com.github.Viduality.VSkyblock.Listener.CobblestoneGenerator;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class IslandLevel implements SubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private DatabaseWriter databaseWriter = new DatabaseWriter();
    private static LoadingCache<String, String> timebetweenreuse = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(gettimebetweencalc(), TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, String>() {
                        @Override
                        public String load(String uuid) throws Exception {
                            return null;
                        }
                    }
            );





    @Override
    public void execute(DatabaseCache databaseCache) {
        if (databaseCache.getIslandId() != 0) {
            UUID uuid = null;
            if (databaseCache.getArg() != null) {
                OfflinePlayer target = plugin.getServer().getOfflinePlayer(databaseCache.getArg());
                uuid = target.getUniqueId();
            } else {
                uuid = databaseCache.getUuid();
            }
            Player player = databaseCache.getPlayer();
            databaseReader.getislandlevelfromuuid(uuid, new DatabaseReader.CallbackINT() {
                @Override
                public void onQueryDone(int result) {
                    if (databaseCache.getArg() != null) {
                        ConfigShorts.custommessagefromString("PlayersIslandLevel", player, String.valueOf(result), databaseCache.getArg());
                    } else {
                        ConfigShorts.custommessagefromString("CurrentIslandLevel", player, String.valueOf(result));
                        if (!timebetweenreuse.asMap().containsValue(player.getUniqueId().toString())) {
                            timebetweenreuse.put(player.getUniqueId().toString(), player.getUniqueId().toString());
                            ConfigShorts.messagefromString("CalculatingNewIslandLevel", player);
                            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    double worldsize = plugin.getServer().getWorld(databaseCache.getIslandname()).getWorldBorder().getSize();
                                    int y1 = 0;
                                    double x1 = -1 * (worldsize/2);
                                    double x2 = worldsize/2;
                                    double z1 = x1;
                                    double z2 = x2;
                                    double value = 0;
                                    if (isInt(plugin.getConfig().getString("IslandValueonStart"))) {
                                        value = plugin.getConfig().getInt("IslandValueonStart");
                                    } else {
                                        value = 150;
                                    }
                                    int valueperlevel;
                                    if (isInt(plugin.getConfig().getString("IslandValue"))) {
                                        valueperlevel = plugin.getConfig().getInt("IslandValue");
                                    } else {
                                        valueperlevel = 300;
                                    }

                                    double level;

                                    int blocks = 0;

                                    for (int x = (int) x1; x <= x2; x++) {
                                        for (int z = (int) z1; z <= z2; z++) {
                                            if (plugin.getServer().getWorld(databaseCache.getIslandname()).getHighestBlockYAt(x, z) != 0) {
                                                int y2 = plugin.getServer().getWorld(databaseCache.getIslandname()).getHighestBlockYAt(x, z);
                                                for (int y = y1; y <= y2; y++) {
                                                    Material block = plugin.getServer().getWorld(databaseCache.getIslandname()).getBlockAt(x, y, z).getType();
                                                    if (!plugin.getServer().getWorld(databaseCache.getIslandname()).getBlockAt(x, y, z).getType().equals(Material.AIR) && !plugin.getServer().getWorld(databaseCache.getIslandname()).getBlockAt(x,y,z).getType().equals(Material.VOID_AIR)) {
                                                        blocks = blocks + 1;
                                                        if (DefaultFiles.blockvalues.containsKey(block)) {
                                                            value = value + DefaultFiles.blockvalues.get(block);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    level = value/valueperlevel;
                                    int roundlevel = (int) level;
                                    databaseWriter.updateIslandLevel(databaseCache.getIslandId(), roundlevel, blocks, player.getUniqueId());
                                    ConfigShorts.custommessagefromString("NewIslandLevel", player, String.valueOf(roundlevel));
                                    plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            CobblestoneGenerator.islandlevels.put(databaseCache.getIslandname(), roundlevel);
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            });
        } else {
            ConfigShorts.messagefromString("NoIsland", databaseCache.getPlayer());
        }
    }

    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private static int gettimebetweencalc() {
        int timebetweencalc = 5;
        if (isInt(VSkyblock.getInstance().getConfig().getString("IslandLevelReuse"))) {
            timebetweencalc = VSkyblock.getInstance().getConfig().getInt("IslandLevelReuse");
        }
        return timebetweencalc;
    }
}
