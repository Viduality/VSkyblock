package com.github.Viduality.VSkyblock.Commands;


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

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.google.common.cache.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Island implements CommandExecutor {

    public static HashMap<String, Map.Entry<String, Double>> activeislandslevels = new HashMap<String, Map.Entry<String, Double>>();
    public static HashMap<UUID, String> playerislands = new HashMap<>();

    public static Cache<UUID, Integer> restartmap = CacheBuilder.newBuilder()
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build();

    public static Cache<UUID, Integer> leavemap = CacheBuilder.newBuilder()
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build();

    public static Cache<UUID, UUID> invitemap = CacheBuilder.newBuilder()
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build();

    public static Cache<UUID, Integer> requestvisit = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    public static Cache<UUID, UUID> isgencooldown = CacheBuilder.newBuilder()
            .expireAfterWrite(getisgencooldown(), TimeUnit.MINUTES)
            .build();

    public static Cache<UUID, UUID> isjoincooldown = CacheBuilder.newBuilder()
            .expireAfterWrite(getisjoincooldown(), TimeUnit.MINUTES)
            .build();

    public static Map<String, BukkitTask> emptyloadedislands = new HashMap<>();

    public static Map<String, Location> islandhomes = new HashMap<>();


    private final VSkyblock plugin;

    public Island(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (cmd.getName().equalsIgnoreCase("island")) {
                    plugin.getDb().getReader().getPlayerData(player.getUniqueId().toString(), playerInfo -> {
                        playerInfo.setPlayer(player);
                        SubCommand subCommand = null;

                        ExecutionInfo executionInfo = new ExecutionInfo(sender, playerInfo);


                        if (player.hasPermission("VSkyblock.Island")) {


                            /*
                             * Default Island Command. Teleports to the players plugin or creates a new one if he has
                             * no plugin or is not a member of any plugin
                             */

                            if (args.length == 0) {
                                subCommand = new IslandCommand(plugin);
                            }


                            else if (args.length == 1) {

                                if (args[0].equalsIgnoreCase("help")) {
                                    subCommand = new IslandHelp(plugin);
                                }



                                /*
                                 * Sets the Islands home if the player is the owner of the plugin and on the plugin
                                 */

                                if (args[0].equalsIgnoreCase("sethome")) {
                                    subCommand = new IslandSethome(plugin);
                                }



                                /*
                                 * Command to restart the Island if the player is owner and there are no plugin members left
                                 * on the plugin. Player has to confirm the restart with "plugin restart confirm".
                                 */

                                else if (args[0].equalsIgnoreCase("restart")) {
                                    subCommand = new IslandRestart(plugin);
                                }



                                /*
                                 * Player can accept the invite of another player with this command. Can only be used when the
                                 * player is plugin owner and has no members on his plugin or if he isn't the plugin owner
                                 */

                                else if (args[0].equalsIgnoreCase("accept")) {
                                    subCommand = new IslandAccept();
                                }



                                /*
                                 * Lets the player leave his current plugin. Deletes it if he is the owner and alone on it.
                                 */

                                else if (args[0].equalsIgnoreCase("leave")) {
                                    subCommand = new IslandLeave(plugin);
                                }



                                /*
                                 * Lists all members of your plugin.
                                 */

                                else if (args[0].equalsIgnoreCase("members")) {
                                    subCommand = new IslandMembers(plugin);
                                }



                                /*
                                 * Gets the islands level.
                                 */

                                else if (args[0].equalsIgnoreCase("level")) {
                                    subCommand = new IslandLevel(plugin);
                                }


                                /*
                                 * Gets the top 5 highest islands (level)
                                 */

                                else if (args[0].equalsIgnoreCase("top")) {
                                    subCommand = new IslandTop(plugin);
                                }


                                /*
                                 * Shows the plugin options menu.
                                 */
                                else if (args[0].equalsIgnoreCase("options") ||
                                args[0].equalsIgnoreCase("option") ||
                                args[0].equalsIgnoreCase("settings") ||
                                args[0].equalsIgnoreCase("setting")) {
                                    subCommand = new IslandOptions(plugin);
                                }

                                /*
                                 * Sets the nether portal home point.
                                 */
                                else if (args[0].equalsIgnoreCase("setnetherhome") ||
                                args[0].equalsIgnoreCase("setnether")) {
                                    subCommand = new IslandSetNetherhome(plugin);
                                }

                                /*
                                 * Teleports to the nether portal home point.
                                 */
                                else if (args[0].equalsIgnoreCase("nether")) {
                                    subCommand = new IslandNether();
                                }

                                /*
                                 * Confirms the visit request.
                                 */
                                else if (args[0].equalsIgnoreCase("confirm")) {
                                    subCommand = new IslandConfirm(plugin);
                                }


                                else if (args[0].equalsIgnoreCase("invite")) {
                                    player.sendMessage(ChatColor.AQUA + "/is invite <Player>");
                                }
                                else if (args[0].equalsIgnoreCase("kick")) {
                                    player.sendMessage(ChatColor.AQUA + "/is kick <Player>");
                                }
                                else if (args[0].equalsIgnoreCase("setowner")) {
                                    player.sendMessage(ChatColor.AQUA + "/is setowner <Player>");
                                }
                            }


                            else if (args.length == 2) {

                                executionInfo.setTargetPlayer(plugin.getServer().getOfflinePlayer(args[1]));
                                executionInfo.setArg(args[1]);


                                /*
                                 * Confirms the plugin restart and executes it.
                                 */

                                if (args[0].equalsIgnoreCase("restart") && args[1].equalsIgnoreCase("confirm")) {
                                    subCommand = new IslandRestartConfirm(plugin);
                                }



                                /*
                                 * Invites another player to your plugin if the player is the plugin owner.
                                 */

                                else if (args[0].equalsIgnoreCase("invite")) {
                                    subCommand = new IslandInvite(plugin);
                                }



                                /*
                                 * Kicks a player from your plugin. Executing player has to be the plugin owner.
                                 */

                                else if (args[0].equalsIgnoreCase("kick")) {
                                    subCommand = new IslandKick(plugin);
                                }



                                /*
                                 * Sets a new owner for the plugin. Executing player has to be the current owner of the plugin.
                                 *  Renames the Island so that its named after the new plugin owners UUID.
                                 */

                                else if (args[0].equalsIgnoreCase("setowner")) {
                                    subCommand = new IslandSetOwner(plugin);
                                }



                                /*
                                 * Confirms the plugin leave and executes it.
                                 */

                                else if (args[0].equalsIgnoreCase("leave") && args[1].equalsIgnoreCase("confirm")) {
                                    subCommand = new IslandLeaveConfirm(plugin);
                                }



                                /*
                                 * Lets the player visit another players plugin without losing his own plugin.
                                 */

                                else if (args[0].equalsIgnoreCase("visit")) {
                                    subCommand = new IslandVisit(plugin);
                                }


                                /*
                                 * Confirms the visit request.
                                 */
                                else if (args[0].equalsIgnoreCase("confirm")) {
                                    subCommand = new IslandConfirm(plugin);
                                }


                                /*
                                 * Shows the plugin level from the given player.
                                 */
                                else if (args[0].equalsIgnoreCase("level")) {
                                    subCommand = new IslandLevel(plugin);
                                }
                            } else {
                                ConfigShorts.messagefromString("FalseInput", player);
                            }
                        } else {
                            ConfigShorts.messagefromString("PermissionLack", player);
                        }
                        if (subCommand != null) {
                            subCommand.execute(executionInfo);
                        } else {
                            ConfigShorts.messagefromString("FalseInput", player);
                        }
                    });
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You must be a player to perform this command!");
            }
        });
        return true;
    }

    public static int getisgencooldown() {
        if (isInt(ConfigShorts.getDefConfig().getString("IslandGenerateCooldown"))) {
            return ConfigShorts.getDefConfig().getInt("IslandGenerateCooldown");
        } else {
            return 5;
        }
    }

    public static int getisjoincooldown() {
        if (isInt(ConfigShorts.getDefConfig().getString("IslandJoinCooldown"))) {
            return ConfigShorts.getDefConfig().getInt("IslandJoinCooldown");
        } else {
            return 10;
        }
    }

    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
