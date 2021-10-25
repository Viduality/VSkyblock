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

public class IslandSetOwner implements SubCommand{

    private final VSkyblock plugin;

    public IslandSetOwner(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ExecutionInfo execution) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerInfo playerInfo = execution.getPlayerInfo();
            Player player = playerInfo.getPlayer();
            if (playerInfo.isIslandOwner()) {
                OfflinePlayer target = plugin.getServer().getOfflinePlayer(execution.getArg());

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
                if (members.contains(target.getUniqueId().toString())) {
                    plugin.getDb().getWriter().updateOwner(player.getUniqueId(), target.getUniqueId());
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
        });
    }
}
