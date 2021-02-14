package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.Commands.WorldCommands.AdminSubCommand;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetNether implements AdminSubCommand {

    private final VSkyblock plugin;

    public SetNether(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (sender instanceof Player) {
                Player player =  (Player) sender;
                if (player.hasPermission("VSkyblock.SetNether")) {
                    if (player.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                        String world = player.getWorld().getName();
                        plugin.getConfigChanger().setConfig("NetherWorld", world);
                        ConfigShorts.messagefromString("SetNewNether", player);
                    } else {
                        ConfigShorts.messagefromString("UseInNetherWorld", player);
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
