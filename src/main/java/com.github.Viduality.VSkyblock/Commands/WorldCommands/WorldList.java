package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldList implements AdminSubCommand {

    private final VSkyblock plugin;

    public WorldList(VSkyblock plugin) {
        this.plugin = plugin;
    }


    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (sender.hasPermission("VSkyblock.List")) {
                Set<String> worlds = plugin.getWorldManager().getAllWorlds();
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
                String worldsString = getWorlds(new ArrayList<>(worlds), site);
                String message = header + '\n' + worldsString + '\n' + sitesString;
                sender.sendMessage(message);
            }
        });
    }



    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private String getWorlds(List<String> worlds, int site) {
        List<String> loadedWorlds = plugin.getWorldManager().getLoadedWorlds();
        String prefix = ConfigShorts.getMessageConfig().getString("Prefix") + " ";
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            if (worlds.size() > (site*6)-6+i) {
                String currentWorld = worlds.get((site*6)-6+i);
                str.append(prefix);
                str.append("§f");
                str.append(currentWorld);
                if (loadedWorlds.contains(currentWorld)) {
                    str.append(" - Loaded");
                } else {
                    str.append(" - §8Unloaded");
                }
                String env = ConfigShorts.getWorldConfig().getString("Worlds." + currentWorld + ".environment").toUpperCase();
                switch (env) {
                    case "THE_END":
                        str.append("§f - §5THE_END");
                        str.append('\n');
                        break;
                    case "NETHER":
                        str.append("§f - §4NETHER");
                        str.append('\n');
                        break;
                    default:
                        str.append("§f - NORMAL");
                        str.append('\n');
                }
            } else {
                i = 7;
            }
        }
        return str.toString();
    }
}
