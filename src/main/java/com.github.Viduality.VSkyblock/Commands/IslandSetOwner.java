package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.SQLConnector;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IslandSetOwner implements SubCommand{

    private VSkyblock plugin = VSkyblock.getInstance();
    private SQLConnector getDatabase = new SQLConnector();
    private DatabaseWriter databaseWriter = new DatabaseWriter();


    @Override
    public void execute(DatabaseCache databaseCache) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Player player = databaseCache.getPlayer();
                if (databaseCache.isIslandowner()) {
                    OfflinePlayer target = plugin.getServer().getOfflinePlayer(databaseCache.getArg());

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
                    if (members.contains(target.getUniqueId().toString())) {
                        databaseWriter.updateOwner(player.getUniqueId().toString(), target.getUniqueId().toString());
                        ConfigShorts.messagefromString("SetNewIslandOwner", player);
                        if (target.isOnline()) {
                            ConfigShorts.messagefromString("NewIslandOwner", (Player) target);
                        }
                    } else {
                        ConfigShorts.messagefromString("PlayerNotIslandMember", player);
                    }
                } else {
                    ConfigShorts.messagefromString("NotIslandOwner", player);
                }
            }
        });
    }
}
