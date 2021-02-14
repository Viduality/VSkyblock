package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.io.File;

public class WorldImport implements AdminSubCommand {

    private final VSkyblock plugin;

    public WorldImport(VSkyblock plugin) {
        this.plugin = plugin;
    }


    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender.hasPermission("VSkyblock.Import")) {
            if (!plugin.getWorldManager().getAllWorlds().contains(args)) {
                ConfigShorts.messagefromString("ImportingWorld", sender);
                File worldContainer = plugin.getServer().getWorldContainer();
                File importworld = new File(worldContainer, args);
                if (importworld.isDirectory()) {
                    String generator = option1;
                    String environment = option2;
                    if (generator == null) {
                        generator = "default";
                    }
                    if (environment == null) {
                        environment = "normal";
                    }
                    boolean leveldat = false;
                    boolean sessionlock = false;
                    boolean uiddat = false;
                    boolean playerdata = false;
                    for (File file : importworld.listFiles()) {
                        switch (file.getName()) {
                            case "level.dat":
                                leveldat = true;
                                break;
                            case "session.lock":
                                sessionlock = true;
                                break;
                            case "uid.dat":
                                uiddat = true;
                                break;
                            case "playerdata":
                                playerdata = true;
                                break;
                            default:
                        }
                    }
                    if (leveldat && sessionlock && uiddat && playerdata) {
                        if (plugin.getWorldManager().addWorld(args, generator, environment)) {
                            if (plugin.getWorldManager().loadWorld(args)) {
                                Location spawnlocation = plugin.getServer().getWorld(args).getSpawnLocation();
                                if (plugin.getWorldManager().setSpawnLocation(spawnlocation)) {
                                    if (plugin.getWorldManager().unloadWorld(args)) {
                                        ConfigShorts.messagefromString("ImportSuccessfull", sender);
                                    } else {
                                        plugin.getWorldManager().deleteWorldfromConfig(args);
                                        ConfigShorts.messagefromString("ImportNotSuccessfull", sender);
                                    }
                                } else {
                                    plugin.getWorldManager().deleteWorldfromConfig(args);
                                    ConfigShorts.messagefromString("ImportNotSuccessfull", sender);
                                }
                            } else {
                                plugin.getWorldManager().deleteWorldfromConfig(args);
                                ConfigShorts.messagefromString("ImportNotSuccessfull", sender);
                            }
                        } else {
                            ConfigShorts.messagefromString("ImportNotSuccessfull", sender);
                        }
                    } else {
                        ConfigShorts.messagefromString("WorldIsNoWorld", sender);
                    }
                } else {
                    ConfigShorts.messagefromString("WorldIsNoWorld", sender);
                }
            } else {
                ConfigShorts.messagefromString("WorldAlreadyExisting", sender);
            }
        } else {
            ConfigShorts.messagefromString("PermissionLack", sender);
        }
    }
}
