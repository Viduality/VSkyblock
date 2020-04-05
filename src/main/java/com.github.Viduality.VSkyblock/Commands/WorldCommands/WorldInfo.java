package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldInfo implements AdminSubCommand {

    private WorldManager wm = new WorldManager();



    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender.hasPermission("VSkyblock.Info")) {
            if (sender instanceof Player) {
                if (args == null) {
                    Player player = (Player) sender;
                    args = player.getWorld().getName();
                }
            }
            if (wm.getAllWorlds().contains(args)) {
                sender.sendMessage(wm.getWorldInformation(args));
            } else {
                ConfigShorts.custommessagefromString("NoWorldFound", sender, args);
            }
        }
    }
}
