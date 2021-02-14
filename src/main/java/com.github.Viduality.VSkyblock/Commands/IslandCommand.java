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
import com.github.Viduality.VSkyblock.VSkyblock;
import com.github.Viduality.VSkyblock.WorldGenerator.IslandCreator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;


public class IslandCommand implements SubCommand {

    private final VSkyblock plugin;

    public IslandCommand(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(DatabaseCache databaseCache) {
        Player player = databaseCache.getPlayer();
        if (databaseCache.getIslandId() != 0) {
            boolean teleport = true;
            if (!ConfigShorts.getDefConfig().getBoolean("SaveWithIslandCommand")) {
                if (player.getFallDistance() > 2) {
                    teleport = false;
                    ConfigShorts.messagefromString("PlayerFalling", player);
                }
            }
            if (!ConfigShorts.getDefConfig().getBoolean("SaveWithIslandCommandLava")) {
                if (player.getLocation().getBlock().getType().equals(Material.LAVA)) {
                    teleport = false;
                    ConfigShorts.messagefromString("PlayerInLava", player);
                }
            }
            if (teleport) {
                if (!plugin.getWorldManager().getLoadedWorlds().contains(databaseCache.getIslandname())) {
                    if (!plugin.getWorldManager().loadWorld(databaseCache.getIslandname())) {
                        ConfigShorts.custommessagefromString("WorldFailedToLoad", player, databaseCache.getIslandname());
                        return;
                    }
                }
                Location islandHome = Island.islandhomes.get(databaseCache.getIslandname());
                if (islandHome != null) {
                    islandHome.getWorld().getChunkAtAsync(islandHome).whenComplete((c, e) -> {
                       if (e != null) {
                           e.printStackTrace();
                       }
                       if (c != null) {
                           Block below = islandHome.getBlock().getRelative(BlockFace.DOWN);
                           if (below.getType() == Material.AIR) {
                               below.setType(Material.INFESTED_COBBLESTONE);
                           }
                           plugin.teleportToIsland(player, islandHome);
                       } else {
                           ConfigShorts.custommessagefromString("WorldFailedToLoad", player, databaseCache.getIslandname());
                       }
                    });
                }
            }
        } else {
            if (!Island.isgencooldown.asMap().containsValue(player.getUniqueId())) {
                ConfigShorts.messagefromString("GenerateNewIsland", player);
                player.getEnderChest().clear();
                player.getInventory().clear();
                player.setTotalExperience(0);
                player.setExp(0);
                player.setFoodLevel(20);
                new IslandCreator(plugin, databaseCache.getUuid()).createNewIsland();
                Island.isgencooldown.put(player.getUniqueId(), player.getUniqueId());
            } else {
                ConfigShorts.custommessagefromString("GenerateCooldown", player, String.valueOf(Island.getisgencooldown()));
            }
        }
    }
}