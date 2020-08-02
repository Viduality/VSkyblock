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
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.WorldGenerator.Islandmethods;
import org.bukkit.entity.Player;

public class IslandRestartConfirm implements SubCommand{

    private Islandmethods islandmethods = new Islandmethods();


    @Override
    public void execute(DatabaseCache databaseCache) {
        Player player = databaseCache.getPlayer();
        if (Island.restartmap.asMap().containsKey(player.getUniqueId())) {
            if (!Island.isgencooldown.asMap().containsValue(player.getUniqueId())) {
                Island.isgencooldown.put(player.getUniqueId(), player.getUniqueId());
                ConfigShorts.messagefromString("GenerateNewIsland", player);
                player.getInventory().clear();
                player.getEnderChest().clear();
                player.setExp(0);
                player.setTotalExperience(0);
                islandmethods.createNewIsland(databaseCache.getUuid(), databaseCache.getIslandname());
            } else {
                ConfigShorts.custommessagefromString("GenerateCooldown", databaseCache.getPlayer(), String.valueOf(Island.getisgencooldown()));
            }
        } else {
            ConfigShorts.messagefromString("RestartFirst", player);
        }
    }
}
