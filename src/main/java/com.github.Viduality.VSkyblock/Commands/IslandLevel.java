package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.DefaultFiles;
import com.github.Viduality.VSkyblock.Listener.CobblestoneGenerator;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

public class IslandLevel implements SubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private DatabaseWriter databaseWriter = new DatabaseWriter();
    private static Cache<UUID, Boolean> timebetweenreuse = CacheBuilder.newBuilder()
            .expireAfterWrite(gettimebetweencalc(), TimeUnit.MINUTES)
            .build();

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
                        if (timebetweenreuse.getIfPresent(player.getUniqueId()) == null) {
                            timebetweenreuse.put(player.getUniqueId(), true);
                            ConfigShorts.messagefromString("CalculatingNewIslandLevel", player);
                            World world = plugin.getServer().getWorld(databaseCache.getIslandname());
                            if (world == null) {
                                plugin.getLogger().log(Level.SEVERE, "World " + databaseCache.getIslandname() + " not found?");
                                return;
                            }
                            databaseReader.getChallengePoints(databaseCache.getIslandId(), new DatabaseReader.CallbackINT() {
                                @Override
                                public void onQueryDone(int result) {
                                    int valueriselevel = getValueRiseLevel();
                                    int valueincrease = getValueIncrease();
                                    double worldsize = world.getWorldBorder().getSize();
                                    int x1 = ((int) (-1 * worldsize / 2)) >> 4;
                                    int x2 = ((int) worldsize / 2) >> 4;
                                    int z1 = x1;
                                    int z2 = x2;
                                    double value = 0;
                                    if (isInt(ConfigShorts.getDefConfig().getString("IslandValueonStart"))) {
                                        value = ConfigShorts.getDefConfig().getInt("IslandValueonStart");
                                    } else {
                                        value = 150;
                                    }
                                    value = value + result;
                                    int valueperlevel;
                                    if (isInt(ConfigShorts.getDefConfig().getString("IslandValue"))) {
                                        valueperlevel = ConfigShorts.getDefConfig().getInt("IslandValue");
                                    } else {
                                        valueperlevel = 300;
                                    }

                                    IslandCounter counter = new IslandCounter(value, 0, (c) -> {

                                        double currentvalue = c.value;

                                        int level = 0;
                                        int increasedvaluefornextlevel = valueperlevel + valueincrease;
                                        for (int i = 0; i < valueriselevel; i++) {
                                            if (currentvalue - valueperlevel >= 0) {
                                                level = level + 1;
                                                currentvalue = currentvalue - valueperlevel;
                                            } else {
                                                currentvalue = 0;
                                                break;
                                            }
                                        }
                                        if (currentvalue - increasedvaluefornextlevel >= 0) {
                                            while (currentvalue >= 0) {
                                                if (currentvalue - increasedvaluefornextlevel >= 0) {
                                                    level++;
                                                    currentvalue = currentvalue - increasedvaluefornextlevel;
                                                    increasedvaluefornextlevel = increasedvaluefornextlevel + valueincrease;
                                                } else {
                                                    currentvalue = 0;
                                                    break;
                                                }
                                            }
                                        }

                                        int roundlevel = (int) Math.floor(level);
                                        databaseWriter.updateIslandLevel(databaseCache.getIslandId(), roundlevel, c.blocks, player.getUniqueId());
                                        ConfigShorts.custommessagefromString("NewIslandLevel", player, String.valueOf(roundlevel));
                                        CobblestoneGenerator.islandlevels.put(databaseCache.getIslandname(), roundlevel);
                                    });

                                    // Two loops are necessary as getChunkAtAsync might return instantly if chunk is loaded
                                    for (int x = x1; x <= x2; x++) {
                                        for (int z = z1; z <= z2; z++) {
                                            counter.toCount();
                                        }
                                    }

                                    for (int x = x1; x <= x2; x++) {
                                        for (int z = z1; z <= z2; z++) {
                                            world.getChunkAtAsync(x, z, false).whenComplete((c, ex) -> counter.count(c));
                                        }
                                    }
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

    private static int getValueRiseLevel() {
        String s = ConfigShorts.getDefConfig().getString("IslandValueRiseLevel");
        if (s != null) {
            if (isInt(s)) {
                return Integer.parseInt(s);
            } else {
                return 150;
            }
        } else {
            return 150;
        }
    }

    private static int getValueIncrease() {
        String s = ConfigShorts.getDefConfig().getString("IslandValueIncreasePerLevel");
        if (s != null) {
            if (isInt(s)) {
                return Integer.parseInt(s);
            } else {
                return 20;
            }
        } else {
            return 20;
        }
    }

    private static int gettimebetweencalc() {
        int timebetweencalc = 5;
        if (isInt(ConfigShorts.getDefConfig().getString("IslandLevelReuse"))) {
            timebetweencalc = ConfigShorts.getDefConfig().getInt("IslandLevelReuse");
        }
        return timebetweencalc;
    }

    public static class IslandCounter {
        public double value;
        public int blocks;
        private int toCount = 0;
        private final Consumer<IslandCounter> onDone;

        public IslandCounter(double value, int blocks, Consumer<IslandCounter> onDone) {
            this.value = value;
            this.blocks = blocks;
            this.onDone = onDone;
        }

        public void add(IslandCounter counter) {
            this.value += counter.value;
            this.blocks += counter.blocks;
        }

        public void toCount() {
            toCount++;
        }

        public void count(Chunk chunk) {
            if (chunk != null && chunk.getInhabitedTime() > 0) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 256; y++) {
                            Block block = chunk.getBlock(x, y, z);
                            if (block.getType() == Material.AIR || block.getType() == Material.VOID_AIR) {
                                continue;
                            }
                            if (Tag.LEAVES.isTagged(block.getType()) && !((Leaves) block.getBlockData()).isPersistent()) {
                                continue;
                            }
                            blocks = blocks + 1;
                            value = value + DefaultFiles.blockvalues.getOrDefault(block.getType(), 0D);
                        }
                    }
                }
            }
            if (--toCount == 0) {
                onDone.accept(this);
            }
        }
    }
}
