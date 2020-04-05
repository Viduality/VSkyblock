package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldDelete implements AdminSubCommand {

    private WorldManager wm = new WorldManager();

    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender.hasPermission("VSkyblock.DeleteWorld")) {
            if (wm.getAllWorlds().contains(args)) {
                if (wm.deleteWorld(args)) {
                    ConfigShorts.messagefromString("WorldDeleted", sender);
                } else {
                    ConfigShorts.messagefromString("FailedToDeleteWorld", sender);
                }
            }
        }
    }
}
