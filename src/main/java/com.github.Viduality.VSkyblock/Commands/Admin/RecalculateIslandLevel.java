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

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Commands.IslandLevel;
import com.github.Viduality.VSkyblock.Commands.WorldCommands.AdminSubCommand;
import com.github.Viduality.VSkyblock.Listener.CobblestoneGenerator;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.*;
import org.bukkit.command.CommandSender;

public class RecalculateIslandLevel implements AdminSubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private DatabaseWriter databaseWriter = new DatabaseWriter();
    WorldManager wm = new WorldManager();



    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender.hasPermission("VSkyblock.RecalculateIslandLevel")) {
            OfflinePlayer p = plugin.getServer().getOfflinePlayer(args);
            databaseReader.getPlayerData(p.getUniqueId().toString(), (playerdata -> {
                if (playerdata.getUuid() != null) {
                    databaseReader.getislandidfromplayer(playerdata.getUuid(), (islandid -> {
                        if (islandid != 0) {
                            databaseReader.getislandlevelfromuuid(playerdata.getUuid(), (oldislandlevel -> {
                                ConfigShorts.custommessagefromString("CurrentIslandLevel", sender, String.valueOf(oldislandlevel));
                                databaseReader.getislandnamefromplayer(playerdata.getUuid(), (islandname -> {
                                    if (wm.loadWorld(islandname)) {
                                        databaseReader.getIslandsChallengePoints(islandid, (challengePoints -> {
                                            World world = plugin.getServer().getWorld(islandname);
                                            int valueriselevel = getValueRiseLevel();
                                            int valueincrease = getValueIncrease();
                                            double worldsize = world.getWorldBorder().getSize();
                                            int x1 = ((int) (-1 * worldsize / 2)) >> 4;
                                            int x2 = ((int) worldsize / 2) >> 4;
                                            int z1 = x1;
                                            int z2 = x2;
                                            double value = 0;
                                            if (isInt(ConfigShorts.getDefConfig().getString("IslandValueonStart"))) {
                                                value = ConfigShorts.getDefConfig().getInt("IslandValueonStart");
                                            } else {
                                                value = 150;
                                            }
                                            value = value + challengePoints;
                                            int valueperlevel;
                                            if (isInt(ConfigShorts.getDefConfig().getString("IslandValue"))) {
                                                valueperlevel = ConfigShorts.getDefConfig().getInt("IslandValue");
                                            } else {
                                                valueperlevel = 300;
                                            }

                                            IslandLevel.IslandCounter counter = new IslandLevel.IslandCounter(value, 0, (c) -> {

                                                double currentvalue = c.value;

                                                int level = 0;
                                                int increasedvaluefornextlevel = valueperlevel + valueincrease;
                                                for (int i = 0; i < valueriselevel; i++) {
                                                    if (currentvalue - valueperlevel >= 0) {
                                                        level = level + 1;
                                                        currentvalue = currentvalue - valueperlevel;
                                                    } else {
                                                        currentvalue = 0;
                                                        break;
                                                    }
                                                }
                                                if (currentvalue - increasedvaluefornextlevel >= 0) {
                                                    while (currentvalue >= 0) {
                                                        if (currentvalue - increasedvaluefornextlevel >= 0) {
                                                            level++;
                                                            currentvalue = currentvalue - increasedvaluefornextlevel;
                                                            increasedvaluefornextlevel = increasedvaluefornextlevel + valueincrease;
                                                        } else {
                                                            currentvalue = 0;
                                                            break;
                                                        }
                                                    }
                                                }

                                                int roundlevel = (int) Math.floor(level);
                                                databaseWriter.updateIslandLevel(islandid, roundlevel, c.blocks, p.getUniqueId());
                                                ConfigShorts.custommessagefromString("NewIslandLevel", sender, String.valueOf(roundlevel));
                                                if (!Island.playerislands.containsValue(islandname)) {
                                                    wm.unloadWorld(islandname);
                                                }
                                                if (CobblestoneGenerator.islandlevels.containsKey(islandname)) {
                                                    CobblestoneGenerator.islandlevels.put(islandname, roundlevel);
                                                }
                                            });

                                            // Two loops are necessary as getChunkAtAsync might return instantly if chunk is loaded
                                            for (int x = x1; x <= x2; x++) {
                                                for (int z = z1; z <= z2; z++) {
                                                    counter.toCount();
                                                }
                                            }

                                            for (int x = x1; x <= x2; x++) {
                                                for (int z = z1; z <= z2; z++) {
                                                    world.getChunkAtAsync(x, z, false).whenComplete((c, ex) -> counter.count(c));
                                                }
                                            }
                                        }));
                                    } else {
                                        ConfigShorts.custommessagefromString("WorldNotFound", sender, islandname);
                                    }
                                }));
                            }));
                        } else {
                            ConfigShorts.messagefromString("PlayerHasNoIsland", sender);
                        }
                    }));
                } else {
                    ConfigShorts.messagefromString("PlayerDoesNotExist", sender);
                }
            }));
        } else {
            ConfigShorts.messagefromString("PermissionLack", sender);
        }
    }



    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private int getValueRiseLevel() {
        String s = ConfigShorts.getDefConfig().getString("IslandValueRiseLevel");
        if (s != null) {
            if (isInt(s)) {
                return Integer.parseInt(s);
            } else {
                return 150;
            }
        } else {
            return 150;
        }
    }

    private int getValueIncrease() {
        String s = ConfigShorts.getDefConfig().getString("IslandValueIncreasePerLevel");
        if (s != null) {
            if (isInt(s)) {
                return Integer.parseInt(s);
            } else {
                return 20;
            }
        } else {
            return 20;
        }
    }
}
