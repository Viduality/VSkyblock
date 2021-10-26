package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/*
 * Gets the top 5 highest islands (level)
 */
public class IslandTop extends PlayerSubCommand {

    public IslandTop(VSkyblock plugin) {
        super(plugin, "top");
    }

    @Override
    public void execute(CommandSender sender, PlayerInfo playerInfo, String[] args) {
        plugin.getDb().getReader().getHighestIslands(result -> {
            StringBuilder message = new StringBuilder(ChatColor.BLUE + "" + ChatColor.BOLD + "-----Top Islands-----\n");
            for (int i = 0; i < result.size(); i++) {
                int rank = i + 1;
                message.append(ChatColor.GOLD).append(rank).append(".: ").append(ChatColor.RESET).append(result.get(i)).append("\n");
            }
            sender.sendMessage(message.toString());
        });
    }
}
