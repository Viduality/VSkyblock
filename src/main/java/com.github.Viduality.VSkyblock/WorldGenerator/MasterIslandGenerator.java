package com.github.Viduality.VSkyblock.WorldGenerator;

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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public class MasterIslandGenerator {

    private final VSkyblock plugin;

    public static final String WORLD_NAME = "VSkyblockMasterIsland";

    public MasterIslandGenerator(VSkyblock plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a new world called "VSkyblockMasterIsland".
     * This island will be used to create all following islands where players will play.
     * @param callback
     */
    public void create(final Callback callback) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {

            WorldCreator wc = new WorldCreator(WORLD_NAME);
            wc.generator("VSkyblock");
            wc.environment(World.Environment.NORMAL);
            wc.type(WorldType.FLAT);
            wc.generateStructures(false);
            World w = wc.createWorld();


            for (int x = -1; x < 5; x++) {
                for (int y = 64; y < 66; y++) {
                    for (int z = -1; z < 5; z++) {
                        w.getBlockAt(x, y, z).setType(Material.DIRT);
                    }
                }
            }

            for (int x = -1; x < 5; x++) {
                for (int z = -1; z < 5; z++) {
                    w.getBlockAt(x, 66, z).setType(Material.GRASS_BLOCK);
                }
            }

            for (int x = 2; x < 5; x++) {
                for (int y = 64; y < 67; y++) {
                    for (int z = 2; z < 5; z++) {
                        w.getBlockAt(x, y, z).setType(Material.AIR);
                    }
                }
            }

            for (int x = 1; x < 4; x++) {
                w.getBlockAt(x, 65, 0).setType(Material.SAND);
            }


            w.getBlockAt(0, 64, 0).setType(Material.BEDROCK);


            //Tree to get the same tree every time//


            w.getBlockAt(0, 67, 3).setType(Material.OAK_LOG);
            w.getBlockAt(0, 68, 3).setType(Material.OAK_LOG);
            w.getBlockAt(0, 69, 3).setType(Material.OAK_LOG);
            w.getBlockAt(0, 70, 3).setType(Material.OAK_LOG);
            w.getBlockAt(0, 71, 3).setType(Material.OAK_LOG);

            w.getBlockAt(0, 69, 2).setType(Material.OAK_LEAVES);
            w.getBlockAt(0, 70, 2).setType(Material.OAK_LEAVES);
            w.getBlockAt(0, 71, 2).setType(Material.OAK_LEAVES);
            w.getBlockAt(0, 72, 2).setType(Material.OAK_LEAVES);
            w.getBlockAt(0, 69, 1).setType(Material.OAK_LEAVES);
            w.getBlockAt(0, 70, 1).setType(Material.OAK_LEAVES);

            w.getBlockAt(-1, 69, 2).setType(Material.OAK_LEAVES);
            w.getBlockAt(-1, 70, 2).setType(Material.OAK_LEAVES);
            w.getBlockAt(-1, 71, 2).setType(Material.OAK_LEAVES);
            w.getBlockAt(-1, 69, 1).setType(Material.OAK_LEAVES);
            w.getBlockAt(-1, 70, 1).setType(Material.OAK_LEAVES);

            w.getBlockAt(1, 69, 2).setType(Material.OAK_LEAVES);
            w.getBlockAt(1, 70, 2).setType(Material.OAK_LEAVES);
            w.getBlockAt(1, 69, 1).setType(Material.OAK_LEAVES);
            w.getBlockAt(1, 70, 1).setType(Material.OAK_LEAVES);

            w.getBlockAt(-2, 69, 2).setType(Material.OAK_LEAVES);
            w.getBlockAt(-2, 70, 2).setType(Material.OAK_LEAVES);

            w.getBlockAt(2, 69, 2).setType(Material.OAK_LEAVES);
            w.getBlockAt(2, 70, 2).setType(Material.OAK_LEAVES);
            w.getBlockAt(2, 69, 1).setType(Material.OAK_LEAVES);

            w.getBlockAt(0, 69, 4).setType(Material.OAK_LEAVES);
            w.getBlockAt(0, 70, 4).setType(Material.OAK_LEAVES);
            w.getBlockAt(0, 71, 4).setType(Material.OAK_LEAVES);
            w.getBlockAt(0, 72, 4).setType(Material.OAK_LEAVES);
            w.getBlockAt(0, 69, 5).setType(Material.OAK_LEAVES);
            w.getBlockAt(0, 70, 5).setType(Material.OAK_LEAVES);

            w.getBlockAt(-1, 69, 4).setType(Material.OAK_LEAVES);
            w.getBlockAt(-1, 70, 4).setType(Material.OAK_LEAVES);
            w.getBlockAt(-1, 71, 4).setType(Material.OAK_LEAVES);
            w.getBlockAt(-1, 69, 5).setType(Material.OAK_LEAVES);
            w.getBlockAt(-1, 70, 5).setType(Material.OAK_LEAVES);

            w.getBlockAt(1, 69, 4).setType(Material.OAK_LEAVES);
            w.getBlockAt(1, 70, 4).setType(Material.OAK_LEAVES);
            w.getBlockAt(1, 69, 5).setType(Material.OAK_LEAVES);
            w.getBlockAt(1, 70, 5).setType(Material.OAK_LEAVES);

            w.getBlockAt(-2, 69, 4).setType(Material.OAK_LEAVES);
            w.getBlockAt(-2, 70, 4).setType(Material.OAK_LEAVES);
            w.getBlockAt(-2, 69, 5).setType(Material.OAK_LEAVES);

            w.getBlockAt(2, 69, 4).setType(Material.OAK_LEAVES);
            w.getBlockAt(2, 70, 4).setType(Material.OAK_LEAVES);

            w.getBlockAt(0, 72, 3).setType(Material.OAK_LEAVES);

            w.getBlockAt(1, 69, 3).setType(Material.OAK_LEAVES);
            w.getBlockAt(1, 70, 3).setType(Material.OAK_LEAVES);
            w.getBlockAt(1, 71, 3).setType(Material.OAK_LEAVES);
            w.getBlockAt(1, 72, 3).setType(Material.OAK_LEAVES);

            w.getBlockAt(-1, 69, 3).setType(Material.OAK_LEAVES);
            w.getBlockAt(-1, 70, 3).setType(Material.OAK_LEAVES);
            w.getBlockAt(-1, 71, 3).setType(Material.OAK_LEAVES);
            w.getBlockAt(-1, 72, 3).setType(Material.OAK_LEAVES);

            w.getBlockAt(2, 69, 3).setType(Material.OAK_LEAVES);
            w.getBlockAt(2, 70, 3).setType(Material.OAK_LEAVES);

            w.getBlockAt(-2, 69, 3).setType(Material.OAK_LEAVES);
            w.getBlockAt(-2, 70, 3).setType(Material.OAK_LEAVES);

            w.getWorldBorder().setCenter(0, 0);
            w.getWorldBorder().setSize(100);


            ///// Chest with Start Equip /////

            w.getBlockAt(4, 67, 0).setType(Material.CHEST);
            org.bukkit.material.Chest chestData = new org.bukkit.material.Chest(Material.CHEST);
            chestData.setFacingDirection(BlockFace.WEST);
            org.bukkit.block.Chest chest = (org.bukkit.block.Chest) w.getBlockAt(4, 67, 0).getState();
            chest.setData(chestData);
            chest.update();


            /////Inventory/////

            ItemStack ice = new ItemStack(Material.ICE, 2);
            ItemStack lavaBucket = new ItemStack(Material.LAVA_BUCKET, 1);
            ItemStack sugarCane = new ItemStack(Material.SUGAR_CANE, 1);
            ItemStack cactus = new ItemStack(Material.CACTUS, 1);
            ItemStack melon = new ItemStack(Material.MELON_SEEDS, 1);
            ItemStack pumpkin = new ItemStack(Material.PUMPKIN_SEEDS, 1);
            ItemStack beetroot = new ItemStack(Material.BEETROOT_SEEDS, 1);
            ItemStack brownmushroom = new ItemStack(Material.BROWN_MUSHROOM, 1);
            ItemStack redmushroom = new ItemStack(Material.RED_MUSHROOM, 1);
            ItemStack sapling = new ItemStack(Material.OAK_SAPLING, 1);
            chest.getBlockInventory().setItem(0, ice);
            chest.getBlockInventory().setItem(1, lavaBucket);
            chest.getBlockInventory().setItem(2, sapling);
            chest.getBlockInventory().setItem(3, sugarCane);
            chest.getBlockInventory().setItem(4, cactus);
            chest.getBlockInventory().setItem(5, melon);
            chest.getBlockInventory().setItem(6, pumpkin);
            chest.getBlockInventory().setItem(7, beetroot);
            chest.getBlockInventory().setItem(8, brownmushroom);
            chest.getBlockInventory().setItem(9, redmushroom);

            w.setSpawnLocation(0, 67, 0);

            w.setAutoSave(true);
            plugin.getWorldManager().addWorld(WORLD_NAME, "VSkyblock", "NORMAL");


            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(WORLD_NAME));
        });

    }

    public interface Callback {
        public void onQueryDone(String result);
    }
}
