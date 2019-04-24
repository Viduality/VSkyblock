package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.SQLConnector;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
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

public class ResetChallenges implements CommandExecutor {

    private VSkyblock plugin = VSkyblock.getInstance();
    private SQLConnector getDatabase = new SQLConnector();
    private VSkyblock resetChallenges;

    public ResetChallenges(VSkyblock resetChallenges) {this.resetChallenges = resetChallenges;}


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (cmd.getName().equalsIgnoreCase("VSkyResetChallenges")) {
                    if (sender.hasPermission("VSkyblock.VSkyResetChallenges")) {
                        if (args.length == 1) {
                            Player player = plugin.getServer().getPlayer(args[0]);
                            DatabaseCache databaseCache = new DatabaseCache();
                            Connection connection = getDatabase.getConnection();

                            PreparedStatement preparedStatement;
                            try {
                                preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Player WHERE playername = ?");
                                preparedStatement.setString(1, args[0]);
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
                                            resetChallenges = connection.prepareStatement("UPDATE " + table + " SET " + c + "  = 0 WHERE uuid = ?");
                                            resetChallenges.setString(1, uuid);
                                        }
                                    }
                                    sender.sendMessage(ChatColor.GREEN + "Successfully reset challenges for player " + args[0]);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                } finally {
                                    getDatabase.closeConnection(connection);
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "Player does not exist");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "False Input!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have permission for this command!");
                    }
                }
            }
        });
        return true;
    }
}
