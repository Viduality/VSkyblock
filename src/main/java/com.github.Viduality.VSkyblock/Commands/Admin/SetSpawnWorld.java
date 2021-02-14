package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.Commands.WorldCommands.AdminSubCommand;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnWorld implements AdminSubCommand {


    private final VSkyblock plugin;

    public SetSpawnWorld(VSkyblock plugin) {
        this.plugin = plugin;
    }


    @Override
    public void execute(CommandSender sender, String args, String option1, String options) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("VSkyblock.SetSpawnWorld")) {
                    String world = player.getWorld().getName();
                    plugin.getConfigChanger().setConfig("SpawnWorld", world);
                    ConfigShorts.messagefromString("SetNewSpawnWorld", player);
                } else {
                    ConfigShorts.messagefromString("PermissionLack", player);
                }
            } else {
                ConfigShorts.messagefromString("NotAPlayer", sender);
            }
        });
    }
}
