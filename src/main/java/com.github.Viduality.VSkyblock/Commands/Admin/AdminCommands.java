package com.github.Viduality.VSkyblock.Commands.Admin;

/*
 * VSkyblock
 * Copyright (C) 2020  Viduality
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.github.Viduality.VSkyblock.Commands.WorldCommands.*;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommands implements CommandExecutor {

    private final VSkyblock plugin;

    public AdminCommands(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        String arg = null;
        String opt1 = null;
        String opt2 = null;
        if (cmd.getName().equalsIgnoreCase("VSkyblock")) {
            AdminSubCommand adminSubCommand = null;

            if (args.length == 0) {
                adminSubCommand = new AdminCommandsHelp(plugin);
            }

            if (args.length == 1) {

                if (args[0].equalsIgnoreCase("help")) {
                    adminSubCommand = new AdminCommandsHelp(plugin);
                }

                if (args[0].equalsIgnoreCase("list")) {
                    adminSubCommand = new WorldList(plugin);
                }

                if (sender instanceof Player) {
                    if (args[0].equalsIgnoreCase("info")) {
                        adminSubCommand = new WorldInfo(plugin);
                    }
                }

            }

            if (args.length == 2) {

                arg = args[1];

                if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")) {
                    adminSubCommand = new WorldTeleportation(plugin);
                }

                if (args[0].equalsIgnoreCase("load")) {
                    adminSubCommand = new WorldLoad(plugin);
                }

                if (args[0].equalsIgnoreCase("unload")) {
                    adminSubCommand = new WorldUnload(plugin);
                }

                if (args[0].equalsIgnoreCase("list")) {
                    adminSubCommand = new WorldList(plugin);
                }

                if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("Nether")) {
                    adminSubCommand = new SetNether(plugin);
                }

                if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("SpawnWorld")) {
                    adminSubCommand = new SetSpawnWorld(plugin);
                }

                if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("SpawnPoint")) {
                    adminSubCommand = new WorldSetSpawnpoint();
                }

                if (args[0].equalsIgnoreCase("import")) {
                    adminSubCommand = new WorldImport(plugin);
                }

                if (args[0].equalsIgnoreCase("info")) {
                    adminSubCommand = new WorldInfo(plugin);
                }

                if (args[0].equalsIgnoreCase("recreate") && args[1].equalsIgnoreCase("languages")) {
                    adminSubCommand = new RecreateLanguageFiles(plugin);
                }

                if (args[0].equalsIgnoreCase("recreate") && args[1].equalsIgnoreCase("help")) {
                    adminSubCommand = new RecreateHelpFiles(plugin);
                }

                if (args[0].equalsIgnoreCase("recreate") && args[1].equalsIgnoreCase("challenges")) {
                    adminSubCommand = new RecreateChallengeFiles(plugin);
                }

                if (args[0].equalsIgnoreCase("reload") && args[1].equalsIgnoreCase("blockvalues")) {
                    adminSubCommand = new ReloadBlockValues();
                }

                if (args[0].equalsIgnoreCase("reload") && args[1].equalsIgnoreCase("config")) {
                    adminSubCommand = new ReloadConfig();
                }

            }

            if (args.length == 3) {

                arg = args[2];

                if (args[0].equalsIgnoreCase("delete") && args[1].equalsIgnoreCase("world")) {
                    adminSubCommand = new WorldDelete(plugin);
                }

                if (args[0].equalsIgnoreCase("delete") && args[1].equalsIgnoreCase("player")) {
                    adminSubCommand = new DeletePlayer(plugin);
                }

                if (args[0].equalsIgnoreCase("reset") && args[1].equalsIgnoreCase("challenges")) {
                    adminSubCommand = new ResetChallenges(plugin);
                }

                if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("autoLoad")) {
                    adminSubCommand = new WorldAutoLoad(plugin);
                }

                if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("lastposition")) {
                    adminSubCommand = new SetLastPosition(plugin);
                }

                if (args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("world")) {
                    adminSubCommand = new WorldCreate(plugin);
                }

                if ((args[0].equalsIgnoreCase("recalculate") || args[0].equalsIgnoreCase("recalc"))
                        && (args[1].equalsIgnoreCase("islandlevel") || args[1].equalsIgnoreCase("islevel"))) {
                    adminSubCommand = new RecalculateIslandLevel(plugin);
                }

            }

            if (args.length == 4) {

                arg = args[1];
                opt1 = args[2];
                opt2 = args[3];

                if (args[0].equalsIgnoreCase("import")) {
                    adminSubCommand = new WorldImport(plugin);
                }

                if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("worldconfig")) {
                    adminSubCommand = new WorldSetConfig(plugin);
                }
            }

            if (args.length == 5) {

                arg = args[2];
                opt1 = args[3];
                opt2 = args[4];

                if (args[0].equalsIgnoreCase("create") && args[1].equalsIgnoreCase("world")) {
                    adminSubCommand = new WorldCreate(plugin);
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
