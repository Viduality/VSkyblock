package com.github.Viduality.VSkyblock.Commands;

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

import com.github.Viduality.VSkyblock.Challenges.Challenge;
import com.github.Viduality.VSkyblock.Challenges.CreateChallengesInventory;
import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;


public class Challenges implements CommandExecutor {

    private final CreateChallengesInventory cc = new CreateChallengesInventory();

    private VSkyblock challenges;
    public Challenges(VSkyblock challenges) {
        this.challenges = challenges;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("Challenges")) {
                if (player.hasPermission("VSkyblock.Challenges")) {
                    if (Island.playerislands.containsKey(player.getUniqueId())) {
                        if (Island.playerislands.get(player.getUniqueId()).equals(player.getWorld().getName())) {
                            cc.createChallenges(player, Challenge.Difficulty.EASY, 1);
                        } else {
                            ConfigShorts.messagefromString("NotAtPlayersIsland", player);
                        }
                    } else {
                        ConfigShorts.messagefromString("NoIsland", player);
                    }
                } else {
                    ConfigShorts.messagefromString("PermissionLack", player);
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player to perform this command!");
        }
        return true;
    }
}
