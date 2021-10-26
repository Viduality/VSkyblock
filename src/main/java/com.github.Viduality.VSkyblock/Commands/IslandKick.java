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
import com.github.Viduality.VSkyblock.Utilitys.IslandCacheHandler;
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/*
 * Kicks a player from your island. Executing player has to be the island owner.
 */
public class IslandKick extends PlayerSubCommand {

    public IslandKick(VSkyblock plugin) {
        super(plugin, "kick");
    }

    @Override
    public void execute(CommandSender sender, PlayerInfo playerInfo, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.AQUA + "/is kick <Player>");
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Player player = playerInfo.getPlayer();
            // TODO: Replace with database query
            OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);
            if (target != player) {
                UUID targetuuid = target.getUniqueId();
                Set<UUID> members = new LinkedHashSet<>();
                try (Connection connection = plugin.getDb().getConnection()) {
                    PreparedStatement preparedStatement;
                    preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Player WHERE islandid = ?");
                    preparedStatement.setInt(1, playerInfo.getIslandId());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        members.add(UUID.fromString(resultSet.getString("uuid")));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (members.contains(targetuuid)) {
                        if (playerInfo.isIslandOwner()) {
                            plugin.getDb().getWriter().kickPlayerfromIsland(targetuuid);
                            ConfigShorts.custommessagefromString("KickedMember", player, player.getName(), target.getName());
                            Player onlinetarget = target.getPlayer();
                            if (onlinetarget != null) {
                                ConfigShorts.messagefromString("KickedFromIsland", onlinetarget);
                                onlinetarget.getInventory().clear();
                                onlinetarget.getEnderChest().clear();
                                onlinetarget.setExp(0);
                                onlinetarget.setTotalExperience(0);
                                onlinetarget.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
                                onlinetarget.teleportAsync(plugin.getWorldManager().getSpawnLocation(ConfigShorts.getDefConfig().getString("SpawnWorld")));
                                plugin.getDb().getWriter().removeKicked(targetuuid);
                                IslandCacheHandler.playerislands.remove(targetuuid);
                            }
                        } else {
                            ConfigShorts.messagefromString("NotIslandOwner", player);
                        }
                    } else {
                        Player onlinetarget = target.getPlayer();
                        if (onlinetarget != null) {
                            if (!onlinetarget.hasPermission("VSkyblock.IgnoreKick")) {
                                onlinetarget.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
                                if (onlinetarget.getWorld().getName().equals(playerInfo.getIslandName())) {
                                    onlinetarget.teleportAsync(plugin.getWorldManager().getSpawnLocation(ConfigShorts.getDefConfig().getString("SpawnWorld")));
                                    ConfigShorts.messagefromString("KickVisitingPlayer", onlinetarget);
                                } else {
                                    ConfigShorts.messagefromString("PlayerNotIslandMember", player);
                                }
                            } else {
                                ConfigShorts.messagefromString("PlayerNotIslandMember", player);
                            }
                        } else {
                            ConfigShorts.messagefromString("PlayerNotIslandMember", player);
                        }
                    }
                });
            } else {
                ConfigShorts.messagefromString("CantKickYourself", player);
            }
        });
    }
}
