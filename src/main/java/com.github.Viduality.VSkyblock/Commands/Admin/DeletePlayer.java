package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.Commands.WorldCommands.AdminSubCommand;
import com.github.Viduality.VSkyblock.SQLConnector;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DeletePlayer implements AdminSubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private SQLConnector getDatabase = new SQLConnector();


    @Override
    public void execute(Player player, String args, String option1, String option2) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (player.hasPermission("VSkyblock.DeletePlayer")) {
                    Player player = plugin.getServer().getPlayer(args);
                    DatabaseCache databaseCache = new DatabaseCache();
                    Connection connection = getDatabase.getConnection();

                    PreparedStatement preparedStatement;
                    try {
                        preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Player WHERE playername = ?");
                        preparedStatement.setString(1, args);
                        ResultSet r = preparedStatement.executeQuery();
                        while (r.next()) {
                            databaseCache.setUuid(r.getString("uuid"));
                        }
                        preparedStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                    String uuid = databaseCache.getuuid();
                    if (uuid != null) {
                        try {
                            PreparedStatement preparedStatement1;

                            preparedStatement1 = connection.prepareStatement("DELETE FROM VSkyblock_Player WHERE uuid = ?");
                            preparedStatement1.setString(1, uuid);
                            preparedStatement1.executeUpdate();
                            preparedStatement1 = connection.prepareStatement("DELETE FROM VSkyblock_Challenges_Easy WHERE uuid = ?");
                            preparedStatement1.setString(1, uuid);
                            preparedStatement1.executeUpdate();
                            preparedStatement1 = connection.prepareStatement("DELETE FROM VSkyblock_Challenges_Medium WHERE uuid = ?");
                            preparedStatement1.setString(1, uuid);
                            preparedStatement1.executeUpdate();
                            preparedStatement1 = connection.prepareStatement("DELETE FROM VSkyblock_Challenges_Hard WHERE uuid = ?");
                            preparedStatement1.setString(1, uuid);
                            preparedStatement1.executeUpdate();
                            preparedStatement1.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            getDatabase.closeConnection(connection);
                        }
                        ConfigShorts.custommessagefromString("DeletedPlayer", player, args);
                        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                OfflinePlayer target = plugin.getServer().getOfflinePlayer(args);
                                if (target.isOnline()) {
                                    Player onlinetarget = (Player) target;
                                    onlinetarget.kickPlayer("Relog please");
                                }
                            }
                        });
                    } else {
                        ConfigShorts.messagefromString("PlayerDoesNotExist", player);
                    }
                } else {
                    ConfigShorts.messagefromString("PermissionLack", player);
                }
            }
        });
    }
}
