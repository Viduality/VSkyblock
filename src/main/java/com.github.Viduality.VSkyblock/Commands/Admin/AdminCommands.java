package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.Commands.WorldCommands.*;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommands implements CommandExecutor {

    private VSkyblock adminCommands;
    public AdminCommands(VSkyblock adminCommands) {
        this.adminCommands = adminCommands;
    }




    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        String arg = null;
        String opt1 = null;
        String opt2 = null;
        if (cmd.getName().equalsIgnoreCase("VSkyblock")) {
            AdminSubCommand adminSubCommand = null;

            if (args.length == 0) {
                adminSubCommand = new AdminCommandsHelp();
            }

            if (args.length == 1) {

                if (args[0].equalsIgnoreCase("help")) {
                    adminSubCommand = new AdminCommandsHelp();
                }

                if (args[0].equalsIgnoreCase("list")) {
                    adminSubCommand = new WorldList();
                }

                if (sender instanceof Player) {
                    if (args[0].equalsIgnoreCase("info")) {
                        adminSubCommand = new WorldInfo();
                    }
                }

            }

            if (args.length == 2) {

                arg = args[1];

                if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")) {
                    adminSubCommand = new WorldTeleportation();
                }

                if (args[0].equalsIgnoreCase("load")) {
                    adminSubCommand = new WorldLoad();
                }

                if (args[0].equalsIgnoreCase("unload")) {
                    adminSubCommand = new WorldUnload();
                }

                if (args[0].equalsIgnoreCase("list")) {
                    adminSubCommand = new WorldList();
                }

                if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("Nether")) {
                    adminSubCommand = new SetNether();
                }

                if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("SpawnWorld")) {
                    adminSubCommand = new SetSpawnWorld();
                }

                if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("SpawnPoint")) {
                    adminSubCommand = new WorldSetSpawnpoint();
                }

                if (args[0].equalsIgnoreCase("import")) {
                    adminSubCommand = new WorldImport();
                }

                if (args[0].equalsIgnoreCase("info")) {
                    adminSubCommand = new WorldInfo();
                }

                if (args[0].equalsIgnoreCase("recreate") && args[1].equalsIgnoreCase("languages")) {
                    adminSubCommand = new RecreateLanguageFiles();
                }

                if (args[0].equalsIgnoreCase("recreate") && args[1].equalsIgnoreCase("help")) {
                    adminSubCommand = new RecreateHelpFiles();
                }

                if (args[0].equalsIgnoreCase("recreate") && args[1].equalsIgnoreCase("challenges")) {
                    adminSubCommand = new RecreateChallengeFiles();
                }

                if (args[0].equalsIgnoreCase("reload") && args[1].equalsIgnoreCase("blockvalues")) {
                    adminSubCommand = new ReloadBlockValues();
                }

            }

            if (args.length == 3) {

                arg = args[2];

                if (args[0].equalsIgnoreCase("delete") && args[1].equalsIgnoreCase("world")) {
                    adminSubCommand = new WorldDelete();
                }

                if (args[0].equalsIgnoreCase("delete") && args[1].equalsIgnoreCase("player")) {
                    adminSubCommand = new DeletePlayer();
                }

                if (args[0].equalsIgnoreCase("reset") && args[1].equalsIgnoreCase("challenges")) {
                    adminSubCommand = new ResetChallenges();
                }

                if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("autoLoad")) {
                    adminSubCommand = new WorldAutoLoad();
                }

                if (args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("world")) {
                    adminSubCommand = new WorldCreate();
                }

            }

            if (args.length == 4) {

                arg = args[1];
                opt1 = args[2];
                opt2 = args[3];

                if (args[0].equalsIgnoreCase("import")) {
                    adminSubCommand = new WorldImport();
                }

                if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("worldconfig")) {
                    adminSubCommand = new WorldSetConfig();
                }
            }

            if (args.length == 5) {

                arg = args[2];
                opt1 = args[3];
                opt2 = args[4];

                if (args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("world")) {
                    adminSubCommand = new WorldCreate();
                }

            }
            if (adminSubCommand != null) {
                adminSubCommand.execute(sender, arg, opt1, opt2);
            } else {
                ConfigShorts.messagefromString("FalseInputAdmin", sender);
            }
        }
        return true;
    }
}
