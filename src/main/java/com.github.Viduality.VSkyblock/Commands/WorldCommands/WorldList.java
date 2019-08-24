package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;

import java.util.List;

public class WorldList implements AdminSubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private WorldManager wm = new WorldManager();



    @Override
    public void execute(Player player, String args) {
        List<String> worlds = wm.getAllWorlds();
        int sites = Math.round((worlds.size()/7)+1);
        int site = 1;
        if (isInt(args)) {
            if (Integer.parseInt(args) != 0) {
                if (Integer.parseInt(args) <= sites) {
                    site = Integer.parseInt(args);
                }
            }
        }
        String header = ConfigShorts.getCustomString("WorldListHeader");
        String sitesString = ConfigShorts.getCustomString("Site", String.valueOf(site), String.valueOf(sites));
        String worldsString = getWorlds(site);
        String message = header + '\n' + worldsString + '\n' + sitesString;
        player.sendMessage(message);
    }



    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private String getWorlds(int site) {
        List<String> worlds = wm.getAllWorlds();
        List<String> loadedWorlds = wm.getLoadedWorlds();
        String prefix = ConfigShorts.getPrefix();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            if (worlds.size() > (site*7)-7+i) {
                String currentWorld = worlds.get((site*7)-7+i);
                str.append(prefix);
                str.append("§f");
                str.append(currentWorld);
                if (loadedWorlds.contains(currentWorld)) {
                    str.append(" - Loaded");
                    str.append('\n');
                } else {
                    str.append(" - §8Unloaded");
                    str.append('\n');
                }
            } else {
                i = 7;
            }
        }
        return str.toString();
    }
}
