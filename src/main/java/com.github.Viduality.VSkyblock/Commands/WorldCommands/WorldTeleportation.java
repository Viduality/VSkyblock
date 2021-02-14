package com.github.Viduality.VSkyblock.Commands.WorldCommands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldTeleportation implements AdminSubCommand {

    private final VSkyblock plugin;

    public WorldTeleportation(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("VSkyblock.Teleportation")) {
                if (plugin.getWorldManager().getLoadedWorlds().contains(args)) {
                    player.teleportAsync(plugin.getWorldManager().getSpawnLocation(args)).whenComplete((b, e) -> {
                        if (b) {
                            ConfigShorts.custommessagefromString("TeleportedToWorld", player, args);
                        } else {
                            ConfigShorts.custommessagefromString("CouldNotTeleportToWorld", player, args);
                        }
                        if (e != null) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    ConfigShorts.custommessagefromString("NoLoadedWorldFound", player, args);
                }
            } else {
                ConfigShorts.messagefromString("PermissionLack", player);
            }
        } else {
            ConfigShorts.messagefromString("NotAPlayer", sender);
        }
    }
}
