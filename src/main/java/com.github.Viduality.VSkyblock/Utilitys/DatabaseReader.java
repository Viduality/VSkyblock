package com.github.Viduality.VSkyblock.Utilitys;

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

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Listener.CobblestoneGenerator;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;


public class DatabaseReader {


    private final VSkyblock plugin = VSkyblock.getInstance();
    private final WorldManager wm = new WorldManager();

    /**
     * Gets the data of an player (database action).
     *
     * @param uuid      The unique id of a player.
     * @param callback  Returns the player data. (Database Cache)
     */
    public void getPlayerData(final String uuid, final Callback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            DatabaseCache databaseCache1 = new DatabaseCache();
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Player WHERE uuid = ?");
                preparedStatement.setString(1, uuid);
                ResultSet r = preparedStatement.executeQuery();
                while (r.next()) {
                    databaseCache1.setUuid(r.getString("uuid"));
                    databaseCache1.setName(r.getString("playername"));
                    databaseCache1.setKicked(r.getBoolean("kicked"));
                    databaseCache1.setIslandowner(r.getBoolean("islandowner"));
                    databaseCache1.setIslandId(r.getInt("islandid"));
                    databaseCache1.setIslandowneruuid(r.getString("owneruuid"));
                    databaseCache1.setDeathCount(r.getInt("deaths"));
                }
                preparedStatement.close();

                if (databaseCache1.getIslandId() != 0) {

                    preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Island WHERE islandid = ?");
                    preparedStatement.setInt(1, databaseCache1.getIslandId());
                    ResultSet r1 = preparedStatement.executeQuery();
                    while (r1.next()) {
                        databaseCache1.setIslandname(r1.getString("island"));
                        databaseCache1.setIslandLevel(r1.getInt("islandlevel"));
                    }
                    preparedStatement.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }


            final DatabaseCache databaseCache = databaseCache1;

            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(databaseCache));
        });
    }

    /**
     * Gets the name of the next island for the database (database action).
     * (Ignore the boolean it will be true all the time.)
     *
     * @param callback  Returns the latest islands name with its id on a sync thread.
     */
    public void getLatestIsland(final CallbackStrings callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int latestIsland = 0;
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                String database = plugin.getdb().getDatabase();
                String preparedStatement1 = "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = \"" + database + "\" AND TABLE_NAME = \"VSkyblock_Island\"";
                preparedStatement = connection.prepareStatement(preparedStatement1);
                ResultSet r = preparedStatement.executeQuery();
                while (r.next()) {
                    latestIsland = r.getInt("AUTO_INCREMENT");
                }
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (latestIsland == 0) {
                latestIsland = 1;
            }
            final String islandname = "VSkyblockIsland_" + latestIsland;
            final boolean a = true;
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(islandname, a));
        });
    }

    /**
     * Gets the island id of an given islandname (database action).
     *
     * @param island    The name of the island (world).
     * @param callback  Returns the id of the island on a sync thread.
     */
    public void getislandid(String island, CallbackINT callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            DatabaseCache databaseCache = new DatabaseCache();
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT islandid FROM VSkyblock_Island WHERE island = ?");
                preparedStatement.setString(1, island);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    databaseCache.setIslandId(resultSet.getInt("islandid"));
                }
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            final int islandid = databaseCache.getIslandId();
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(islandid));
        });
    }

    /**
     * Checks if an island has members (database action).
     *
     * @param islandid  The id of the island.
     * @param callback  Returns (boolean) wether the island has members or not.
     */
    public void hasislandmembers(int islandid, CallbackBoolean callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            DatabaseCache databaseCache = new DatabaseCache();
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Player WHERE islandid = ?");
                preparedStatement.setInt(1, islandid);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    databaseCache.addIslandMember(resultSet.getString("playername"));
                }
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            boolean hasmembers;
            hasmembers = databaseCache.getislandmembers().size() > 1;
            plugin.getServer().getScheduler().runTask(plugin, () -> callback.onQueryDone(hasmembers));
        });
    }

    /**
     * Gets the island id from a player (database action).
     *
     * @param uuid      The unique id of a player.
     * @param callback  Returns the islandid of the given player.
     */
    public void getislandidfromplayer(UUID uuid, CallbackINT callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            DatabaseCache databaseCache = new DatabaseCache();
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT islandid FROM VSkyblock_Player WHERE uuid = ?");
                preparedStatement.setString(1, uuid.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    databaseCache.setIslandId(resultSet.getInt("islandid"));
                }
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            final int islandid = databaseCache.getIslandId();
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(islandid));
        });
    }

    /**
     * Gets the island id from a player (database action).
     *
     * @param uuid      The unique id of a player.
     * @param callback  Returns the island on which the player is playing.
     */
    public void getislandnamefromplayer(UUID uuid, CallbackString callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            DatabaseCache databaseCache = new DatabaseCache();
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT islandid FROM VSkyblock_Player WHERE uuid = ?");
                preparedStatement.setString(1, uuid.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    databaseCache.setIslandId(resultSet.getInt("islandid"));
                }
                preparedStatement.close();

                PreparedStatement preparedStatement1;
                preparedStatement1 = connection.prepareStatement("SELECT island FROM VSkyblock_Island WHERE islandid = ?");
                preparedStatement1.setInt(1, databaseCache.getIslandId());
                ResultSet resultSet1 = preparedStatement1.executeQuery();
                while (resultSet1.next()) {
                    databaseCache.setIslandname(resultSet1.getString("island"));
                }
                preparedStatement1.close();
                resultSet1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            final String island = databaseCache.getIslandname();
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(island));
        });
    }

    /**
     * Gets all members of an island (database action).
     * Does not matter if a player is the owner of the island. He will be listed as well.
     *
     * @param islandid  The id of an island.
     * @param callback  Returns all members of the island.
     */
    public void getIslandMembers(Integer islandid, CallbackList callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<String> islandmembers = new ArrayList<>();
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT playername FROM VSkyblock_Player WHERE islandid = ?");
                preparedStatement.setInt(1, islandid);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    islandmembers.add(resultSet.getString("playername"));
                }
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            final List<String> result = islandmembers;
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(result));
        });
    }

    /**
     * Gets all islands without members (database action).
     *
     * @param callback Returns a list of all empty islands.
     */
    public void getemptyIslands(CallbackList callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<Integer> islandids = new ArrayList<>();
            List<String> emptyislands = new ArrayList<>();
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT islandid FROM VSkyblock_Island");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    islandids.add(resultSet.getInt("islandid"));
                }
                preparedStatement.close();

                PreparedStatement preparedStatement1;
                for (Integer currentid : islandids) {
                    preparedStatement1 = connection.prepareStatement("SELECT * FROM VSkyblock_Player WHERE islandid = ?");
                    preparedStatement1.setInt(1, currentid);
                    ResultSet resultSet1 = preparedStatement1.executeQuery();
                    boolean hasmembers = false;
                    while (resultSet1.next()) {
                        hasmembers = true;
                    }
                    if (!hasmembers) {
                        emptyislands.add("VSkyblockIsland_" + currentid);
                    }
                    preparedStatement1.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            final List<String> result = emptyislands;
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(result));
        });
    }

    /**
     * Gets the island level from a player (database action).
     *
     * @param uuid      The unique id of a player.
     * @param callback  Returns the island level of a player.
     */
    public void getislandlevelfromuuid(UUID uuid, CallbackINT callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            DatabaseCache databaseCache = new DatabaseCache();
            try (Connection connection = plugin.getdb().getConnection()) {
                int islandid = 0;
                PreparedStatement prep;
                prep = connection.prepareStatement("SELECT islandid FROM VSkyblock_Player WHERE uuid = ?");
                prep.setString(1, uuid.toString());
                ResultSet rs = prep.executeQuery();
                while (rs.next()) {
                    islandid = rs.getInt("islandid");
                }
                prep.close();

                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT islandlevel FROM VSkyblock_Island WHERE islandid = ?");
                preparedStatement.setInt(1, islandid);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    databaseCache.setIslandLevel(resultSet.getInt("islandlevel"));
                }
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            final int islandlevel = databaseCache.getIslandLevel();
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(islandlevel));
        });
    }

    /**
     * Gets the challenge counts for a specific difficulty from a player (database action).
     *
     * @param islandid  The id of an island.
     * @param callback  Returns a cache with the challenge counts.
     */
    public void getIslandChallenges(final int islandid, final cCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            ChallengesCache cache = new ChallengesCache();
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Challenges WHERE islandid = ?");
                preparedStatement.setInt(1, islandid);
                ResultSet r = preparedStatement.executeQuery();
                while (r.next()) {
                    cache.setChallengeCount(r.getString("challenge"), r.getInt("count"));
                    if (r.getBoolean("tracked")) {
                        cache.addTrackedChallenge(r.getString("challenge"));
                    }
                }
                preparedStatement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }


            final ChallengesCache cache1 = cache;

            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(cache1));
        });
    }

    /**
     * Re-writes all active islands into an islandlist.
     * Used after a reload since the list loses its contents when the plugin is reloaded.
     * The list is used to load and unload the islands.
     *
     * @param onlineplayers  A list of all players who are currently online.
     */
    public void refreshIslands(List<Player> onlineplayers) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Connection connection = plugin.getdb().getConnection();
            Location loc = null;
            String islandname = null;
            int islandid = 0;
            int cobblestonelevel = 0;
            int islandlevel = 0;
            List<Player> playerList = new ArrayList<>();
            try {
                for (Player player : onlineplayers) {
                    playerList.add(player);
                    PreparedStatement preparedStatement;
                    preparedStatement = connection.prepareStatement("SELECT islandid FROM VSkyblock_Player WHERE uuid = ?");
                    preparedStatement.setString(1, player.getUniqueId().toString());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        islandid = resultSet.getInt("islandid");
                    }
                    if (islandid != 0) {
                        PreparedStatement preparedStatement1;
                        preparedStatement1 = connection.prepareStatement("SELECT island FROM VSkyblock_Island WHERE islandid = ?");
                        preparedStatement1.setInt(1, islandid);
                        ResultSet resultSet1 = preparedStatement1.executeQuery();
                        while (resultSet1.next()) {
                            islandname = resultSet1.getString("island");
                        }
                        preparedStatement1.close();
                        PreparedStatement preparedStatementGetGeneratorLevel;
                        preparedStatementGetGeneratorLevel = connection.prepareStatement("SELECT cobblestonelevel FROM VSkyblock_Island WHERE islandid = ?");
                        preparedStatementGetGeneratorLevel.setInt(1, islandid);
                        ResultSet resultSet2 = preparedStatementGetGeneratorLevel.executeQuery();
                        while (resultSet2.next()) {
                            cobblestonelevel = resultSet2.getInt("cobblestonelevel");
                        }
                        preparedStatementGetGeneratorLevel.close();

                        PreparedStatement preparedStatementGetIslandLevel;
                        preparedStatementGetIslandLevel = connection.prepareStatement("SELECT islandlevel FROM VSkyblock_Island WHERE islandid = ?");
                        preparedStatementGetIslandLevel.setInt(1, islandid);
                        ResultSet resultSet3 = preparedStatementGetIslandLevel.executeQuery();
                        while (resultSet3.next()) {
                            islandlevel = resultSet3.getInt("islandlevel");
                        }
                        preparedStatementGetIslandLevel.close();


                        if (islandname != null) {
                            World w = plugin.getServer().getWorld(islandname);
                            PreparedStatement getIslandhome;
                            getIslandhome = connection.prepareStatement("SELECT * FROM VSkyblock_IslandLocations WHERE islandid = ?");
                            getIslandhome.setInt(1, islandid);
                            ResultSet r = getIslandhome.executeQuery();
                            while (r.next()) {
                                loc = new Location(w, r.getDouble("spawnX"), r.getDouble("spawnY"), r.getDouble("spawnZ"), r.getFloat("spawnYaw"), r.getFloat("spawnPitch"));
                            }
                        }
                    }
                    preparedStatement.close();

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Location finalLoc = loc;
            String finalIslandname = islandname;
            int finalIslandlevel = islandlevel;
            int finalCobblestonelevel = cobblestonelevel;
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                for (Player player : playerList) {
                    if (finalLoc != null) {
                        Island.islandhomes.put(finalIslandname, finalLoc);
                    }
                    if (finalIslandname != null && !finalIslandname.equals("NULL")) {
                        Island.playerislands.put(player.getUniqueId(), finalIslandname);
                        CobblestoneGenerator.islandGenLevel.put(finalIslandname, finalCobblestonelevel);
                        CobblestoneGenerator.islandlevels.put(finalIslandname, finalIslandlevel);
                    }
                }
            });
        });
    }

    /**
     * Adds the given island to  the cobblestone generator map.
     *
     * @param islandname The name of the island (world).
     */
    public void addToCobbleStoneGenerators(String islandname) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                int islandgeneratorLVL = 0;
                int islandlevel = 0;
                PreparedStatement preparedStatementGetGeneratorLevel;
                preparedStatementGetGeneratorLevel = connection.prepareStatement("SELECT cobblestonelevel FROM VSkyblock_Island WHERE island = ?");
                preparedStatementGetGeneratorLevel.setString(1, islandname);
                ResultSet resultSet = preparedStatementGetGeneratorLevel.executeQuery();
                while (resultSet.next()) {
                    islandgeneratorLVL = resultSet.getInt("cobblestonelevel");
                }
                CobblestoneGenerator.islandGenLevel.put(islandname, islandgeneratorLVL);
                preparedStatementGetGeneratorLevel.close();

                PreparedStatement preparedStatementGetIslandLevel;
                preparedStatementGetIslandLevel = connection.prepareStatement("SELECT islandlevel FROM VSkyblock_Island WHERE island = ?");
                preparedStatementGetIslandLevel.setString(1, islandname);
                ResultSet resultSet1 = preparedStatementGetIslandLevel.executeQuery();
                while (resultSet1.next()) {
                    islandlevel = resultSet1.getInt("islandlevel");
                }
                CobblestoneGenerator.islandlevels.put(islandname, islandlevel);
                preparedStatementGetIslandLevel.close();


            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Gets the 5 highest islands. Sorted by level.
     *
     * @param callback  Returns a list of the 5 highest islands, sorted by level
     */
    public void getHighestIslands(CallbackList callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<Integer> islandids = new ArrayList<>();
            List<Integer> islandlevels = new ArrayList<>();
            List<String> playersperisland = new ArrayList<>();
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Island ORDER BY CAST(islandlevel as unsigned) desc limit 5");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    islandids.add(resultSet.getInt("islandid"));
                    islandlevels.add(resultSet.getInt("islandlevel"));
                }
                preparedStatement.close();
                PreparedStatement preparedStatement1;
                for (int i = 0; i < islandids.size(); i++) {
                    preparedStatement1 = connection.prepareStatement("SELECT * FROM VSkyblock_Player WHERE islandid = ?");
                    preparedStatement1.setInt(1, islandids.get(i));
                    ResultSet resultSet1 = preparedStatement1.executeQuery();
                    StringBuilder memberList = null;
                    boolean hasmembers = false;
                    while (resultSet1.next()) {
                        hasmembers = true;
                        if (memberList == null) {
                            memberList = new StringBuilder(resultSet1.getString("playername"));
                        } else {
                            memberList.append(", ").append(resultSet1.getString("playername"));
                        }
                    }
                    if (!hasmembers) {
                        memberList = new StringBuilder("-");
                    }

                    memberList.append(" - ").append(islandlevels.get(i));

                    playersperisland.add(String.valueOf(memberList));
                    preparedStatement1.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            final List<String> result = playersperisland;
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(result));
        });
    }

    /**
     * Checks if an island is visitable.
     *
     * @param islandid  The id of the island.
     * @param callback  Returns true if the island is visitable (boolean).
     */
    public void isislandvisitable(int islandid, CallbackBoolean callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean visitable = false;
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Island WHERE islandid = ?");
                preparedStatement.setInt(1, islandid);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    visitable = resultSet.getBoolean("visit");
                }

                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            final boolean finalvisitable = visitable;
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(finalvisitable));
        });
    }

    /**
     * Checks if players need to request to visit the island.
     *
     * @param islandid  The id of the island.
     * @param callback  Returns true if players need to request.
     */
    public void islandneedsrequestforvisit(int islandid, CallbackBoolean callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean needsrequest = false;
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Island WHERE islandid = ?");
                preparedStatement.setInt(1, islandid);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    needsrequest = resultSet.getBoolean("visitneedsrequest");
                }

                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            final boolean finalneedsrequest = needsrequest;
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(finalneedsrequest));
        });
    }

    /**
     * Returns all options for an island.
     *
     * @param islandid  The id of the island
     * @param callback  Returns all options for the given island.
     */
    public void getIslandOptions(final int islandid, final isoptionsCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            IslandOptionsCache islandOptionsCache = new IslandOptionsCache();
            PreparedStatement preparedStatement;
            try (Connection connection = plugin.getdb().getConnection()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Island WHERE islandid = ?");
                preparedStatement.setInt(1, islandid);
                ResultSet r = preparedStatement.executeQuery();
                while (r.next()) {
                    islandOptionsCache.setVisit(r.getBoolean("visit"));
                    islandOptionsCache.setDifficulty(r.getString("difficulty"));
                    islandOptionsCache.setNeedRequest(r.getBoolean("visitneedsrequest"));
                }
                preparedStatement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }


            final IslandOptionsCache cache = islandOptionsCache;

            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(cache));
        });
    }

    /**
     * Refreshes deathcounts of all online players.
     *
     * @param onlineplayers A list of all players currently online.
     */
    public void refreshDeathCounts(List<Player> onlineplayers) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                for (Player player : onlineplayers) {
                    int deathcount = 0;
                    PreparedStatement preparedStatement;
                    preparedStatement = connection.prepareStatement("SELECT deaths FROM VSkyblock_Player WHERE uuid = ?");
                    preparedStatement.setString(1, player.getUniqueId().toString());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        deathcount = resultSet.getInt("deaths");
                    }
                    if (deathcount != 0) {
                        if (plugin.scoreboardmanager.doesobjectiveexist("deaths")) {
                            if (plugin.scoreboardmanager.hasPlayerScore(player.getName(), "deaths")) {
                                plugin.scoreboardmanager.updatePlayerScore(player.getName(), "deaths", deathcount);
                            }
                        }
                        if (plugin.getServer().getScoreboardManager().getMainScoreboard().getObjective("deaths") != null) {
                            Objective objective = plugin.getServer().getScoreboardManager().getMainScoreboard().getObjective("deaths");
                            if (objective != null) {
                                objective.getScore(player.getName()).setScore(deathcount);
                            }
                        }
                    }
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Returns the last position of the player, before leaving the server.
     *
     * @param uuid      The unique id of a player.
     * @param callback  Returns the last location the player where, before leaving the server.
     */
    public void getlastLocation(final UUID uuid, final CallbackLocation callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Location location = null;
            double x = 0;
            double y = 67;
            double z = 0;
            double pitch = 0;
            double yaw = 0;
            String lastWorld = null;
            int islandid = 0;
            String island = null;
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Player WHERE uuid = ?");
                preparedStatement.setString(1, uuid.toString());
                ResultSet r = preparedStatement.executeQuery();
                if (r.next()) {
                    islandid = r.getInt("islandid");
                    x = r.getDouble("lastX");
                    y = r.getDouble("lastY");
                    z = r.getDouble("lastZ");
                    pitch = r.getDouble("lastPitch");
                    yaw = r.getDouble("lastYaw");
                    lastWorld = r.getString("lastWorld");
                } else {
                    plugin.getServer().getLogger().log(Level.WARNING, "Could not find last location for uuid: " + uuid + "!");
                }
                preparedStatement.close();

                if (islandid != 0) {
                    PreparedStatement preparedStatement1;
                    preparedStatement1 = connection.prepareStatement("SELECT * FROM VSkyblock_Island WHERE islandid = ?");
                    preparedStatement1.setInt(1, islandid);
                    ResultSet r1 = preparedStatement1.executeQuery();
                    while (r1.next()) {
                        island = r1.getString("island");
                    }
                    preparedStatement1.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (lastWorld != null) {
                if (y != 0) {
                    if (wm.getLoadedWorlds().contains(lastWorld)) {
                        if (plugin.getServer().getWorld(lastWorld).getEnvironment().equals(World.Environment.NETHER) || lastWorld.equals(island)) {
                            World world = plugin.getServer().getWorld(lastWorld);
                            location = new Location(world, x, y, z, (float) yaw, (float) pitch);
                        }
                    }
                }
            }

            Location finalLocation = location;
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(finalLocation));
        });
    }

    /**
     * Calculates the challenge points for an island.
     * Used for the calculation of the islands level.
     *
     * @param islandid  The id of the island.
     * @param callback  Returns the points the island will get for all finished challenges.
     */
    public void getIslandsChallengePoints(int islandid, CallbackINT callback) {
        getIslandChallenges(islandid, (cache) -> {
            int challengeValueFirstComplete = getChallengeValueFirstComplete();
            int challengeValueAfterFirstComplete = getChallengeValueAfterFirstComplete();
            int challengeValueRepeats = getChallengeValueRepeats();
            int totalChallengePoints = 0;
            for (int currentc : cache.getAllChallengeCounts().values()) {
                if (currentc > challengeValueRepeats) {
                    currentc = challengeValueRepeats;
                }
                if (currentc > 0) {
                    int repeatedPoints = (currentc - 1) * challengeValueAfterFirstComplete;
                    totalChallengePoints = totalChallengePoints + challengeValueFirstComplete + repeatedPoints;
                }
            }
            callback.onQueryDone(totalChallengePoints);
        });
    }

    /**
     * Gets the value a challenge is worth at the first completion.
     * Measured in points for the island level.
     * Default is 150.
     *
     * @return int
     */
    private int getChallengeValueFirstComplete() {
        if (ConfigShorts.getDefConfig().getString("ChallengeValueFirstComplete") != null) {
            String challengeValueFirstComplete = ConfigShorts.getDefConfig().getString("ChallengeValueFirstComplete");
            if (challengeValueFirstComplete != null) {
                if (isInt(challengeValueFirstComplete)) {
                    return Integer.parseInt(challengeValueFirstComplete);
                } else {
                    return 150;
                }
            } else {
                return 150;
            }
        } else {
            return 150;
        }
    }

    /**
     * Gets the value a challenge is worth after the first completion.
     * Measured in points for the island level
     * Default is 10.
     *
     * @return int
     */
    private int getChallengeValueAfterFirstComplete() {
        if (ConfigShorts.getDefConfig().getString("ChallengeValueAfterFirstComplete") != null) {
            String challengeValueAfterFirstComplete = ConfigShorts.getDefConfig().getString("ChallengeValueAfterFirstComplete");
            if (challengeValueAfterFirstComplete != null) {
                if (isInt(challengeValueAfterFirstComplete)) {
                    return Integer.parseInt(challengeValueAfterFirstComplete);
                } else {
                    return 10;
                }
            } else {
                return 10;
            }
        } else {
            return 10;
        }
    }

    /**
     * Gets the amount a challenge will reward points when completed.
     * Default is 15.
     *
     * @return int
     */
    private int getChallengeValueRepeats() {
        if (ConfigShorts.getDefConfig().getString("ChallengeValueRepeats") != null) {
            String challengeValueRepeats = ConfigShorts.getDefConfig().getString("ChallengeValueRepeats");
            if (challengeValueRepeats != null) {
                if (isInt(challengeValueRepeats)) {
                    return Integer.parseInt(challengeValueRepeats);
                } else {
                    return 15;
                }
            } else {
                return 15;
            }
        } else {
            return 15;
        }
    }

    /**
     * Checks if the given String is from type Integer.
     * @param s         String
     * @return boolean
     */
    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Returns the nether home of a player.
     *
     * @param uuid      The unique id of a player
     * @param callback  Returns the nether home
     */
    public void getNetherHome(final UUID uuid, final CallbackLocation callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Location location = null;
            double x = 0;
            double y = 0;
            double z = 0;
            double yaw = 0;
            String world = null;
            int islandid = 0;
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement getislandid;
                getislandid = connection.prepareStatement("SELECT islandid FROM VSkyblock_Player WHERE uuid = ?");
                getislandid.setString(1, uuid.toString());
                ResultSet r = getislandid.executeQuery();
                while (r.next()) {
                    islandid = r.getInt("islandid");
                }
                getislandid.close();

                if (islandid != 0) {
                    PreparedStatement preparedStatement1;
                    preparedStatement1 = connection.prepareStatement("SELECT * FROM VSkyblock_IslandLocations WHERE islandid = ?");
                    preparedStatement1.setInt(1, islandid);
                    ResultSet getLoc = preparedStatement1.executeQuery();
                    while (getLoc.next()) {
                        x = getLoc.getDouble("netherX");
                        y = getLoc.getDouble("netherY");
                        z = getLoc.getDouble("netherZ");
                        yaw = getLoc.getFloat("netherYaw");
                        world = getLoc.getString("netherWorld");
                    }
                    preparedStatement1.close();

                    if (world != null) {
                        if (wm.getLoadedWorlds().contains(world)) {
                            World w = plugin.getServer().getWorld(world);
                            location = new Location(w, x, y, z, (float) yaw, 0);
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            Location finalLocation = location;
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(finalLocation));
        });
    }

    /**
     * Returns the island home location.
     *
     * @param world     The name of the world.
     * @param callback  Returns the location.
     */
    public void getIslandSpawn(final String world, final CallbackLocation callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Location location = null;
            double x = 0;
            double y = 67;
            double z = 0;
            float yaw = 0;
            float pitch = 0;
            int islandid = 0;
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement getislandid;
                getislandid = connection.prepareStatement("SELECT islandid FROM VSkyblock_Island WHERE island = ?");
                getislandid.setString(1, world);
                ResultSet r = getislandid.executeQuery();
                while (r.next()) {
                    islandid = r.getInt("islandid");
                }
                getislandid.close();

                if (islandid != 0) {
                    PreparedStatement preparedStatement1;
                    preparedStatement1 = connection.prepareStatement("SELECT * FROM VSkyblock_IslandLocations WHERE islandid = ?");
                    preparedStatement1.setInt(1, islandid);
                    ResultSet getLoc = preparedStatement1.executeQuery();
                    if (getLoc.next()) {
                        x = getLoc.getDouble("spawnX");
                        y = getLoc.getDouble("spawnY");
                        z = getLoc.getDouble("spawnZ");
                        yaw = getLoc.getFloat("spawnYaw");
                        pitch = getLoc.getFloat("spawnPitch");
                    } else {
                        plugin.getServer().getLogger().log(Level.WARNING, "Could not find island spawn location for island " + world + "!");
                    }
                    preparedStatement1.close();

                    if (world != null) {
                        if (wm.getLoadedWorlds().contains(world)) {
                            World w = plugin.getServer().getWorld(world);
                            location = new Location(w, x, y, z, yaw, pitch);
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            Location finalLocation = location;
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(finalLocation));
        });
    }







    public interface Callback {
        void onQueryDone(DatabaseCache result);
    }

    public interface cCallback {
        void onQueryDone(ChallengesCache cache);
    }

    public interface isoptionsCallback {
        void onQueryDone(IslandOptionsCache isoptionsCache);
    }

    public interface CallbackString {
        void onQueryDone(String result);
    }

    public interface CallbackINT {
        void onQueryDone(int result);
    }

    public interface CallbackBoolean {
        void onQueryDone(boolean result);
    }

    public interface CallbackList {
        void onQueryDone(List<String> result);
    }

    public interface CallbackStrings {
        void onQueryDone(String result, boolean a);
    }

    public interface CallbackLocation {
        void onQueryDone(Location loc);
    }
}
