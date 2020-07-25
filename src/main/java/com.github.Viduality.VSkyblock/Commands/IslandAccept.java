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
    private final DatabaseReader databaseReader = new DatabaseReader();
    private final DatabaseWriter databaseWriter = new DatabaseWriter();
    private final WorldManager wm = new WorldManager();



    @Override
    public void execute(DatabaseCache databaseCache) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Player player = databaseCache.getPlayer();
            if (Island.invitemap.asMap().containsKey(databaseCache.getUuid())) {
                UUID newmemberuuid = databaseCache.getUuid();
                UUID islandowneruuid = Island.invitemap.asMap().get(databaseCache.getUuid());
                databaseReader.getislandidfromplayer(islandowneruuid, (islandid) -> databaseReader.getislandnamefromplayer(islandowneruuid, (newisland) -> {
                    if (databaseCache.isIslandowner()) {
                        databaseReader.hasislandmembers(databaseCache.getIslandId(), result1 -> {
                            if (!result1) {

                                player.getInventory().clear();
                                player.getEnderChest().clear();
                                player.teleportAsync(Island.islandhomes.get(newisland)).whenComplete((b, e) -> {
                                    player.setCollidable(true);
                                    player.setSleepingIgnored(false);
                                    wm.unloadWorld(databaseCache.getIslandname());
                                });

                                databaseWriter.updatePlayersIsland(newmemberuuid, islandid, false);
                                Island.invitemap.asMap().remove(player.getUniqueId());
                                Island.playerislands.put(player.getUniqueId(), newisland);
                                Island.isjoincooldown.put(player.getUniqueId(), player.getUniqueId());
                                databaseWriter.updateDeathCount(newmemberuuid, 0);
                                plugin.scoreboardmanager.updatePlayerScore(player.getName(), "deaths", 0);

                            } else {
                                ConfigShorts.messagefromString("HasIslandMembers", player);
                            }
                        });
                    } else {
                        player.getInventory().clear();
                        player.getEnderChest().clear();
                        databaseWriter.updatePlayersIsland(newmemberuuid, islandid, false);
                        Island.invitemap.asMap().remove(player.getUniqueId());
                        Island.playerislands.put(player.getUniqueId(), newisland);
                        Island.isjoincooldown.put(player.getUniqueId(), player.getUniqueId());
                        player.teleportAsync(Island.islandhomes.get(newisland)).whenComplete((b, e) -> {
                            if (databaseCache.getIslandname() != null) {
                                if (!Island.playerislands.containsValue(databaseCache.getIslandname())) {
                                    player.setCollidable(true);
                                    player.setSleepingIgnored(false);
                                    wm.unloadWorld(databaseCache.getIslandname());
                                }
                            }
                            if (e != null) {
                                e.printStackTrace();
                            }
                        });
                    }
                }));
            } else {
                ConfigShorts.messagefromString("NoPendingInvite", player);
            }
        });
    }
}
