package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class WorldLoader implements Runnable{

    private VSkyblock plugin = VSkyblock.getInstance();
    private WorldManager wm = new WorldManager();

    @Override
    public void run() {
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                List<String> worlds = wm.getAllWorlds();
                List<String> autoloadedislands = new ArrayList<>();
                for (World world : plugin.getServer().getWorlds()) {
                    if (!wm.getAllWorlds().contains(world.getName())) {
                        wm.addWorld(world.getName(), String.valueOf(world.getGenerator()), world.getEnvironment().name());
                        wm.setSpawnLocation(world.getSpawnLocation());
                    }
                }
                if (!worlds.isEmpty()) {
                    for (String world : worlds) {
                        if (ConfigShorts.getWorldConfig().getBoolean("Worlds." + world + ".autoLoad")) {
                            autoloadedislands.add(world);
                        }
                    }
                    if (!autoloadedislands.isEmpty()) {
                        for (String world1 : autoloadedislands) {
                            wm.loadWorld(world1);
                        }
                    }
                }
            }
        });
    }
}
