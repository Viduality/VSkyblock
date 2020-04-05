package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class WorldImport implements AdminSubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private WorldManager wm = new WorldManager();


    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender.hasPermission("VSkyblock.Import")) {
            if (!wm.getAllWorlds().contains(args)) {
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
                        if (wm.addWorld(args, generator, environment)) {
                            if (wm.loadWorld(args)) {
                                Location spawnlocation = plugin.getServer().getWorld(args).getSpawnLocation();
                                if (wm.setSpawnLocation(spawnlocation)) {
                                    if (wm.unloadWorld(args)) {
                                        ConfigShorts.messagefromString("ImportSuccessfull", sender);
                                    } else {
                                        wm.deleteWorldfromConfig(args);
                                        ConfigShorts.messagefromString("ImportNotSuccessfull", sender);
                                    }
                                } else {
                                    wm.deleteWorldfromConfig(args);
                                    ConfigShorts.messagefromString("ImportNotSuccessfull", sender);
                                }
                            } else {
                                wm.deleteWorldfromConfig(args);
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
