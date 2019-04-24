package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.SQLConnector;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
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

    private VSkyblock plugin = VSkyblock.getInstance();
    private SQLConnector getDatabase = new SQLConnector();
    private DatabaseReader databaseReader = new DatabaseReader();


    @Override
    public void execute(DatabaseCache databaseCache) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Player player = databaseCache.getPlayer();
                OfflinePlayer target = plugin.getServer().getOfflinePlayer(databaseCache.getArg());
                if (databaseCache.isIslandowner()) {
                    databaseReader.getIslandMembers(databaseCache.getIslandId(), new DatabaseReader.CallbackList() {
                        @Override
                        public void onQueryDone(List<String> result) {
                            if (result.size() < getislandplayerlimit()) {
                                if (target.isOnline()) {
                                    if (target != player) {
                                        List<String> members = new ArrayList<>();
                                        Connection connection = getDatabase.getConnection();
                                        try {
                                            PreparedStatement preparedStatement;
                                            preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Player WHERE islandid = ?");
                                            preparedStatement.setInt(1, databaseCache.getIslandId());
                                            ResultSet resultSet = preparedStatement.executeQuery();
                                            while (resultSet.next()) {
                                                members.add(resultSet.getString("uuid"));
                                            }
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        } finally {
                                            getDatabase.closeConnection(connection);
                                        }
                                        if (!members.contains(target.getUniqueId().toString())) {

                                            ConfigShorts.custommessagefromString("InviteToIsland", player, player.getName(), target.getName());
                                            Island.invitemap.put(target.getUniqueId(), player.getUniqueId());

                                            ConfigShorts.custommessagefromString("GetInviteToIsland", (Player) target, player.getName(), target.getName());
                                            ConfigShorts.messagefromString("HowToAcceptInvite", (Player) target);
                                        } else {
                                            ConfigShorts.messagefromString("AlreadyIslandMember", player);
                                        }
                                    } else {
                                        ConfigShorts.messagefromString("InviteYourself", player);
                                    }
                                } else {
                                    ConfigShorts.custommessagefromString("PlayerNotOnline", player, player.getName(), databaseCache.getArg());
                                }
                            } else {
                                ConfigShorts.messagefromString("PlayerLimitReached", player);
                            }
                        }
                    });

                } else {
                    ConfigShorts.messagefromString("NotIslandOwner", player);
                }
            }
        });
    }

    private Integer getislandplayerlimit() {
        Integer islandplayerlimit = 4;
        if (isInt(plugin.getConfig().getString("IslandPlayerLimit"))) {
            islandplayerlimit = plugin.getConfig().getInt("IslandPlayerLimit");
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
