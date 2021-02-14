package com.github.Viduality.VSkyblock.Commands.Admin;

/*
 * VSkyblock
 * Copyright (C) 2020  Viduality
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.github.Viduality.VSkyblock.Commands.WorldCommands.AdminSubCommand;
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

    private final VSkyblock plugin;

    public DeletePlayer(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (sender.hasPermission("VSkyblock.DeletePlayer")) {
                DatabaseCache databaseCache = new DatabaseCache();
                try (Connection connection = plugin.getDb().getConnection()) {
                    PreparedStatement preparedStatement;
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
                    try (Connection connection = plugin.getDb().getConnection()) {
                        PreparedStatement preparedStatement1;

                        preparedStatement1 = connection.prepareStatement("DELETE FROM VSkyblock_Player WHERE uuid = ?");
                        preparedStatement1.setString(1, uuid.toString());
                        preparedStatement1.executeUpdate();
                        preparedStatement1.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        ConfigShorts.custommessagefromString("DeletedPlayer", sender, args);
                        OfflinePlayer target = plugin.getServer().getOfflinePlayer(args);
                        if (target.isOnline()) {
                            Player onlinetarget = (Player) target;
                            onlinetarget.kickPlayer("Relog please");
                        }
                    });
                } else {
                    plugin.getServer().getScheduler().runTask(plugin,
                            () -> ConfigShorts.messagefromString("PlayerDoesNotExist", sender));
                }
            } else {
                plugin.getServer().getScheduler().runTask(plugin,
                        () -> ConfigShorts.messagefromString("PermissionLack", sender));
            }
        });
    }
}
