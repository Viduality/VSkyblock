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

import com.github.Viduality.VSkyblock.Utilitys.IslandCacheHandler;
import com.github.Viduality.VSkyblock.Listener.CobblestoneGenerator;
import com.github.Viduality.VSkyblock.Utilitys.*;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class IslandCreator {

    private final VSkyblock plugin;
    private final UUID playerId;

    private String oldIsland = null;

    public IslandCreator(VSkyblock plugin, UUID playerId) {
        this.plugin = plugin;
        this.playerId = playerId;
    }

    public IslandCreator oldIsland(String oldIsland) {
        this.oldIsland = oldIsland;
        return this;
    }

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
     */
    public void createNewIsland() {
        String worldsizeString = ConfigShorts.getDefConfig().getString("WorldSize");
        String difficulty = ConfigShorts.getDefConfig().getString("Difficulty");
        plugin.getDb().getReader().getLatestIsland((worldName) -> {
            Player player = plugin.getServer().getPlayer(playerId);
            if (worldName != null) {
                if (!plugin.getWorldManager().createIsland(worldName)) {
                    ConfigShorts.messagefromString("FailedToCreateIsland", player);
                    plugin.getLogger().severe("Failed to create new island for " + playerId + " with id " + worldName);
                    return;
                }


                /*
                 * World Size
                 */

                World world = plugin.getServer().getWorld(worldName);
                if (world == null) {
                    ConfigShorts.messagefromString("FailedToCreateIsland", player);
                    plugin.getLogger().severe("Could not create new island for " + playerId + " properly as we don't know the world?");
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


                IslandCacheHandler.restartmap.asMap().remove(playerId);
                IslandCacheHandler.playerislands.put(playerId, worldName);
                IslandCacheHandler.islandhomes.put(worldName, world.getSpawnLocation());
                CobblestoneGenerator.islandGenLevel.put(worldName, 0);
                CobblestoneGenerator.islandlevels.put(worldName, 0);
                if (player != null) {
                    player.teleportAsync(world.getSpawnLocation()).thenAccept(b -> {
                        player.getInventory().clear();
                        player.getEnderChest().clear();
                        player.setExp(0);
                        player.setTotalExperience(0);
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    });
                }
                if (oldIsland != null) {
                    plugin.getWorldManager().unloadWorld(oldIsland);
                }
                plugin.getDb().getWriter().addIsland(worldName, playerId, finaldifficutly.toUpperCase());
                plugin.getDb().getWriter().updateDeathCount(playerId, 0);
                plugin.scoreboardmanager.updatePlayerScore(player.getName(), "deaths", 0);
            } else {
                ConfigShorts.messagefromString("FailedToCreateIsland", plugin.getServer().getPlayer(playerId));
            }
        });
    }
}

