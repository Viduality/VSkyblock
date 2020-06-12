package com.github.Viduality.VSkyblock.Commands.WorldCommands;

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

import com.github.Viduality.VSkyblock.Commands.Admin.PlayerInfo;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldInfo implements AdminSubCommand {

    private WorldManager wm = new WorldManager();



    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender.hasPermission("VSkyblock.Info")) {
            if (sender instanceof Player) {
                if (args == null) {
                    Player player = (Player) sender;
                    args = player.getWorld().getName();
                }
            }
            if (wm.getAllWorlds().contains(args)) {
                sender.sendMessage(wm.getWorldInformation(args));
            } else {
                new PlayerInfo(sender, args);
            }
        }
    }
}
