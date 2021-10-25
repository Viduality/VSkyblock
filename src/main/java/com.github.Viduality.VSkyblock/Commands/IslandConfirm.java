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
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IslandConfirm implements SubCommand {

    private final VSkyblock plugin;

    public IslandConfirm(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ExecutionInfo execution) {
        PlayerInfo playerInfo = execution.getPlayerInfo();
        if (Island.requestvisit.asMap().containsValue(playerInfo.getIslandId())) {
            int i = 0;
            List<UUID> players = new ArrayList<>();
            for (UUID currentrequest : Island.requestvisit.asMap().keySet()) {
                if (Island.requestvisit.asMap().get(currentrequest).equals(playerInfo.getIslandId())) {
                    i++;
                    players.add(currentrequest);
                }
            }
            if (i > 1) {
                if (execution.getTargetPlayer() != null) {
                    if (execution.getTargetPlayer().isOnline()) {
                        Player visitingplayer = (Player) execution.getTargetPlayer();
                        if (players.contains(visitingplayer.getUniqueId())) {
                            teleportPlayer(visitingplayer, playerInfo.getIslandName(), playerInfo.getIslandId());
                        } else {
                            ConfigShorts.custommessagefromString("NoVisitRequestFromPlayer", playerInfo.getPlayer(), visitingplayer.getName());
                        }
                    } else {
                        ConfigShorts.custommessagefromString("PlayerNotOnline", playerInfo.getPlayer(), playerInfo.getName(), execution.getArg());
                    }
                } else {
                    ConfigShorts.messagefromString("MultipleRequests", playerInfo.getPlayer());
                }
            } else {
                if (execution.getTargetPlayer() != null) {
                    if (execution.getTargetPlayer().isOnline()) {
                        Player visitingplayer = (Player) execution.getTargetPlayer();
                        if (players.contains(visitingplayer.getUniqueId())) {
                            teleportPlayer(visitingplayer, playerInfo.getIslandName(), playerInfo.getIslandId());
                        } else {
                            ConfigShorts.custommessagefromString("NoVisitRequestFromPlayer", playerInfo.getPlayer(), visitingplayer.getName());
                        }
                    } else {
                        ConfigShorts.custommessagefromString("PlayerNotOnline", playerInfo.getPlayer(), playerInfo.getName(), execution.getArg());
                    }
                } else {
                    OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(players.get(0));
                    if (offlinePlayer.isOnline()) {
                        Player visitingplayer = plugin.getServer().getPlayer(players.get(0));
                        teleportPlayer(visitingplayer, playerInfo.getIslandName(), playerInfo.getIslandId());
                    } else {
                        ConfigShorts.custommessagefromString("PlayerNotOnline", playerInfo.getPlayer(), playerInfo.getName(), execution.getArg());
                    }
                }
            }
        } else {
            ConfigShorts.messagefromString("NoVisitRequest", playerInfo.getPlayer());
        }
    }


    private void teleportPlayer(Player player, String island, int islandid) {
        if (plugin.getWorldManager().getLoadedWorlds().contains(island)) {
            Location islandHome = Island.islandhomes.get(island);
            if (islandHome != null) {
                islandHome.getWorld().getChunkAtAsync(islandHome).whenComplete((c, e) -> {
                    if (e != null) {
                        e.printStackTrace();
                    }
                    if (c != null) {
                        plugin.getDb().getReader().getIslandMembers(islandid, islandMembers -> {
                            plugin.teleportToIsland(player, islandHome, true, islandMembers);
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
