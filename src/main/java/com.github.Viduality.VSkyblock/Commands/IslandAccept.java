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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/*
 * Player can accept the invite of another player with this command. Can only be used when the
 * player is island owner and has no members on his island or if he isn't the island owner
 */
public class IslandAccept extends PlayerSubCommand {

    public IslandAccept(VSkyblock plugin) {
        super(plugin, "accept");
    }

    @Override
    public void execute(CommandSender sender, PlayerInfo playerInfo, String[] args) {
        Player player = playerInfo.getPlayer();
        UUID islandowneruuid = IslandCacheHandler.invitemap.getIfPresent(playerInfo.getUuid());
        if (islandowneruuid != null) {
            UUID newmemberuuid = playerInfo.getUuid();
            plugin.getDb().getReader().getIslandIdFromPlayer(islandowneruuid, (islandid) -> plugin.getDb().getReader().getIslandNameFromPlayer(islandowneruuid, (newisland) -> {
                if (playerInfo.isIslandOwner()) {
                    plugin.getDb().getReader().hasIslandMembers(playerInfo.getIslandId(), hasMembers -> {
                        if (!hasMembers) {

                            player.getInventory().clear();
                            player.getEnderChest().clear();
                            player.teleportAsync(IslandCacheHandler.islandhomes.get(newisland)).whenComplete((b, e) -> {
                                if (e != null) {
                                    e.printStackTrace();
                                }
                                player.setCollidable(true);
                                player.setSleepingIgnored(false);
                                plugin.getWorldManager().unloadWorld(playerInfo.getIslandName());
                                plugin.getDb().getReader().getIslandChallenges(playerInfo.getIslandId(), challenges -> {
                                    if (player.isOnline()) {
                                        plugin.getScoreboardManager().updateTracked(player, challenges);
                                    }
                                });
                            });

                            plugin.getDb().getWriter().updatePlayersIsland(newmemberuuid, islandid, false);
                            IslandCacheHandler.invitemap.invalidate(player.getUniqueId());
                            IslandCacheHandler.playerislands.put(player.getUniqueId(), newisland);
                            IslandCacheHandler.isjoincooldown.put(player.getUniqueId(), player.getUniqueId());
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
                    IslandCacheHandler.invitemap.invalidate(player.getUniqueId());
                    IslandCacheHandler.playerislands.put(player.getUniqueId(), newisland);
                    IslandCacheHandler.isjoincooldown.put(player.getUniqueId(), player.getUniqueId());
                    player.teleportAsync(IslandCacheHandler.islandhomes.get(newisland)).whenComplete((b, e) -> {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        if (playerInfo.getIslandName() != null) {
                            if (!IslandCacheHandler.playerislands.containsValue(playerInfo.getIslandName())) {
                                player.setCollidable(true);
                                player.setSleepingIgnored(false);
                                plugin.getWorldManager().unloadWorld(playerInfo.getIslandName());
                            }
                        }
                        plugin.getDb().getReader().getIslandChallenges(playerInfo.getIslandId(), challenges -> {
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
    }
}
