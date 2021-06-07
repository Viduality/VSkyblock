package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;

public class WorldDelete implements AdminSubCommand {

    private final VSkyblock plugin;

    public WorldDelete(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender.hasPermission("VSkyblock.DeleteWorld")) {
            if (plugin.getWorldManager().getAllWorlds().contains(args)) {
                plugin.getWorldManager().deleteWorld(args).thenAccept(success -> {
                    if (success) {
                        ConfigShorts.messagefromString("WorldDeleted", sender);
                    } else {
                        ConfigShorts.messagefromString("FailedToDeleteWorld", sender);
                    }
                });
            }
        }
    }
}
