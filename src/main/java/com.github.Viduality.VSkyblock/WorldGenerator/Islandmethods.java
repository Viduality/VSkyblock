package com.github.Viduality.VSkyblock.WorldGenerator;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Listener.CobblestoneGenerator;
import com.github.Viduality.VSkyblock.Utilitys.*;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
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
    public void createNewIsland(UUID uuid, String oldIsland) {
        String worldsizeString = ConfigShorts.getDefConfig().getString("WorldSize");
        String difficulty = ConfigShorts.getDefConfig().getString("Difficulty");
        databaseReader.getLatestIsland((worldName, a) -> {
            if (a) {
                if (!wm.createIsland(worldName)) {
                    plugin.getLogger().severe("Failed to create new island for " + uuid + " with id " + worldName);
                    return;
                }


                /*
                 * World Size
                 */

                World world = plugin.getServer().getWorld(worldName);
                if (world == null) {
                    plugin.getLogger().severe("Could not create new island for " + uuid + " properly as we don't know the world?");
                    return;
                }
                world.getWorldBorder().setCenter(0, 0);

                if (worldsizeString != null) {
                    if (isInt(worldsizeString)) {
                        Integer worldsize = Integer.valueOf(worldsizeString);
                        if (worldsize <= 2000) {
                            world.getWorldBorder().setSize(worldsize);
                        }
                    }
                } else {
                    world.getWorldBorder().setSize(500);
                }


                /*
                 * Difficulty
                 */

                String finaldifficutly = difficulty;

                if (difficulty != null) {
                    if (difficulty.equalsIgnoreCase("EASY")) {
                        world.setDifficulty(Difficulty.EASY);
                    } else if (difficulty.equalsIgnoreCase("HARD")) {
                        world.setDifficulty(Difficulty.HARD);
                    } else if (difficulty.equalsIgnoreCase("PEACEFUL")) {
                        world.setDifficulty(Difficulty.PEACEFUL);
                    } else {
                        world.setDifficulty(Difficulty.NORMAL);
                        finaldifficutly = "NORMAL";
                    }
                } else {
                    world.setDifficulty(Difficulty.NORMAL);
                    finaldifficutly = "NORMAL";
                }

                world.setGameRule(GameRule.DO_INSOMNIA, false);


                Island.restartmap.asMap().remove(uuid);
                Island.playerislands.put(uuid, worldName);
                CobblestoneGenerator.islandGenLevel.put(worldName, 0);
                CobblestoneGenerator.islandlevels.put(worldName, 0);
                Player player = plugin.getServer().getPlayer(uuid);
                if (player != null) {
                    player.teleportAsync(world.getSpawnLocation());
                }
                if (oldIsland != null) {
                    wm.unloadWorld(oldIsland);
                }
                databaseWriter.addIsland(worldName, uuid, finaldifficutly.toUpperCase());
                databaseWriter.updateDeathCount(uuid, 0);
                plugin.scoreboardmanager.updatePlayerScore(player.getName(), "deaths", 0);
            } else {
                ConfigShorts.messagefromString("FailedToCreateIsland", plugin.getServer().getPlayer(uuid));
            }
        });
    }
}

