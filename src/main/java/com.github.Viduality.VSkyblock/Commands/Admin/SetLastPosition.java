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
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLastPosition implements AdminSubCommand {

    private final VSkyblock plugin = VSkyblock.getInstance();
    private final DatabaseWriter databaseWriter = new DatabaseWriter();
    private final DatabaseReader databaseReader = new DatabaseReader();


    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("VSkyblock.SetLastPosition")) {
                OfflinePlayer p = plugin.getServer().getOfflinePlayer(args);
                databaseReader.getPlayerData(p.getUniqueId().toString(), (playerData -> {
                    if (playerData.getUuid() != null) {
                        databaseWriter.savelastLocation(playerData.getUuid(), player.getLocation());
                        ConfigShorts.custommessagefromString("SavedLastLocation", player, playerData.getName());
                    } else {
                        ConfigShorts.messagefromString("PlayerDoesNotExist", player);
                    }
                }));
            } else {
                ConfigShorts.messagefromString("PermissionLack", player);
            }
        } else {
            ConfigShorts.messagefromString("NotAPlayer", sender);
        }
    }
}
