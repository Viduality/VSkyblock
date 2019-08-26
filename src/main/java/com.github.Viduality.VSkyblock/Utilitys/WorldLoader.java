package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.VSkyblock;

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
                ConfigShorts.loadWorldConfig();
                for (String world : worlds) {
                    if (plugin.getConfig().getBoolean("Worlds." + world + ".autoLoad")) {
                        autoloadedislands.add(world);
                    }
                }
                if (!autoloadedislands.isEmpty()) {
                    for (String world1 : autoloadedislands) {
                        wm.loadWorld(world1);
                    }
                }
            }
        });
    }
}
