package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.Commands.WorldCommands.AdminSubCommand;
import com.github.Viduality.VSkyblock.SQLConnector;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResetChallenges implements AdminSubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private SQLConnector getDatabase = new SQLConnector();


    @Override
    public void execute(Player player, String args, String option1, String option2) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (player.hasPermission("VSkyblock.ResetChallenges")) {
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
                            for (int i = 0; i < 3; i++) {
                                for (int x = 1; x < 19; x++) {
                                    String c = "c" + x;
                                    String table;
                                    if (i == 0) {
                                        table = "VSkyblock_Challenges_Easy";
                                    } else if (i == 1) {
                                        table = "VSkyblock_Challenges_Medium";
                                    } else {
                                        table = "VSkyblock_Challenges_Hard";
                                    }

                                    PreparedStatement resetChallenges;
                                    resetChallenges = connection.prepareStatement("UPDATE " + table + " SET " + c + " = 0 WHERE uuid = ?");
                                    resetChallenges.setString(1, uuid);
                                    resetChallenges.executeUpdate();
                                    resetChallenges.close();
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            getDatabase.closeConnection(connection);
                        }
                        ConfigShorts.custommessagefromString("ResettedChallenges", player, args);
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
