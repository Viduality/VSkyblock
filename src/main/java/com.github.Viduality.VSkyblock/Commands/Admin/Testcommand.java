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

import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Testcommand implements CommandExecutor {

    private VSkyblock testcommand;
    public Testcommand(VSkyblock testcommand) {
        this.testcommand = testcommand;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("Testcommand")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("VSkyblock.Testcommand")) {
                    player.sendMessage(String.valueOf(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType()));
                }
            }
        }
        return true;
    }
}
