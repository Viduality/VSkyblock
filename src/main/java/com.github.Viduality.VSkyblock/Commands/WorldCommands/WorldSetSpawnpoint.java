package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldSetSpawnpoint implements AdminSubCommand {

    private final VSkyblock plugin = VSkyblock.getInstance();


    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("VSkyblock.SetWorldSpawnpoint")) {
                    if (plugin.getWorldManager().getLoadedWorlds().contains(player.getWorld().getName())) {
                        plugin.getWorldManager().setSpawnLocation(player.getLocation());
                        ConfigShorts.custommessagefromString("SetNewSpawnpoint", player, player.getWorld().getName());
                    } else {
                        ConfigShorts.custommessagefromString("NoWorldFound", player, player.getWorld().getName());
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
