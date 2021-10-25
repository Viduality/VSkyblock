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

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.*;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.github.Viduality.VSkyblock.WorldGenerator.MasterIslandGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayDeque;
import java.util.Deque;

public class PlayerJoinListener implements Listener {

    private final VSkyblock plugin;

    private final Deque<DatabaseCache> toLoad = new ArrayDeque<>();

    public PlayerJoinListener(VSkyblock plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();

        if (plugin.getWorldManager().getUnloadedWorlds().contains(ConfigShorts.getDefConfig().getString("SpawnWorld"))) {
            plugin.getWorldManager().loadWorld(ConfigShorts.getDefConfig().getString("SpawnWorld"));
        }

        if (plugin.getWorldManager().getUnloadedWorlds().contains(ConfigShorts.getDefConfig().getString("NetherWorld"))) {
            plugin.getWorldManager().loadWorld(ConfigShorts.getDefConfig().getString("NetherWorld"));
        }


        plugin.getDb().getReader().getPlayerData(player.getUniqueId().toString(), result -> {
            if (result.getUuid() == null) {
                plugin.getDb().getWriter().addPlayer(player.getUniqueId(), player.getName());
            } else if (player.isOnline()) {
                if (!result.getName().equals(player.getName())) {
                    plugin.getDb().getWriter().updatePlayerName(player.getUniqueId(), player.getName());
                }
                if (plugin.scoreboardmanager.doesobjectiveexist("deaths")) {
                    if (plugin.scoreboardmanager.addPlayerToObjective(player, "deaths")) {
                        plugin.scoreboardmanager.updatePlayerScore(player.getName(), "deaths", result.getDeathCount());
                    }
                }
                if (result.getIslandname() != null) {
                    PotionEffect potionEffectBlindness = new PotionEffect(PotionEffectType.BLINDNESS, 600, 1);
                    PotionEffect potionEffectNightVision = new PotionEffect(PotionEffectType.NIGHT_VISION, 600, 1);
                    player.addPotionEffect(potionEffectBlindness);
                    player.addPotionEffect(potionEffectNightVision);
                    Location location = player.getLocation();
                    location.setPitch(-90);
                    player.teleport(location);
                    BukkitTask task = Island.emptyloadedislands.remove(result.getIslandname());
                    if (task != null) {
                        task.cancel();
                    }
                    plugin.getDb().getReader().getIslandChallenges(result.getIslandId(), challenges -> {
                        if (player.isOnline()) {
                            plugin.getScoreboardManager().updateTracked(player, challenges);
                        }
                    });
                    toLoad.add(result);
                    if (toLoad.size() == 1) {
                        loadWorld(result);
                    }
                } else {
                    player.teleportAsync(plugin.getWorldManager().getSpawnLocation(ConfigShorts.getDefConfig().getString("SpawnWorld"))).whenComplete((b, e) -> {
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    });
                    if (result.isKicked()) {
                        ConfigShorts.messagefromString("KickedFromIslandOffline", player);
                        player.setTotalExperience(0);
                        player.setExp(0);
                        player.getEnderChest().clear();
                        player.getInventory().clear();
                        plugin.getDb().getWriter().removeKicked(result.getUuid());
                    }
                }

            }
        });


        if (plugin.getServer().getWorld(MasterIslandGenerator.WORLD_NAME) == null && !plugin.getWorldManager().getUnloadedWorlds().contains(MasterIslandGenerator.WORLD_NAME)) {
            ConfigShorts.broadcastfromString("MasterIsland");
            new MasterIslandGenerator(plugin).create(result -> {
                plugin.getWorldManager().unloadWorld(result);
                ConfigShorts.broadcastfromString("MasterIslandReady");
            });
        }
    }

    private void loadWorld(DatabaseCache result) {
        if (!Island.playerislands.containsValue(result.getIslandname())) {
            plugin.getDb().getReader().addToCobbleStoneGenerators(result.getIslandname());
        }
        if (!plugin.getWorldManager().loadWorld(result.getIslandname())) {
            ConfigShorts.custommessagefromString("WorldFailedToLoad", result.getPlayer(), result.getIslandname());
            toLoad.remove(result);
            DatabaseCache nextResult = toLoad.peekFirst();
            if (nextResult != null) {
                loadWorld(nextResult);
            }
            return;
        }
        Island.playerislands.put(result.getUuid(), result.getIslandname());
        plugin.getDb().getReader().getIslandSpawn(result.getIslandname(), islandspawn -> {
            if (!Island.islandhomes.containsKey(result.getIslandname())) {
                Island.islandhomes.put(result.getIslandname(), islandspawn);
            }
            plugin.getDb().getReader().getLastLocation(result.getUuid(), loc -> {
                Player player = result.getPlayer();
                if (player != null) {
                    if (loc == null) {
                        loc = islandspawn;
                    }
                    Location finalLoc = loc;
                    loc.getWorld().getChunkAtAsync(loc).whenComplete((c, e) -> {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                        if (c != null) {
                            Block below = finalLoc.getBlock().getRelative(BlockFace.DOWN);
                            if (below.getType() == Material.AIR) {
                                below.setType(Material.INFESTED_COBBLESTONE);
                            }
                            player.teleport(finalLoc);
                        } else {
                            ConfigShorts.custommessagefromString("WorldFailedToLoad", player, result.getIslandname());
                        }
                    });
                } else {
                    Island.emptyloadedislands.put(result.getIslandname(), plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        plugin.getWorldManager().unloadWorld(result.getIslandname());
                        Island.islandhomes.remove(result.getIslandname());
                    }, 20 * 60));
                }
                toLoad.remove(result);
                DatabaseCache nextResult = toLoad.peekFirst();
                if (nextResult != null) {
                    loadWorld(nextResult);
                }
            });
        });
    }
}
