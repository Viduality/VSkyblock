package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

public class WorldCreate implements AdminSubCommand {

    private WorldManager wm = new WorldManager();
    private VSkyblock plugin = VSkyblock.getInstance();


    @Override
    public void execute(Player player, String args, String option1, String option2) {
        if (player.hasPermission("VSkyblock.CreateWorld")) {
            if (!wm.getAllWorlds().contains(args)) {
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
                        if (wm.addWorld(args, generator, environment.toUpperCase())) {
                            Location spawnlocation = plugin.getServer().getWorld(args).getSpawnLocation();
                            if (wm.setSpawnLocation(spawnlocation)) {
                                ConfigShorts.messagefromString("WorldCreated", player);
                                player.teleport(wm.getSpawnLocation(args));
                            } else {
                                ConfigShorts.messagefromString("WorldCreationFailed", player);
                                wm.deleteWorld(args);
                            }
                        } else {
                            ConfigShorts.messagefromString("WorldCreationFailed", player);
                            wm.deleteWorld(args);
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
