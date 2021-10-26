package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PlayerSubCommand extends SubCommand {

    protected PlayerSubCommand(VSkyblock plugin, String name, String... aliases) {
        super(plugin, name, aliases);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            plugin.getDb().getReader().getPlayerData(((Player) sender).getUniqueId().toString(),
                    playerInfo -> execute(sender, playerInfo, args));
        } else {
            sender.sendMessage(ChatColor.RED + "You must be a player to perform this command!");
        }
        return true;
    }

    abstract void execute(CommandSender sender, PlayerInfo playerInfo, String... args);
}
