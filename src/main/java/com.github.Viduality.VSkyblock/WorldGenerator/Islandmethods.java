package com.github.Viduality.VSkyblock.WorldGenerator;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Difficulty;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Islandmethods {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseWriter databaseWriter = new DatabaseWriter();
    private DatabaseReader databaseReader = new DatabaseReader();
    private WorldManager wm = new WorldManager();

    /**
     * Checks if a string is from type Integer
     * @param s
     * @return boolean
     */
    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Copies the template island and creates a new world for the player.
     * @param uuid
     * @param oldIsland
     */
    public void createNewIsland(String uuid, String oldIsland) {
        Player player = plugin.getServer().getPlayer(UUID.fromString(uuid));
        String worldsizeString = plugin.getConfig().getString("WorldSize");
        String difficulty = plugin.getConfig().getString("Difficulty");
        // boolean loaded = plugin.getMV().getCore().getMVWorldManager().loadWorld("VSkyblockMasterIsland");
        databaseReader.getLatestIsland(new DatabaseReader.CallbackStrings() {
            @Override
            public void onQueryDone(String result, boolean a) {
                if (a) {
                    wm.createIsland(result);
                    // plugin.getMV().getCore().getMVWorldManager().cloneWorld("VSkyblockMasterIsland", result);

                    // boolean loaded = plugin.getMV().getCore().getMVWorldManager().loadWorld(result);
                    // plugin.getMV().getCore().getMVWorldManager().getMVWorld(result).setAlias(result);
                    // plugin.getMV().getCore().getMVWorldManager().getMVWorld(result).setAutoLoad(false);


                    /*
                     * World Size
                     */

                    plugin.getServer().getWorld(result).getWorldBorder().setCenter(0, 0);

                    if (worldsizeString != null) {
                        if (isInt(worldsizeString)) {
                            Integer worldsize = Integer.valueOf(worldsizeString);
                            if (worldsize <= 2000) {
                                plugin.getServer().getWorld(result).getWorldBorder().setSize(worldsize);
                            }
                        }
                    } else {
                        plugin.getServer().getWorld(result).getWorldBorder().setSize(500);
                    }


                    /*
                     * Difficulty
                     */

                    if (difficulty.equalsIgnoreCase("EASY")) {
                        plugin.getServer().getWorld(result).setDifficulty(Difficulty.EASY);
                        // plugin.getMV().getCore().getMVWorldManager().getMVWorld(result).setDifficulty(Difficulty.EASY);
                    } else if (difficulty.equalsIgnoreCase("HARD")) {
                        plugin.getServer().getWorld(result).setDifficulty(Difficulty.HARD);
                        // plugin.getMV().getCore().getMVWorldManager().getMVWorld(result).setDifficulty(Difficulty.HARD);
                    } else if (difficulty.equalsIgnoreCase("PEACEFUL")) {
                        plugin.getServer().getWorld(result).setDifficulty(Difficulty.PEACEFUL);
                        // plugin.getMV().getCore().getMVWorldManager().getMVWorld(result).setDifficulty(Difficulty.PEACEFUL);
                    } else {
                        plugin.getServer().getWorld(result).setDifficulty(Difficulty.NORMAL);
                        // plugin.getMV().getCore().getMVWorldManager().getMVWorld(result).setDifficulty(Difficulty.NORMAL);
                    }


                    Island.restartmap.asMap().remove(player.getUniqueId());
                    Island.playerislands.put(uuid, result);
                    plugin.getServer().getPlayer(player.getUniqueId()).teleport(plugin.getServer().getWorld(result).getSpawnLocation());
                    // plugin.getServer().getPlayer(player.getUniqueId()).teleport(plugin.getMV().getCore().getMVWorldManager().getMVWorld(result).getSpawnLocation());
                    if (oldIsland != null) {
                        wm.unloadWorld(oldIsland);
                        // plugin.getMV().getCore().getMVWorldManager().unloadWorld(oldIsland);
                    }
                    databaseWriter.addIsland(result, uuid);
                } else {
                    ConfigShorts.messagefromString("FailedToCreateIsland", player);
                }
            }
        });
    }
}

