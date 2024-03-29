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
import com.github.Viduality.VSkyblock.WorldGenerator.IslandCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Confirms the island restart and executes it.
 */
public class IslandRestartConfirm extends PlayerSubCommand {

    public IslandRestartConfirm(VSkyblock plugin) {
        super(plugin, "confirm");
    }

    @Override
    public void execute(CommandSender sender, PlayerInfo playerInfo, String[] args) {
        Player player = playerInfo.getPlayer();
        if (IslandCacheHandler.restartmap.asMap().containsKey(player.getUniqueId())) {
            if (!IslandCacheHandler.isgencooldown.asMap().containsValue(player.getUniqueId())) {
                IslandCacheHandler.isgencooldown.put(player.getUniqueId(), player.getUniqueId());
                ConfigShorts.messagefromString("GenerateNewIsland", player);
                player.getInventory().clear();
                player.getEnderChest().clear();
                player.setExp(0);
                player.setTotalExperience(0);
                player.setScoreboard(plugin.getServer().getScoreboardManager().getMainScoreboard());
                new IslandCreator(plugin, playerInfo.getUuid())
                        .oldIsland(playerInfo.getIslandName())
                        .createNewIsland();
            } else {
                ConfigShorts.custommessagefromString("GenerateCooldown", playerInfo.getPlayer(), String.valueOf(IslandCacheHandler.getIsGenCooldown()));
            }
        } else {
            ConfigShorts.messagefromString("RestartFirst", player);
        }
    }
}
