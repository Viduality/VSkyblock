package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldCreate implements AdminSubCommand {

    private final VSkyblock plugin;

    public WorldCreate(VSkyblock plugin) {
        this.plugin = plugin;
    }


    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender instanceof  Player) {
            Player player = (Player) sender;
            if (sender.hasPermission("VSkyblock.CreateWorld")) {
                if (!plugin.getWorldManager().getAllWorlds().contains(args)) {
                    ConfigShorts.messagefromString("WorldCreate", player);
                    String generator = option1;
                    String environment = option2;
                    if (environment == null) {
                        environment = "NORMAL";
                    }
                    if (checkEnvironment(environment.toUpperCase())) {
                        WorldCreator wc = new WorldCreator(args);
                        if (generator != null) {
                            wc.generator(generator);
                        } else {
                            generator = "default";
                        }
                        wc.environment(getEnvironment(environment.toUpperCase()));
                        wc.type(WorldType.NORMAL);
                        wc.generateStructures(true);
                        World loadedworld = wc.createWorld();
                        if (loadedworld != null) {
                            if (plugin.getWorldManager().addWorld(args, generator, environment.toUpperCase())) {
                                Location spawnlocation = loadedworld.getSpawnLocation();
                                if (plugin.getWorldManager().setSpawnLocation(spawnlocation)) {
                                    ConfigShorts.messagefromString("WorldCreated", player);
                                    player.teleportAsync(plugin.getWorldManager().getSpawnLocation(args));
                                } else {
                                    ConfigShorts.messagefromString("WorldCreationFailed", player);
                                    plugin.getWorldManager().deleteWorld(args);
                                }
                            } else {
                                ConfigShorts.messagefromString("WorldCreationFailed", player);
                                plugin.getWorldManager().deleteWorld(args);
                            }
                        } else {
                            ConfigShorts.messagefromString("WorldCreationFailed", player);
                        }
                    } else {
                        ConfigShorts.custommessagefromString("WorldEnvironmentNotFound", player, option2);
                    }
                } else {
                    ConfigShorts.messagefromString("WorldAlreadyExisting", player);
                }
            } else {
                ConfigShorts.messagefromString("PermissionLack", player);
            }
        } else {
            ConfigShorts.messagefromString("NotAPlayer", sender);
        }
    }

    private boolean checkEnvironment(String environment) {
        switch (environment) {
            case "NORMAL":
                return true;
            case "NETHER":
                return true;
            case "THE_END":
                return true;
            default:
                return false;
        }
    }

    private World.Environment getEnvironment(String environment) {
        switch (environment) {
            case "NORMAL":
                return World.Environment.NORMAL;
            case "NETHER":
                return World.Environment.NETHER;
            case "THE_END":
                return World.Environment.THE_END;
            default:
                return World.Environment.NORMAL;
        }
    }
}
