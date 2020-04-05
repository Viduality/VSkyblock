package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.Commands.WorldCommands.AdminSubCommand;
import com.github.Viduality.VSkyblock.Utilitys.ConfigChanger;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnWorld implements AdminSubCommand {


    private VSkyblock plugin = VSkyblock.getInstance();
    private ConfigChanger cc = new ConfigChanger();


    @Override
    public void execute(CommandSender sender, String args, String option1, String options) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("VSkyblock.SetSpawnWorld")) {
                        String world = player.getWorld().getName();
                        cc.setConfig("SpawnWorld", world);
                        ConfigShorts.messagefromString("SetNewSpawnWorld", player);
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
