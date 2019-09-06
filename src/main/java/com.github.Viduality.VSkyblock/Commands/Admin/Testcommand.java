package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


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
                    ItemStack chest = new ItemStack(Material.CHEST, 1);
                    ItemMeta chestItemMeta = chest.getItemMeta();
                    player.sendMessage("TEST");
                }
            }
        }
        return true;
    }
}
