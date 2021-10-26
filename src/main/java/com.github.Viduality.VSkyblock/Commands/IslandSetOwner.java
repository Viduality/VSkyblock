package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
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
 * Sets a new owner for the island. Executing player has to be the current owner of the island.
 *  Renames the Island so that it's named after the new island owners UUID.
 */
public class IslandSetOwner extends PlayerSubCommand {

    public IslandSetOwner(VSkyblock plugin) {
        super(plugin, "setowner");
    }

    @Override
    public void execute(CommandSender sender, PlayerInfo playerInfo, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.AQUA + "/is setowner <Player>");
            return;
        }
        if (playerInfo.isIslandOwner()) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                OfflinePlayer target = plugin.getServer().getOfflinePlayer(args[0]);

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
                    plugin.getDb().getWriter().updateOwner(playerInfo.getUuid(), target.getUniqueId());
                    ConfigShorts.messagefromString("SetNewIslandOwner", sender);
                    if (target.isOnline()) {
                        ConfigShorts.messagefromString("NewIslandOwner", (Player) target);
                    }
                } else {
                    ConfigShorts.messagefromString("PlayerNotIslandMember", sender);
                }
            });
        } else {
            ConfigShorts.messagefromString("NotIslandOwner", sender);
        }
    }
}
