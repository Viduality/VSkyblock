package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.Commands.WorldCommands.*;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommands implements CommandExecutor {

    private VSkyblock plugin = VSkyblock.getInstance();

    private VSkyblock adminCommands;
    public AdminCommands(VSkyblock adminCommands) {
        this.adminCommands = adminCommands;
    }




    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String arg = null;
            if (cmd.getName().equalsIgnoreCase("VSkyblock")) {
                AdminSubCommand worldSubCommand = null;

                if (args.length == 0) {
                    //IMPLEMENT HELP
                    player.sendMessage("0 args");
                }

                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        worldSubCommand = new WorldList();
                    }
                }

                if (args.length == 2) {

                    arg = args[1];

                    if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")) {
                        worldSubCommand = new WorldTeleportation();
                    }

                    if (args[0].equalsIgnoreCase("load")) {
                        worldSubCommand = new WorldLoad();
                    }

                    if (args[0].equalsIgnoreCase("unload")) {
                        worldSubCommand = new WorldUnload();
                    }

                    if (args[0].equalsIgnoreCase("list")) {
                        worldSubCommand = new WorldList();
                    }

                    if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("Nether")) {
                        worldSubCommand = new SetNether();
                    }

                    if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("Spawnworld")) {
                        worldSubCommand = new SetSpawnWorld();
                    }

                }

                if (args.length == 3) {

                    arg = args[2];

                    if (args[0].equalsIgnoreCase("delete") && args[1].equalsIgnoreCase("world")) {
                        worldSubCommand = new WorldDelete();
                    }

                    if (args[0].equalsIgnoreCase("delete") && args[1].equalsIgnoreCase("player")) {
                        worldSubCommand = new DeletePlayer();
                    }

                    if (args[0].equalsIgnoreCase("reset") && args[1].equalsIgnoreCase("challenges")) {
                        worldSubCommand = new ResetChallenges();
                    }

                }
                if (worldSubCommand != null) {
                    worldSubCommand.execute(player, arg);
                } else {
                    ConfigShorts.messagefromString("FalseInput", player);
                }
            }
        }
        return true;
    }
}
