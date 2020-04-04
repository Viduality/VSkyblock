package com.github.Viduality.VSkyblock.Commands;


import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.google.common.cache.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Island implements CommandExecutor {

    private static VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();


    public static HashMap<String, Map.Entry<String, Double>> activeislandslevels = new HashMap<String, Map.Entry<String, Double>>();
    public static HashMap<String, String> playerislands = new HashMap<>();

    public static LoadingCache<UUID, Integer> restartmap = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build(
                    new CacheLoader<UUID, Integer>() {
                        @Override
                        public Integer load(UUID uuid) throws Exception {
                            return null;
                        }
                    }
            );

    public static LoadingCache<UUID, Integer> leavemap = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build(
                    new CacheLoader<UUID, Integer>() {
                        @Override
                        public Integer load(UUID uuid) throws Exception {
                            return null;
                        }
                    }
            );

    public static LoadingCache<UUID, UUID> invitemap = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build(
                    new CacheLoader<UUID, UUID>() {
                        @Override
                        public UUID load(UUID uuid) throws Exception {
                            return null;
                        }
                    }
            );

    public static LoadingCache<UUID, UUID> isgencooldown = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(getisgencooldown(), TimeUnit.MINUTES)
            .build(
                    new CacheLoader<UUID, UUID>() {
                        @Override
                        public UUID load(UUID uuid) throws Exception {
                            return null;
                        }
                    }
            );

    public static LoadingCache<UUID, UUID> isjoincooldown = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(getisjoincooldown(), TimeUnit.MINUTES)
            .build(
                    new CacheLoader<UUID, UUID>() {
                        @Override
                        public UUID load(UUID uuid) throws Exception {
                            return null;
                        }
                    }
            );

    public static RemovalListener<String, String> unloadIslands = new RemovalListener<String, String>() {
        @Override
        public void onRemoval(RemovalNotification<String, String> removal) {
            if (removal.getCause().equals(RemovalCause.EXPIRED)) {
                wm.unloadWorld(removal.getValue());
            }
        }
    };

    public static LoadingCache<String, String> emptyloadedislands = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .removalListener(unloadIslands)
            .build(
                    new CacheLoader<String, String>() {
                        @Override
                        public String load(String key) throws Exception {
                            return null;
                        }
                    }
            );




    private VSkyblock island;

    public static WorldManager wm = new WorldManager();

    public Island(VSkyblock island) {
        this.island = island;
    }




    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (cmd.getName().equalsIgnoreCase("island")) {
                        {
                            databaseReader.getPlayerData(player.getUniqueId().toString(), new DatabaseReader.Callback() {
                                @Override
                                public void onQueryDone(DatabaseCache databasecache) {
                                    databasecache.setPlayer(player);
                                    SubCommand subCommand = null;


                                    if (player.hasPermission("VSkyblock.Island")) {


                                        /*
                                         * Default Island Command. Teleports to the players island or creates a new one if he has
                                         * no island or is not a member of any island
                                         */

                                        if (args.length == 0) {
                                            subCommand = new IslandCommand();
                                        }


                                        else if (args.length == 1) {

                                            if (args[0].equalsIgnoreCase("help")) {
                                                subCommand = new IslandHelp();
                                            }



                                            /*
                                             * Sets the Islands home if the player is the owner of the island and on the island
                                             */

                                            if (args[0].equalsIgnoreCase("sethome")) {
                                                subCommand = new IslandSethome();
                                            }



                                            /*
                                             * Command to restart the Island if the player is owner and there are no island members left
                                             * on the island. Player has to confirm the restart with "island restart confirm".
                                             */

                                            else if (args[0].equalsIgnoreCase("restart")) {
                                                subCommand = new IslandRestart();
                                            }



                                            /*
                                             * Player can accept the invite of another player with this command. Can only be used when the
                                             * player is island owner and has no members on his island or if he isn't the island owner
                                             */

                                            else if (args[0].equalsIgnoreCase("accept")) {
                                                subCommand = new IslandAccept();
                                            }



                                            /*
                                             * Lets the player leave his current island. Deletes it if he is the owner and alone on it.
                                             */

                                            else if (args[0].equalsIgnoreCase("leave")) {
                                                subCommand = new IslandLeave();
                                            }



                                            /*
                                             * Lists all members of your island.
                                             */

                                            else if (args[0].equalsIgnoreCase("members")) {
                                                subCommand = new IslandMembers();
                                            }



                                            /*
                                             * Gets the islands level.
                                             */

                                            else if (args[0].equalsIgnoreCase("level")) {
                                                subCommand = new IslandLevel();
                                            }


                                            /*
                                             * Gets the top 5 highest islands (level)
                                             */

                                            else if (args[0].equalsIgnoreCase("top")) {
                                                subCommand = new IslandTop();
                                            }


                                            /*
                                             * Shows the island options menu.
                                             */
                                            else if (args[0].equalsIgnoreCase("options") ||
                                            args[0].equalsIgnoreCase("option") ||
                                            args[0].equalsIgnoreCase("settings") ||
                                            args[0].equalsIgnoreCase("setting")) {
                                                subCommand = new IslandOptions();
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

                                            databasecache.setTargetPlayer(plugin.getServer().getOfflinePlayer(args[1]));
                                            databasecache.setArg(args[1]);


                                            /*
                                             * Confirms the island restart and executes it.
                                             */

                                            if (args[0].equalsIgnoreCase("restart") && args[1].equalsIgnoreCase("confirm")) {
                                                subCommand = new IslandRestartConfirm();
                                            }



                                            /*
                                             * Invites another player to your island if the player is the island owner.
                                             */

                                            else if (args[0].equalsIgnoreCase("invite")) {
                                                subCommand = new IslandInvite();
                                            }



                                            /*
                                             * Kicks a player from your island. Executing player has to be the island owner.
                                             */

                                            else if (args[0].equalsIgnoreCase("kick")) {
                                                subCommand = new IslandKick();
                                            }



                                            /*
                                             * Sets a new owner for the island. Executing player has to be the current owner of the island.
                                             *  Renames the Island so that its named after the new island owners UUID.
                                             */

                                            else if (args[0].equalsIgnoreCase("setowner")) {
                                                subCommand = new IslandSetOwner();
                                            }



                                            /*
                                             * Confirms the island leave and executes it.
                                             */

                                            else if (args[0].equalsIgnoreCase("leave") && args[1].equalsIgnoreCase("confirm")) {
                                                subCommand = new IslandLeaveConfirm();
                                            }



                                            /*
                                             * Lets the player visit another players island without losing his own island.
                                             */

                                            else if (args[0].equalsIgnoreCase("visit")) {
                                                subCommand = new IslandVisit();
                                            }



                                            /*
                                             * Shows the island level from the given player.
                                             */
                                            else if (args[0].equalsIgnoreCase("level")) {
                                                subCommand = new IslandLevel();
                                            }
                                        } else {
                                            ConfigShorts.messagefromString("FalseInput", player);
                                        }
                                    } else {
                                        ConfigShorts.messagefromString("PermissionLack", player);
                                    }
                                    if (subCommand != null) {
                                        subCommand.execute(databasecache);
                                    } else {
                                        ConfigShorts.messagefromString("FalseInput", player);
                                    }
                                }
                            });
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You must be a player to perform this command!");
                }
            }
        });
        return true;
    }

    public static int getisgencooldown() {
        ConfigShorts.loaddefConfig();
        if (isInt(plugin.getConfig().getString("IslandGenerateCooldown"))) {
            return plugin.getConfig().getInt("IslandGenerateCooldown");
        } else {
            return 5;
        }
    }

    public static int getisjoincooldown() {
        ConfigShorts.loaddefConfig();
        if (isInt(plugin.getConfig().getString("IslandJoinCooldown"))) {
            return plugin.getConfig().getInt("IslandJoinCooldown");
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
