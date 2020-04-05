package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldAutoLoad implements AdminSubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private WorldManager wm = new WorldManager();


    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
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
                        } else {
                            ConfigShorts.custommessagefromString("NoWorldFound", sender, args);
                        }
                    } else {
                        ConfigShorts.messagefromString("PermissionLack", player);
                    }
                } else {
                    ConfigShorts.messagefromString("NotAPlayer", sender);
                }
            }
        });
    }
}
