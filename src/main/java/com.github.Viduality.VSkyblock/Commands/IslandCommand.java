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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IslandCommand extends PlayerSubCommand {

    private final VSkyblock plugin;

    public IslandCommand(VSkyblock plugin) {
        super(plugin, "island");
        this.plugin = plugin;

        registerSubCommand(new IslandHelp(plugin));
        registerSubCommand(new IslandSethome(plugin));
        registerSubCommand(new IslandRestart(plugin));
        registerSubCommand(new IslandAccept(plugin));
        registerSubCommand(new IslandLeave(plugin));
        registerSubCommand(new IslandMembers(plugin));
        registerSubCommand(new IslandLevel(plugin));
        registerSubCommand(new IslandTop(plugin));
        registerSubCommand(new IslandOptions(plugin));
        registerSubCommand(new IslandSetNetherhome(plugin));
        registerSubCommand(new IslandNether(plugin));
        registerSubCommand(new IslandConfirm(plugin));
        registerSubCommand(new IslandInvite(plugin));
        registerSubCommand(new IslandKick(plugin));
        registerSubCommand(new IslandSetOwner(plugin));
        registerSubCommand(new IslandVisit(plugin));
        registerSubCommand(new IslandConfirm(plugin));
    }

    @Override
    public void execute(CommandSender sender, PlayerInfo playerInfo, String[] args) {
        if (args.length > 0) {
            ConfigShorts.messagefromString("FalseInput", sender);
            return;
        }
        Player player = playerInfo.getPlayer();
        if (playerInfo.getIslandId() != 0) {
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
                if (!plugin.getWorldManager().getLoadedWorlds().contains(playerInfo.getIslandName())) {
                    if (!plugin.getWorldManager().loadWorld(playerInfo.getIslandName())) {
                        ConfigShorts.custommessagefromString("WorldFailedToLoad", player, playerInfo.getIslandName());
                        return;
                    }
                }
                Location islandHome = IslandCacheHandler.islandhomes.get(playerInfo.getIslandName());
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
                            ConfigShorts.custommessagefromString("WorldFailedToLoad", player, playerInfo.getIslandName());
                        }
                    });
                }
            }
        } else {
            if (!IslandCacheHandler.isgencooldown.asMap().containsValue(player.getUniqueId())) {
                ConfigShorts.messagefromString("GenerateNewIsland", player);
                player.getEnderChest().clear();
                player.getInventory().clear();
                player.setTotalExperience(0);
                player.setExp(0);
                player.setFoodLevel(20);
                new IslandCreator(plugin, playerInfo.getUuid()).createNewIsland();
                IslandCacheHandler.isgencooldown.put(player.getUniqueId(), player.getUniqueId());
            } else {
                ConfigShorts.custommessagefromString("GenerateCooldown", player, String.valueOf(IslandCacheHandler.getIsGenCooldown()));
            }
        }
    }
}
