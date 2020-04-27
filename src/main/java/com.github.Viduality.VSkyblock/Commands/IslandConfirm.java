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
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IslandConfirm implements SubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private WorldManager wm = new WorldManager();
    private DatabaseReader databaseReader = new DatabaseReader();

    @Override
    public void execute(DatabaseCache databaseCache) {
        if (Island.requestvisit.asMap().containsValue(databaseCache.getIslandId())) {
            int i = 0;
            List<UUID> players = new ArrayList<>();
            for (UUID currentrequest : Island.requestvisit.asMap().keySet()) {
                if (Island.requestvisit.asMap().get(currentrequest).equals(databaseCache.getIslandId())) {
                    i++;
                    players.add(currentrequest);
                }
            }
            if (i > 1) {
                if (databaseCache.getTargetPlayer() != null) {
                    if (databaseCache.getTargetPlayer().isOnline()) {
                        Player visitingplayer = (Player) databaseCache.getTargetPlayer();
                        if (players.contains(visitingplayer.getUniqueId())) {
                            teleportPlayer(visitingplayer, databaseCache.getIslandname(), databaseCache.getIslandId());
                        } else {
                            ConfigShorts.custommessagefromString("NoVisitRequestFromPlayer", databaseCache.getPlayer(), visitingplayer.getName());
                        }
                    } else {
                        ConfigShorts.custommessagefromString("PlayerNotOnline", databaseCache.getPlayer(), databaseCache.getName(), databaseCache.getArg());
                    }
                } else {
                    ConfigShorts.messagefromString("MultipleRequests", databaseCache.getPlayer());
                }
            } else {
                if (databaseCache.getTargetPlayer() != null) {
                    if (databaseCache.getTargetPlayer().isOnline()) {
                        Player visitingplayer = (Player) databaseCache.getTargetPlayer();
                        if (players.contains(visitingplayer.getUniqueId())) {
                            teleportPlayer(visitingplayer, databaseCache.getIslandname(), databaseCache.getIslandId());
                        } else {
                            ConfigShorts.custommessagefromString("NoVisitRequestFromPlayer", databaseCache.getPlayer(), visitingplayer.getName());
                        }
                    } else {
                        ConfigShorts.custommessagefromString("PlayerNotOnline", databaseCache.getPlayer(), databaseCache.getName(), databaseCache.getArg());
                    }
                } else {
                    OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(players.get(0));
                    if (offlinePlayer.isOnline()) {
                        Player visitingplayer = plugin.getServer().getPlayer(players.get(0));
                        teleportPlayer(visitingplayer, databaseCache.getIslandname(), databaseCache.getIslandId());
                    } else {
                        ConfigShorts.custommessagefromString("PlayerNotOnline", databaseCache.getPlayer(), databaseCache.getName(), databaseCache.getArg());
                    }
                }
            }
        } else {
            ConfigShorts.messagefromString("NoVisitRequest", databaseCache.getPlayer());
        }
    }


    private void teleportPlayer(Player player, String island, int islandid) {
        if (wm.getLoadedWorlds().contains(island)) {
            Location islandHome = Island.islandhomes.get(island);
            if (islandHome != null) {
                islandHome.getWorld().getChunkAtAsync(islandHome).whenComplete((c, e) -> {
                    if (e != null) {
                        e.printStackTrace();
                    }
                    if (c != null) {
                        player.teleportAsync(islandHome);
                        player.setCanCollide(false);
                        Island.requestvisit.asMap().remove(player.getUniqueId());
                        databaseReader.getIslandMembers(islandid, islandMembers -> {
                            for (String memberName : islandMembers) {
                                Player onlinePlayer = plugin.getServer().getPlayer(memberName);
                                if (onlinePlayer != null) {
                                    ConfigShorts.custommessagefromString("PlayerVisitingYourIsland", onlinePlayer, player.getName());
                                }
                            }
                        });
                    } else {
                        ConfigShorts.messagefromString("IslandSpawnNotSafe", player);
                        Island.requestvisit.asMap().remove(player.getUniqueId());
                    }
                });
            }
        } else {
            ConfigShorts.messagefromString("IslandSpawnNotSafe", player);
            Island.requestvisit.asMap().remove(player.getUniqueId());
        }
    }
}
