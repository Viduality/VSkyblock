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

import com.github.Viduality.VSkyblock.Utilitys.*;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IslandAccept implements SubCommand {

    private final VSkyblock plugin = VSkyblock.getInstance();

    @Override
    public void execute(DatabaseCache databaseCache) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Player player = databaseCache.getPlayer();
            if (Island.invitemap.asMap().containsKey(databaseCache.getUuid())) {
                UUID newmemberuuid = databaseCache.getUuid();
                UUID islandowneruuid = Island.invitemap.asMap().get(databaseCache.getUuid());
                plugin.getDb().getReader().getIslandIdFromPlayer(islandowneruuid, (islandid) -> plugin.getDb().getReader().getIslandNameFromPlayer(islandowneruuid, (newisland) -> {
                    if (databaseCache.isIslandowner()) {
                        plugin.getDb().getReader().hasIslandMembers(databaseCache.getIslandId(), hasMembers -> {
                            if (!hasMembers) {

                                player.getInventory().clear();
                                player.getEnderChest().clear();
                                player.teleportAsync(Island.islandhomes.get(newisland)).whenComplete((b, e) -> {
                                    if (e != null) {
                                        e.printStackTrace();
                                    }
                                    player.setCollidable(true);
                                    player.setSleepingIgnored(false);
                                    plugin.getWorldManager().unloadWorld(databaseCache.getIslandname());
                                    plugin.getDb().getReader().getIslandChallenges(databaseCache.getIslandId(), challenges -> {
                                        if (player.isOnline()) {
                                            plugin.getScoreboardManager().updateTracked(player, challenges);
                                        }
                                    });
                                });

                                plugin.getDb().getWriter().updatePlayersIsland(newmemberuuid, islandid, false);
                                Island.invitemap.asMap().remove(player.getUniqueId());
                                Island.playerislands.put(player.getUniqueId(), newisland);
                                Island.isjoincooldown.put(player.getUniqueId(), player.getUniqueId());
                                plugin.getDb().getWriter().updateDeathCount(newmemberuuid, 0);
                                plugin.scoreboardmanager.updatePlayerScore(player.getName(), "deaths", 0);

                            } else {
                                ConfigShorts.messagefromString("HasIslandMembers", player);
                            }
                        });
                    } else {
                        player.getInventory().clear();
                        player.getEnderChest().clear();
                        plugin.getDb().getWriter().updatePlayersIsland(newmemberuuid, islandid, false);
                        Island.invitemap.asMap().remove(player.getUniqueId());
                        Island.playerislands.put(player.getUniqueId(), newisland);
                        Island.isjoincooldown.put(player.getUniqueId(), player.getUniqueId());
                        player.teleportAsync(Island.islandhomes.get(newisland)).whenComplete((b, e) -> {
                            if (e != null) {
                                e.printStackTrace();
                            }
                            if (databaseCache.getIslandname() != null) {
                                if (!Island.playerislands.containsValue(databaseCache.getIslandname())) {
                                    player.setCollidable(true);
                                    player.setSleepingIgnored(false);
                                    plugin.getWorldManager().unloadWorld(databaseCache.getIslandname());
                                }
                            }
                            plugin.getDb().getReader().getIslandChallenges(databaseCache.getIslandId(), challenges -> {
                                if (player.isOnline()) {
                                    plugin.getScoreboardManager().updateTracked(player, challenges);
                                }
                            });
                        });
                    }
                }));
            } else {
                ConfigShorts.messagefromString("NoPendingInvite", player);
            }
        });
    }
}
