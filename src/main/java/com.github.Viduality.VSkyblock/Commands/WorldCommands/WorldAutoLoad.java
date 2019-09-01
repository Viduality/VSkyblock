package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.entity.Player;

public class WorldAutoLoad implements AdminSubCommand {

    private WorldManager wm = new WorldManager();


    @Override
    public void execute(Player player, String args, String option1, String option2) {
        if (player.hasPermission("VSkyblock.AutoLoad")) {
            if (wm.getAllWorlds().contains(player.getWorld().getName())) {
                if (args.equalsIgnoreCase("true")) {
                    wm.setOption(player.getWorld().getName(), "autoLoad", "true");
                    ConfigShorts.custommessagefromString("SetAutoLoad", player, "true");
                } else if (args.equalsIgnoreCase("false")) {
                    wm.setOption(player.getWorld().getName(), "autoLoad", "false");
                    ConfigShorts.custommessagefromString("SetAutoLoad", player, "false");
                } else {
                    ConfigShorts.messagefromString("OnlyTrueOrFalse", player);
                }
            }
        }
    }
}
