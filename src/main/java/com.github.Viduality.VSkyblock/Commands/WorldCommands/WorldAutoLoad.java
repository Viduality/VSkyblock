package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldAutoLoad implements AdminSubCommand {

    private final VSkyblock plugin;

    public WorldAutoLoad(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("VSkyblock.AutoLoad")) {
                    if (plugin.getWorldManager().getAllWorlds().contains(player.getWorld().getName())) {
                        if (args.equalsIgnoreCase("true")) {
                            plugin.getWorldManager().setOption(player.getWorld().getName(), "autoLoad", "true");
                            ConfigShorts.custommessagefromString("SetAutoLoad", player, "true");
                        } else if (args.equalsIgnoreCase("false")) {
                            plugin.getWorldManager().setOption(player.getWorld().getName(), "autoLoad", "false");
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
        });
    }
}
