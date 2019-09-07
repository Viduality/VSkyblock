package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.Difficulty;
import org.bukkit.entity.Player;

public class WorldSetConfig implements AdminSubCommand {

    private WorldManager wm = new WorldManager();


    @Override
    public void execute(Player player, String args, String option1, String option2) {
        if (player.hasPermission("Skyblock.SetWorldConfig")) {
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
            }
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
