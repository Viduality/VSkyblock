package com.github.Viduality.VSkyblock.Commands.Challenges;

import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;


public class Challenges implements CommandExecutor {

    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_RESET = "\u001B[0m";

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private CreateChallengesInventory cc = new CreateChallengesInventory();

    private VSkyblock challenges;
    public Challenges(VSkyblock challenges) {
        this.challenges = challenges;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (cmd.getName().equalsIgnoreCase("Challenges")) {
                        if (player.hasPermission("VSkyblock.Challenges")) {
                            cc.createChallenges(player, "Easy");
                        }
                    }
                }
            }
        });
        return true;
    }
}
