package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.entity.Player;

public class WorldInfo implements AdminSubCommand {

    private WorldManager wm = new WorldManager();



    @Override
    public void execute(Player player, String args, String option1, String option2) {
        if (player.hasPermission("VSkyblock.Info")) {
            if (wm.getAllWorlds().contains(args)) {
                player.sendMessage(wm.getWorldInformation(args));
            } else {
                ConfigShorts.custommessagefromString("NoWorldFound", player, args);
            }
        }
    }
}
