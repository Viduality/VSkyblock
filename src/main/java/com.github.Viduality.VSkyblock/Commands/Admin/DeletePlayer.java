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


public class DeletePlayer implements CommandExecutor {

    private VSkyblock plugin = VSkyblock.getInstance();
    private SQLConnector getDatabase = new SQLConnector();
    private VSkyblock deletePlayer;

    public DeletePlayer(VSkyblock deletePlayer) {this.deletePlayer = deletePlayer;}


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (cmd.getName().equalsIgnoreCase("VSkydelete")) {
                    if (sender.hasPermission("VSkyblock.VSkydelete")) {
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
                                sender.sendMessage(ChatColor.GREEN + "Successfully deleted all data from player " + args[0]);
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
