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

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IslandVisit implements SubCommand {

    private final VSkyblock plugin;

    public IslandVisit(VSkyblock plugin) {
        this.plugin = plugin;
    }


    @Override
    public void execute(DatabaseCache databaseCache) {
        Player player = databaseCache.getPlayer();
        Player onlinetarget = plugin.getServer().getPlayer(databaseCache.getArg());
        if (player != onlinetarget) {
            if (onlinetarget != null) {
                UUID uuid = onlinetarget.getUniqueId();
                plugin.getDb().getReader().getislandidfromplayer(uuid, islandId -> plugin.getDb().getReader().getIslandMembers(islandId, islandMembers -> {
                    if (!islandMembers.contains(player.getName())) {
                        plugin.getDb().getReader().isislandvisitable(islandId, isVisitable -> {
                            if (isVisitable) {
                                plugin.getDb().getReader().islandneedsrequestforvisit(islandId, needsRequest -> {
                                    if (needsRequest) {
                                        Island.requestvisit.put(player.getUniqueId(), islandId);
                                        ConfigShorts.messagefromString("SendVisitRequest", player);
                                        for (String memberName : islandMembers) {
                                            Player onlinePlayer = plugin.getServer().getPlayer(memberName);
                                            if (onlinePlayer != null) {
                                                ConfigShorts.custommessagefromString("PlayerWantsToVisitYourIsland", onlinePlayer, player.getName());
                                            }
                                        }
                                    } else {
                                        plugin.getDb().getReader().getislandnamefromplayer(uuid, islandName -> {
                                            if (plugin.getWorldManager().getLoadedWorlds().contains(islandName)) {
                                                Location islandHome = Island.islandhomes.get(islandName);
                                                if (islandHome != null) {
                                                    islandHome.getWorld().getChunkAtAsync(islandHome).whenComplete((c, e) -> {
                                                        if (e != null) {
                                                            e.printStackTrace();
                                                        }
                                                        if (c != null) {
                                                            plugin.teleportToIsland(player, islandHome, true, islandMembers);
                                                        } else {
                                                            ConfigShorts.messagefromString("IslandSpawnNotSafe", player);
                                                        }
                                                    });
                                                }
                                            } else {
                                                ConfigShorts.messagefromString("IslandSpawnNotSafe", player);
                                            }
                                        });
                                    }
                                });
                            } else {
                                ConfigShorts.messagefromString("CannotVisitIsland", player);
                            }
                        });
                    } else {
                        ConfigShorts.messagefromString("VisitYourself", player);
                    }
                }));
            } else {
                ConfigShorts.custommessagefromString("PlayerNotOnline", player, player.getName(), databaseCache.getArg());
            }
        } else {
            ConfigShorts.messagefromString("VisitYourself", player);
        }
    }
}
