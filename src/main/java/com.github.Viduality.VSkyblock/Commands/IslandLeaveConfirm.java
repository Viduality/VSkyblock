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

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.IslandCacheHandler;
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Confirms the island leave and executes it.
 */
public class IslandLeaveConfirm extends PlayerSubCommand {

    public IslandLeaveConfirm(VSkyblock plugin) {
        super(plugin, "confirm");
    }

    @Override
    public void execute(CommandSender sender, PlayerInfo playerInfo, String[] args) {
        Player player = playerInfo.getPlayer();
        if (IslandCacheHandler.leavemap.getIfPresent(player.getUniqueId()) != null) {
            plugin.getDb().getWriter().leavefromIsland(player.getUniqueId());
            ConfigShorts.messagefromString("LeftIsland", player);
            player.getInventory().clear();
            player.getEnderChest().clear();
            player.setExp(0);
            player.setTotalExperience(0);
            player.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
            IslandCacheHandler.leavemap.invalidate(player.getUniqueId());
            IslandCacheHandler.playerislands.remove(player.getUniqueId());
            if (!IslandCacheHandler.playerislands.containsValue(playerInfo.getIslandName())) {
                plugin.getWorldManager().unloadWorld(playerInfo.getIslandName());
            }
        } else {
            ConfigShorts.messagefromString("LeaveFirst", player);
        }
    }
}
