package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.Commands.WorldCommands.AdminSubCommand;
import com.github.Viduality.VSkyblock.SQLConnector;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class DeletePlayer implements AdminSubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private SQLConnector getDatabase = new SQLConnector();


    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (sender.hasPermission("VSkyblock.DeletePlayer")) {
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


                    UUID uuid = databaseCache.getUuid();
                    if (uuid != null) {
                        try {
                            PreparedStatement preparedStatement1;

                            preparedStatement1 = connection.prepareStatement("DELETE FROM VSkyblock_Player WHERE uuid = ?");
                            preparedStatement1.setString(1, uuid.toString());
                            preparedStatement1.executeUpdate();
                            preparedStatement1 = connection.prepareStatement("DELETE FROM VSkyblock_Challenges_Easy WHERE uuid = ?");
                            preparedStatement1.setString(1, uuid.toString());
                            preparedStatement1.executeUpdate();
                            preparedStatement1 = connection.prepareStatement("DELETE FROM VSkyblock_Challenges_Medium WHERE uuid = ?");
                            preparedStatement1.setString(1, uuid.toString());
                            preparedStatement1.executeUpdate();
                            preparedStatement1 = connection.prepareStatement("DELETE FROM VSkyblock_Challenges_Hard WHERE uuid = ?");
                            preparedStatement1.setString(1, uuid.toString());
                            preparedStatement1.executeUpdate();
                            preparedStatement1.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            getDatabase.closeConnection(connection);
                        }
                        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                ConfigShorts.custommessagefromString("DeletedPlayer", sender, args);
                                OfflinePlayer target = plugin.getServer().getOfflinePlayer(args);
                                if (target.isOnline()) {
                                    Player onlinetarget = (Player) target;
                                    onlinetarget.kickPlayer("Relog please");
                                }
                            }
                        });
                    } else {
                        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                ConfigShorts.messagefromString("PlayerDoesNotExist", sender);
                            }
                        });
                    }
                } else {
                    plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            ConfigShorts.messagefromString("PermissionLack", sender);
                        }
                    });
                }
            }
        });
    }
}
