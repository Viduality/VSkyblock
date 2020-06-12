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

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class PlayerInfo {

    private final VSkyblock plugin = VSkyblock.getInstance();
    private final DatabaseReader databaseReader = new DatabaseReader();


    public PlayerInfo(CommandSender sender, String args) {
        OfflinePlayer p = plugin.getServer().getOfflinePlayer(args);
        databaseReader.getPlayerData(p.getUniqueId().toString(), (playerData -> {
            if (playerData.getUuid() != null) {
                String playerInfo = ChatColor.AQUA + "----- " + playerData.getName() + " -----" + "\n" + "\n" +
                        ChatColor.GOLD + "IslandID: " + ChatColor.RESET + playerData.getIslandId() + "\n" +
                        ChatColor.GOLD + "Island: " + ChatColor.RESET + playerData.getIslandname() + "\n" +
                        ChatColor.GOLD + "IslandLevel: " + ChatColor.RESET + playerData.getIslandLevel() + "\n" +
                        ChatColor.GOLD + "IslandOwner: " + ChatColor.RESET + playerData.isIslandowner() + "\n" +
                        ChatColor.GOLD + "DeathCount: " + ChatColor.RESET + playerData.getDeathCount() + "\n";
                sender.sendMessage(playerInfo);
            } else {
                ConfigShorts.custommessagefromString("NoMatchFound", sender, args);
            }
        }));
    }
}
