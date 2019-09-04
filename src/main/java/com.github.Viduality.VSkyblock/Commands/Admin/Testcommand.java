package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Testcommand implements CommandExecutor {

    private VSkyblock testcommand;
    public Testcommand(VSkyblock testcommand) {
        this.testcommand = testcommand;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("Testcommand")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("VSkyblock.Testcommand")) {
                    player.sendMessage("TEST");
                }
            }
        }
        return true;
    }
}
