package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldLoader implements Runnable {

    private final VSkyblock plugin;

    public WorldLoader(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Set<String> worlds = plugin.getWorldManager().getAllWorlds();
        List<String> autoloadedislands = new ArrayList<>();
        for (World world : plugin.getServer().getWorlds()) {
            if (!plugin.getWorldManager().getAllWorlds().contains(world.getName())) {
                plugin.getWorldManager().addWorld(world.getName(), String.valueOf(world.getGenerator()), world.getEnvironment().name());
                plugin.getWorldManager().setSpawnLocation(world.getSpawnLocation());
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
                    plugin.getWorldManager().loadWorld(world1);
                }
            }
        }
    }
}
