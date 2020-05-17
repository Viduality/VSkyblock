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

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.Difficulty;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldSetConfig implements AdminSubCommand {

    private WorldManager wm = new WorldManager();


    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("VSkyblock.SetWorldConfig")) {
                String world = player.getWorld().getName();
                String option = option1;
                String value = option2;
                if (wm.getAllWorlds().contains(world)) {
                    if (value != null) {
                        switch (option.toUpperCase()) {
                            case "AUTOLOAD":
                                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                    wm.setOption(world, "autoLoad", value.toLowerCase());
                                    ConfigShorts.messagefromString("OptionChanged", player);
                                    break;
                                } else {
                                    ConfigShorts.messagefromString("OnlyTrueOrFalse", player);
                                    break;
                                }
                            case "GENERATOR":
                                ConfigShorts.messagefromString("ChangeGeneratorMidGame", player);
                                break;
                            case "ENVIRONMENT":
                                ConfigShorts.messagefromString("ChangeEnvironmentMidGame", player);
                                break;
                            case "DIFFICULTY":
                                if (getDifficultyasString(value) != null) {
                                    wm.setOption(world, "difficulty", value.toUpperCase());
                                    player.getWorld().setDifficulty(getDifficulty(value));
                                    ConfigShorts.messagefromString("OptionChanged", player);
                                    break;
                                } else {
                                    ConfigShorts.custommessagefromString("WorldDifficultyNotFound", player, value);
                                    break;
                                }
                            case "KEEPSPAWNINMEMORY":
                                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                    wm.setOption(world, "keepSpawnInMemory", value.toLowerCase());
                                    player.getWorld().setKeepSpawnInMemory(Boolean.parseBoolean(value.toLowerCase()));
                                    ConfigShorts.messagefromString("OptionChanged", player);
                                    break;
                                } else {
                                    ConfigShorts.messagefromString("OnlyTrueOrFalse", player);
                                    break;
                                }
                            case "GENERATESTRUCTURES":
                                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                                    wm.setOption(world, "generateStructures", value.toLowerCase());
                                    player.getWorld().setKeepSpawnInMemory(Boolean.parseBoolean(value.toLowerCase()));
                                    ConfigShorts.messagefromString("OptionChanged", player);
                                    break;
                                } else {
                                    ConfigShorts.messagefromString("OnlyTrueOrFalse", player);
                                    break;
                                }
                            case "SPAWNPOINT":
                                ConfigShorts.messagefromString("UseCommandSetSpawnPoint", player);
                                break;
                            case "SETSPAWNPOINT":
                                ConfigShorts.messagefromString("UseCommandSetSpawnPoint", player);
                                break;
                            case "SET_SPAWNPOINT":
                                ConfigShorts.messagefromString("UseCommandSetSpawnPoint", player);
                                break;
                            default:
                                ConfigShorts.messagefromString("NotAValidOption", player);
                        }
                    } else {
                        ConfigShorts.messagefromString("MissingValue", player);
                    }
                } else {
                    ConfigShorts.custommessagefromString("NoWorldFound", sender, args);
                }
            } else {
                ConfigShorts.messagefromString("PermissionLack", player);
            }
        } else {
            ConfigShorts.messagefromString("NotAPlayer", sender);
        }
    }

    private String getEnvironment(String environment) {
        switch (environment.toUpperCase()) {
            case "NORMAL":
                return "NORMAL";
            case "NETHER":
                return "NETHER";
            case "THE_END":
                return "THE_END";
            default:
                return null;
        }
    }

    private String getDifficultyasString(String difficulty) {
        switch (difficulty.toUpperCase()) {
            case "PEACEFUL":
                return "PEACEFUL";
            case "EASY":
                return "EASY";
            case "NORMAL":
                return "NORMAL";
            case "HARD":
                return "HARD";
            default:
                return null;
        }
    }

    private Difficulty getDifficulty(String difficulty) {
        switch (difficulty.toUpperCase()) {
            case "PEACEFUL":
                return Difficulty.PEACEFUL;
            case "EASY":
                return Difficulty.EASY;
            case "NORMAL":
                return Difficulty.NORMAL;
            case "HARD":
                return Difficulty.HARD;
            default:
                return null;
        }
    }
}
