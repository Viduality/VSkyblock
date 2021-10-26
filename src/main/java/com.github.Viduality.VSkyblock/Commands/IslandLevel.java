package com.github.Viduality.VSkyblock.Commands;

/*
 * VSkyblock
 * Copyright (C) 2020  Viduality
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.github.Viduality.VSkyblock.DefaultFiles;
import com.github.Viduality.VSkyblock.Listener.CobblestoneGenerator;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

/*
 * Gets the islands level.
 */
public class IslandLevel extends PlayerSubCommand {

    private static Cache<UUID, Boolean> timebetweenreuse = CacheBuilder.newBuilder()
            .expireAfterWrite(gettimebetweencalc(), TimeUnit.MINUTES)
            .build();

    public IslandLevel(VSkyblock plugin) {
        super(plugin, "level");
    }

    @Override
    public void execute(CommandSender sender, PlayerInfo playerInfo, String[] args) {
        if (playerInfo.getIslandId() != 0) {
            UUID uuid;
            if (args.length > 0) {
                OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);
                uuid = target.getUniqueId();
            } else {
                uuid = playerInfo.getUuid();
            }
            Player player = playerInfo.getPlayer();
            plugin.getDb().getReader().getIslandLevelFromUuid(uuid, (islandlevel) -> {
                if (args.length > 0) {
                    ConfigShorts.custommessagefromString("PlayersIslandLevel", player, String.valueOf(islandlevel), args[0]);
                } else {
                    ConfigShorts.custommessagefromString("CurrentIslandLevel", player, String.valueOf(islandlevel));
                    if (timebetweenreuse.getIfPresent(player.getUniqueId()) == null) {
                        timebetweenreuse.put(player.getUniqueId(), true);
                        ConfigShorts.messagefromString("CalculatingNewIslandLevel", player);
                        World world = plugin.getServer().getWorld(playerInfo.getIslandName());
                        if (world == null) {
                            plugin.getLogger().log(Level.SEVERE, "World " + playerInfo.getIslandName() + " not found?");
                            return;
                        }
                        plugin.getDb().getReader().getIslandsChallengePoints(playerInfo.getIslandId(), (challengePoints) -> {
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
                            value = value + challengePoints;
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
                                plugin.getDb().getWriter().updateIslandLevel(playerInfo.getIslandId(), roundlevel, c.blocks, player.getUniqueId());
                                ConfigShorts.custommessagefromString("NewIslandLevel", player, String.valueOf(roundlevel));
                                CobblestoneGenerator.islandlevels.put(playerInfo.getIslandName(), roundlevel);
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
                        });
                    }
                }
            });
        } else {
            ConfigShorts.messagefromString("NoIsland", playerInfo.getPlayer());
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
