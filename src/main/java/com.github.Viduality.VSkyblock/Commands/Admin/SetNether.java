package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.Commands.WorldCommands.AdminSubCommand;
import com.github.Viduality.VSkyblock.Utilitys.ConfigChanger;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sun.security.krb5.Config;

public class SetNether implements AdminSubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private ConfigChanger cc = new ConfigChanger();


    @Override
    public void execute(Player player, String args) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (player.hasPermission("VSkyblock.SetNether")) {
                    if (player.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                        String world = player.getWorld().getName();
                        cc.setConfig("NetherWorld", world);
                        ConfigShorts.messagefromString("SetNewNether", player);
                    } else {
                        ConfigShorts.messagefromString("UseInNetherWorld", player);
                    }
                } else {
                    ConfigShorts.messagefromString("PermissionLack", player);
                }
            }
        });
    }
}
