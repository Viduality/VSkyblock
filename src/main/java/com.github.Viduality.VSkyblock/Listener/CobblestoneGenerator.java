package com.github.Viduality.VSkyblock.Listener;

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

import com.github.Viduality.VSkyblock.VSkyblock;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CobblestoneGenerator implements Listener {

    private final VSkyblock plugin;

    public static final List<Level> LEVELS = new ArrayList<>();

    public static int cobblestoneLevelInterval;
    public static double cobblestoneChance;
    public static boolean cobbleStoneMultiDrop = false;

    public static HashMap<String, Integer> islandGenLevel = new HashMap<>(); //Islandname and generatorlevel
    public static HashMap<String, Integer> islandlevels = new HashMap<>(); //Islandname and islandlevel
    public static Cache<Location, Long> cobblegen = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    public CobblestoneGenerator(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void cobblestoneGeneratorBlocks(BlockFormEvent blockFormEvent) {
        Block block = blockFormEvent.getNewState().getBlock();
        Material newmaterial = blockFormEvent.getNewState().getType();
        Location location = blockFormEvent.getNewState().getLocation();
        if (newmaterial.equals(Material.COBBLESTONE)) {
            blockFormEvent.getNewState().setType(getCobblestone(location));
            if (islandGenLevel.containsKey(block.getLocation().getWorld().getName())) {
                int level = islandGenLevel.get(block.getLocation().getWorld().getName());
                blockFormEvent.getNewState().setType(rollGenerator(level, location));
            }
        }
    }

    @EventHandler
    public void cobblestoneGeneratorDrops(BlockBreakEvent blockBreakEvent) {
        if (blockBreakEvent.getBlock().getType().equals(Material.COBBLESTONE) && cobbleStoneMultiDrop) {
            if (blockBreakEvent.getBlock().getRelative(BlockFace.NORTH).getType().equals(Material.LAVA)
                    || blockBreakEvent.getBlock().getRelative(BlockFace.EAST).getType().equals(Material.LAVA)
                    || blockBreakEvent.getBlock().getRelative(BlockFace.SOUTH).getType().equals(Material.LAVA)
                    || blockBreakEvent.getBlock().getRelative(BlockFace.WEST).getType().equals(Material.LAVA)) {
                if (blockBreakEvent.getBlock().getRelative(BlockFace.NORTH).getType().equals(Material.WATER)
                        || blockBreakEvent.getBlock().getRelative(BlockFace.EAST).getType().equals(Material.WATER)
                        || blockBreakEvent.getBlock().getRelative(BlockFace.SOUTH).getType().equals(Material.WATER)
                        || blockBreakEvent.getBlock().getRelative(BlockFace.WEST).getType().equals(Material.WATER)) {
                    if (islandlevels.containsKey(blockBreakEvent.getBlock().getLocation().getWorld().getName())) {
                        int level = islandlevels.get(blockBreakEvent.getBlock().getLocation().getWorld().getName());
                        if (level >= cobblestoneLevelInterval) {
                            int additionalDropsAmount = rollCobbleAmount(blockBreakEvent.getBlock().getLocation().getWorld().getName());
                            if (additionalDropsAmount != 0) {
                                blockBreakEvent.getBlock().getLocation().getWorld().dropItemNaturally(blockBreakEvent.getBlock().getLocation(), new ItemStack(Material.COBBLESTONE, additionalDropsAmount));
                            }

                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void pistonEvent(BlockPistonExtendEvent blockPistonExtendEvent) {
        if (!blockPistonExtendEvent.getBlocks().isEmpty()) {
            if (blockPistonExtendEvent.getBlocks().get(0).getType().equals(Material.INFESTED_COBBLESTONE)) {
                blockPistonExtendEvent.setCancelled(true);
                blockPistonExtendEvent.getBlocks().get(0).setType(Material.COBBLESTONE);
                CobblestoneGenerator.cobblegen.put(blockPistonExtendEvent.getBlocks().get(0).getLocation(), System.currentTimeMillis());
            }
        }
    }

    /**
     * Rolls the chances for the generator block.
     * @param generatorLevel The level of the generator
     * @param location The location of the gneerator
     * @return int
     */
    private Material rollGenerator(int generatorLevel, Location location) {
        double random = Math.random() * 100;
        double checkedChance = 0;

        for (Level level : LEVELS) {
            if (level.getLevel() <= generatorLevel) {
                checkedChance += level.getChance();
                if (checkedChance > random) {
                    return level.getType();
                }
            }
        }
        return getCobblestone(location);
    }

    /**
     * Rolls the chances for the amount of cobblestone to be dropped.
     * @param island
     * @return int
     */
    private int rollCobbleAmount(String island) {
        double chance = cobblestoneChance / 100;
        int chances = islandlevels.get(island) / cobblestoneLevelInterval;
        int drops = 0;
        for (int i = 0; i < chances; i++) {
            double random = Math.random();
            if (chance >= random) {
                drops = drops +1;
            }
        }
        return drops;
    }

    private Material getCobblestone(Location loc) {
        if (cobblegen.getIfPresent(loc) != null) {
            return Material.COBBLESTONE;
        } else {
            return Material.INFESTED_COBBLESTONE;
        }
    }

    /**
     * Returns the required island level to upgrade the current cobblestone generator.
     * @param generatorLevel
     * @return the island level as an Integer or null if there is no level of that generator
     */
    public static Integer getRequiredIslandLevel(int generatorLevel) {
        Level level = getLevel( generatorLevel);
        return level != null ? level.getIslandLevel() : null;
    }

    /**
     * Returns the generator level by the number
     * @param generatorLevel The level number
     * @return The generator level or null if none is found with that number
     */
    public static Level getLevel(int generatorLevel) {
        if (generatorLevel == 0) {
            return null;
        }
        if (LEVELS.size() <= generatorLevel) {
            Level level = LEVELS.get(generatorLevel - 1);
            if (level.getLevel() == generatorLevel) {
                return level;
            }
        }
        for (CobblestoneGenerator.Level level : CobblestoneGenerator.LEVELS) {
            if (level.getLevel() == generatorLevel) {
                return level;
            }
        }
        return null;
    }

    public static class Level {
        private final int level;
        private final int islandLevel;
        private final Material type;
        private final double chance;

        public Level(int level, int islandLevel, Material type, double chance) {
            this.level = level;
            this.islandLevel = islandLevel;
            this.type = type;
            this.chance = chance;
        }

        public int getLevel() {
            return level;
        }

        public int getIslandLevel() {
            return islandLevel;
        }

        public Material getType() {
            return type;
        }

        public double getChance() {
            return chance;
        }
    }
}
