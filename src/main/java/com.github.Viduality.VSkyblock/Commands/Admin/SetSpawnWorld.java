package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.Utilitys.ConfigChanger;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnWorld implements CommandExecutor {


    private VSkyblock plugin = VSkyblock.getInstance();
    private ConfigChanger cc = new ConfigChanger();

    private VSkyblock setSpawnWorld;


    public SetSpawnWorld(VSkyblock setSpawnWorld) {
        this.setSpawnWorld = setSpawnWorld;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (cmd.getName().equalsIgnoreCase("VSkySetSpawnWorld")) {
                        if (player.hasPermission("VSkyblock.VSkySetSpawnWorld")) {
                            String world = player.getWorld().getName();
                            cc.setConfig("SpawnWorld", world);
                            player.sendMessage(ChatColor.GOLD + "A new spawn world has been set");
                        } else {
                            player.sendMessage(ChatColor.RED + "You don't have permission for this command!");
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You must be a player to perform for this command!");
                }
            }
        });
        return true;
    }
}
