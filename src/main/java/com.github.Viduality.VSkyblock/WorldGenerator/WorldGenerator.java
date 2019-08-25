package com.github.Viduality.VSkyblock.WorldGenerator;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public class WorldGenerator {

    private static final VSkyblock plugin = VSkyblock.getInstance();

    private static final String islandName = "VSkyblockMasterIsland";
    private static WorldManager wm = new WorldManager();

    /**
     * Creates a new world called "VSkyblockMasterIsland".
     * This island will be used to create all following islands where players will play.
     * @param callback
     */
    public static void CreateNewMasterIsland(final Callback callback) {
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {

                WorldCreator wc = new WorldCreator(islandName);
                wc.generator("VSkyblock");
                wc.environment(World.Environment.NORMAL);
                wc.type(WorldType.FLAT);
                wc.generateStructures(false);
                wc.createWorld();

                ConfigShorts.loaddefConfig();


                for (int x = -1; x < 5; x++) {
                    for (int y = 64; y < 66; y++) {
                        for (int z = -1; z < 5; z++) {
                            plugin.getServer().getWorld(islandName).getBlockAt(x, y, z).setType(Material.DIRT);
                        }
                    }
                }

                for (int x = -1; x < 5; x++) {
                    for (int z = -1; z < 5; z++) {
                        plugin.getServer().getWorld(islandName).getBlockAt(x, 66, z).setType(Material.GRASS_BLOCK);
                    }
                }

                for (int x = 2; x < 5; x++) {
                    for (int y = 64; y < 67; y++) {
                        for (int z = 2; z < 5; z++) {
                            plugin.getServer().getWorld(islandName).getBlockAt(x, y, z).setType(Material.AIR);
                        }
                    }
                }

                for (int x = 1; x < 4; x++) {
                    plugin.getServer().getWorld(islandName).getBlockAt(x, 65, 0).setType(Material.SAND);
                }


                plugin.getServer().getWorld(islandName).getBlockAt(0, 64, 0).setType(Material.BEDROCK);


                //Tree to get the same tree every time//


                plugin.getServer().getWorld(islandName).getBlockAt(0, 67, 3).setType(Material.OAK_LOG);
                plugin.getServer().getWorld(islandName).getBlockAt(0, 68, 3).setType(Material.OAK_LOG);
                plugin.getServer().getWorld(islandName).getBlockAt(0, 69, 3).setType(Material.OAK_LOG);
                plugin.getServer().getWorld(islandName).getBlockAt(0, 70, 3).setType(Material.OAK_LOG);
                plugin.getServer().getWorld(islandName).getBlockAt(0, 71, 3).setType(Material.OAK_LOG);

                plugin.getServer().getWorld(islandName).getBlockAt(0, 69, 2).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(0, 70, 2).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(0, 71, 2).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(0, 72, 2).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(0, 69, 1).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(0, 70, 1).setType(Material.OAK_LEAVES);

                plugin.getServer().getWorld(islandName).getBlockAt(-1, 69, 2).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(-1, 70, 2).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(-1, 71, 2).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(-1, 69, 1).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(-1, 70, 1).setType(Material.OAK_LEAVES);

                plugin.getServer().getWorld(islandName).getBlockAt(1, 69, 2).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(1, 70, 2).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(1, 69, 1).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(1, 70, 1).setType(Material.OAK_LEAVES);

                plugin.getServer().getWorld(islandName).getBlockAt(-2, 69, 2).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(-2, 70, 2).setType(Material.OAK_LEAVES);

                plugin.getServer().getWorld(islandName).getBlockAt(2, 69, 2).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(2, 70, 2).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(2, 69, 1).setType(Material.OAK_LEAVES);

                plugin.getServer().getWorld(islandName).getBlockAt(0, 69, 4).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(0, 70, 4).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(0, 71, 4).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(0, 72, 4).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(0, 69, 5).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(0, 70, 5).setType(Material.OAK_LEAVES);

                plugin.getServer().getWorld(islandName).getBlockAt(-1, 69, 4).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(-1, 70, 4).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(-1, 71, 4).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(-1, 69, 5).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(-1, 70, 5).setType(Material.OAK_LEAVES);

                plugin.getServer().getWorld(islandName).getBlockAt(1, 69, 4).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(1, 70, 4).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(1, 69, 5).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(1, 70, 5).setType(Material.OAK_LEAVES);

                plugin.getServer().getWorld(islandName).getBlockAt(-2, 69, 4).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(-2, 70, 4).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(-2, 69, 5).setType(Material.OAK_LEAVES);

                plugin.getServer().getWorld(islandName).getBlockAt(2, 69, 4).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(2, 70, 4).setType(Material.OAK_LEAVES);

                plugin.getServer().getWorld(islandName).getBlockAt(0, 72, 3).setType(Material.OAK_LEAVES);

                plugin.getServer().getWorld(islandName).getBlockAt(1, 69, 3).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(1, 70, 3).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(1, 71, 3).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(1, 72, 3).setType(Material.OAK_LEAVES);

                plugin.getServer().getWorld(islandName).getBlockAt(-1, 69, 3).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(-1, 70, 3).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(-1, 71, 3).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(-1, 72, 3).setType(Material.OAK_LEAVES);

                plugin.getServer().getWorld(islandName).getBlockAt(2, 69, 3).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(2, 70, 3).setType(Material.OAK_LEAVES);

                plugin.getServer().getWorld(islandName).getBlockAt(-2, 69, 3).setType(Material.OAK_LEAVES);
                plugin.getServer().getWorld(islandName).getBlockAt(-2, 70, 3).setType(Material.OAK_LEAVES);

                plugin.getServer().getWorld(islandName).getWorldBorder().setCenter(0, 0);
                plugin.getServer().getWorld(islandName).getWorldBorder().setSize(100);


                ///// Chest with Start Equip /////

                plugin.getServer().getWorld(islandName).getBlockAt(4, 67, 0).setType(Material.CHEST);
                org.bukkit.material.Chest ChestData = new org.bukkit.material.Chest(Material.CHEST);
                ChestData.setFacingDirection(BlockFace.WEST);
                org.bukkit.block.Chest chest = (org.bukkit.block.Chest) plugin.getServer().getWorld(islandName).getBlockAt(4, 67, 0).getState();
                chest.setData(ChestData);
                chest.update();


                /////Inventory/////

                ItemStack ice = new ItemStack(Material.ICE, 2);
                ItemStack lavaBucket = new ItemStack(Material.LAVA_BUCKET, 1);
                ItemStack sugarCane = new ItemStack(Material.SUGAR_CANE, 1);
                ItemStack cactus = new ItemStack(Material.CACTUS, 1);
                ItemStack melon = new ItemStack(Material.MELON_SEEDS, 1);
                ItemStack pumpkin = new ItemStack(Material.PUMPKIN_SEEDS, 1);
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
                chest.getBlockInventory().setItem(7, brownmushroom);
                chest.getBlockInventory().setItem(8, redmushroom);

                plugin.getServer().getWorld(islandName).getSpawnLocation().setX(0);
                plugin.getServer().getWorld(islandName).getSpawnLocation().setY(67);
                plugin.getServer().getWorld(islandName).getSpawnLocation().setZ(0);

                plugin.getServer().getWorld(islandName).setAutoSave(true);
                wm.addWorld(islandName);


                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(islandName);
                    }
                });
            }
        });

    }



    public interface Callback {
        public void onQueryDone(String result);
    }
}
