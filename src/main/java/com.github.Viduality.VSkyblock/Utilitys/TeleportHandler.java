package com.github.Viduality.VSkyblock.Utilitys;

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
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeleportHandler {

    private final VSkyblock plugin = VSkyblock.getInstance();
    private final WorldManager wm = new WorldManager();

    public static Map<Player, BukkitTask> teleportQueue = new HashMap<>();
    public static HashMap<Player, Location> locations = new HashMap<>();

    public boolean teleportToIsland(Player player, Location location, boolean visit, List<String> islandMembers) {
        locations.put(player, location);
        if (player.getFallDistance() < 5) {
            if (location != null) {
                if (wm.getLoadedWorlds().contains(location.getWorld().getName())) {
                    if (!player.getLocation().getBlock().getType().equals(Material.LAVA)) {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            player.teleport(location);
                            if (visit) {
                                player.setCollidable(false);
                                player.setSleepingIgnored(true);
                                for (String memberName : islandMembers) {
                                    Player onlinePlayer = plugin.getServer().getPlayer(memberName);
                                    if (onlinePlayer != null) {
                                        ConfigShorts.custommessagefromString("PlayerVisitingYourIsland", onlinePlayer, player.getName());
                                    }
                                }
                                teleportQueue.remove(player);
                                locations.remove(player);
                            } else {
                                player.setCollidable(true);
                                player.setSleepingIgnored(false);
                                teleportQueue.remove(player);
                                locations.remove(player);
                            }
                        });
                        return true;
                    } else {
                        ConfigShorts.messagefromString("PlayerInLava", player);
                        teleportQueue.put(player, plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                            if (teleportQueue.containsKey(player)) {
                                teleportToIsland(player, location, visit, islandMembers);
                            }
                        }, 20 * 5));
                    }
                } else {
                    ConfigShorts.messagefromString("IslandSpawnNotSafe", player);
                    return false;
                }
            } else {
                ConfigShorts.messagefromString("IslandSpawnNotSafe", player);
                return false;
            }
        } else {
            ConfigShorts.messagefromString("WaitingForFall", player);
            teleportQueue.put(player, plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (teleportQueue.containsKey(player)) {
                    teleportToIsland(player, location, visit, islandMembers);
                }
            }, 20));
        }
        return false;
    }
}
