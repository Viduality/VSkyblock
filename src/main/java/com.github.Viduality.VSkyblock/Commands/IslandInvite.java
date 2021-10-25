package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IslandInvite implements SubCommand {

    private final VSkyblock plugin;

    public IslandInvite(VSkyblock plugin) {
        this.plugin = plugin;
    }


    @Override
    public void execute(ExecutionInfo execution) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerInfo playerInfo = execution.getPlayerInfo();
            Player player = playerInfo.getPlayer();
            OfflinePlayer target = plugin.getServer().getOfflinePlayer(execution.getArg());
            if (playerInfo.isIslandOwner()) {
                plugin.getDb().getReader().getIslandMembers(playerInfo.getIslandId(), result -> {
                    if (result.size() <= getislandplayerlimit()) {
                        if (target.isOnline()) {
                            Player onlinetarget = (Player) target;
                            if (onlinetarget != player) {
                                List<String> members = new ArrayList<>();
                                try (Connection connection = plugin.getDb().getConnection()) {
                                    PreparedStatement preparedStatement;
                                    preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Player WHERE islandid = ?");
                                    preparedStatement.setInt(1, playerInfo.getIslandId());
                                    ResultSet resultSet = preparedStatement.executeQuery();
                                    while (resultSet.next()) {
                                        members.add(resultSet.getString("uuid"));
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                if (!members.contains(onlinetarget.getUniqueId().toString())) {
                                    if (!Island.isjoincooldown.asMap().containsKey(onlinetarget.getUniqueId())) {
                                        ConfigShorts.custommessagefromString("InviteToIsland", player, player.getName(), onlinetarget.getName());
                                        Island.invitemap.put(onlinetarget.getUniqueId(), player.getUniqueId());

                                        ConfigShorts.custommessagefromString("GetInviteToIsland", onlinetarget, player.getName(), onlinetarget.getName());
                                        ConfigShorts.messagefromString("HowToAcceptInvite", onlinetarget);
                                    } else {
                                        ConfigShorts.custommessagefromString("IslandJoinCooldown", player, String.valueOf(Island.getisjoincooldown()));
                                    }
                                } else {
                                    ConfigShorts.messagefromString("AlreadyIslandMember", player);
                                }
                            } else {
                                ConfigShorts.messagefromString("InviteYourself", player);
                            }
                        } else {
                            ConfigShorts.custommessagefromString("PlayerNotOnline", player, player.getName(), execution.getArg());
                        }
                    } else {
                        ConfigShorts.messagefromString("PlayerLimitReached", player);
                    }
                });

            } else {
                ConfigShorts.messagefromString("NotIslandOwner", player);
            }
        });
    }

    private Integer getislandplayerlimit() {
        int islandplayerlimit = 4;
        if (isInt(ConfigShorts.getDefConfig().getString("IslandPlayerLimit"))) {
            islandplayerlimit = ConfigShorts.getDefConfig().getInt("IslandPlayerLimit");
        }
        return islandplayerlimit;
    }

    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
