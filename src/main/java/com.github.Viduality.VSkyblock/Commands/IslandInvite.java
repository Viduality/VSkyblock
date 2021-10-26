package com.github.Viduality.VSkyblock.Commands;

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
import java.util.ArrayList;
import java.util.List;

/*
 * Invites another player to your island if the player is the island owner.
 */
public class IslandInvite extends PlayerSubCommand {

    public IslandInvite(VSkyblock plugin) {
        super(plugin, "invite");
    }

    @Override
    public void execute(CommandSender sender, PlayerInfo playerInfo, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.AQUA + "/is invite <Player>");
            return;
        }
        if (playerInfo.isIslandOwner()) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                Player player = playerInfo.getPlayer();
                OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);
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
                                    if (!IslandCacheHandler.isjoincooldown.asMap().containsKey(onlinetarget.getUniqueId())) {
                                        ConfigShorts.custommessagefromString("InviteToIsland", player, player.getName(), onlinetarget.getName());
                                        IslandCacheHandler.invitemap.put(onlinetarget.getUniqueId(), player.getUniqueId());

                                        ConfigShorts.custommessagefromString("GetInviteToIsland", onlinetarget, player.getName(), onlinetarget.getName());
                                        ConfigShorts.messagefromString("HowToAcceptInvite", onlinetarget);
                                    } else {
                                        ConfigShorts.custommessagefromString("IslandJoinCooldown", player, String.valueOf(IslandCacheHandler.getIsJoinCooldown()));
                                    }
                                } else {
                                    ConfigShorts.messagefromString("AlreadyIslandMember", player);
                                }
                            } else {
                                ConfigShorts.messagefromString("InviteYourself", player);
                            }
                        } else {
                            ConfigShorts.custommessagefromString("PlayerNotOnline", player, player.getName(), args[0]);
                        }
                    } else {
                        ConfigShorts.messagefromString("PlayerLimitReached", player);
                    }
                });
            });
        } else {
            ConfigShorts.messagefromString("NotIslandOwner", sender);
        }
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
