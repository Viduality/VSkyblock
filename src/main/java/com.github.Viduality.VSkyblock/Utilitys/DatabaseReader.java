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

import com.github.Viduality.VSkyblock.Listener.CobblestoneGenerator;
import com.github.Viduality.VSkyblock.SQLConnector;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;


public class DatabaseReader {
    
    private final VSkyblock plugin;
    private final SQLConnector connector;

    public DatabaseReader(VSkyblock plugin, SQLConnector sqlConnector) {
        this.plugin = plugin;
        this.connector = sqlConnector;
    }

    /**
     * Gets the data of an player (database action).
     *
     * @param uuid      The unique id of a player.
     * @param callback  Returns the player data. (Database Cache)
     */
    public void getPlayerData(final String uuid, final Consumer<PlayerInfo> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerInfo playerInfo = new PlayerInfo();
            try (Connection connection = connector.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Player WHERE uuid = ?")) {;
                preparedStatement.setString(1, uuid);
                ResultSet r = preparedStatement.executeQuery();
                if (r.next()) {
                    playerInfo.setUuid(r.getString("uuid"));
                    playerInfo.setName(r.getString("playername"));
                    playerInfo.setKicked(r.getBoolean("kicked"));
                    playerInfo.setIsIslandOwner(r.getBoolean("islandowner"));
                    playerInfo.setIslandId(r.getInt("islandid"));
                    playerInfo.setDeathCount(r.getInt("deaths"));

                    if (playerInfo.getIslandId() != 0) {

                        PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT * FROM VSkyblock_Island WHERE islandid = ?");
                        preparedStatement1.setInt(1, playerInfo.getIslandId());
                        ResultSet r1 = preparedStatement1.executeQuery();
                        while (r1.next()) {
                            playerInfo.setIslandName(r1.getString("island"));
                            playerInfo.setIslandLevel(r1.getInt("islandlevel"));
                        }
                        preparedStatement1.close();
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(playerInfo));
        });
    }

    /**
     * Gets the name of the next island for the database (database action).
     * (Ignore the boolean it will be true all the time.)
     *
     * @param callback  Returns the latest islands name with its id on a sync thread.
     */
    public void getLatestIsland(final Consumer<String> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int latestIsland = -1;
            String database = connector.getDatabase();
            String preparedStatement1 = "SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = \"" + database + "\" AND TABLE_NAME = \"VSkyblock_Island\"";
            try (Connection connection = connector.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(preparedStatement1)) {
                ResultSet r = preparedStatement.executeQuery();
                if (r.next()) {
                    latestIsland = r.getInt("AUTO_INCREMENT");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            final String islandname;
            if (latestIsland > -1) {
                islandname = "VSkyblockIsland_" + latestIsland;
            } else {
                islandname = null;
            }
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(islandname));
        });
    }

    /**
     * Gets the island id of an given islandname (database action).
     *
     * @param island    The name of the island (world).
     * @param callback  Returns the id of the island on a sync thread.
     */
    public void getIslandId(String island, Consumer<Integer> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = connector.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT islandid FROM VSkyblock_Island WHERE island = ?")) {
                preparedStatement.setString(1, island);
                ResultSet resultSet = preparedStatement.executeQuery();
                int islandId;
                if (resultSet.next()) {
                    islandId = resultSet.getInt("islandid");
                } else {
                    islandId = 0;
                }
                Bukkit.getScheduler().runTask(plugin, () -> callback.accept(islandId));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Checks if an island has members (database action).
     *
     * @param islandid  The id of the island.
     * @param callback  Returns (boolean) wether the island has members or not.
     */
    public void hasIslandMembers(int islandid, Consumer<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int islandMemberCount = 0;
            try (Connection connection = connector.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Player WHERE islandid = ?")) {
                preparedStatement.setInt(1, islandid);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    islandMemberCount++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            boolean hasMembers = islandMemberCount > 1;
            plugin.getServer().getScheduler().runTask(plugin, () -> callback.accept(hasMembers));
        });
    }

    /**
     * Gets the island id from a player (database action).
     *
     * @param uuid      The unique id of a player.
     * @param callback  Returns the islandid of the given player.
     */
    public void getIslandIdFromPlayer(UUID uuid, Consumer<Integer> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int islandId = 0;
            try (Connection connection = connector.getConnection();
                PreparedStatement preparedStatement  = connection.prepareStatement("SELECT islandid FROM VSkyblock_Player WHERE uuid = ?")) {
                preparedStatement.setString(1, uuid.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    islandId = resultSet.getInt("islandid");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            int finalIslandId = islandId;
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(finalIslandId));
        });
    }

    /**
     * Gets the island id from a player (database action).
     *
     * @param uuid      The unique id of a player.
     * @param callback  Returns the island on which the player is playing.
     */
    public void getIslandNameFromPlayer(UUID uuid, Consumer<String> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String island = null;
            try (Connection connection = connector.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT i.island AS island FROM VSkyblock_Island AS i" +
                         " INNER JOIN VSkyblock_Player AS p" +
                         " ON i.islandid = p.islandid" +
                         " WHERE p.uuid = ?")) {
                preparedStatement.setString(1, uuid.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    island = resultSet.getString("island");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String finalIsland = island;
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(finalIsland));
        });
    }

    /**
     * Gets all members of an island (database action).
     * Does not matter if a player is the owner of the island. He will be listed as well.
     *
     * @param islandid  The id of an island.
     * @param callback  Returns all members of the island.
     */
    public void getIslandMembers(Integer islandid, Consumer<List<String>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<String> islandmembers = new ArrayList<>();
            try (Connection connection = connector.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT playername FROM VSkyblock_Player WHERE islandid = ?")) {
                preparedStatement.setInt(1, islandid);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    islandmembers.add(resultSet.getString("playername"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(islandmembers));
        });
    }

    /**
     * Gets all islands without members (database action).
     *
     * @param callback Returns a list of all empty islands.
     */
    public void getEmptyIslands(Consumer<List<String>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<Integer> islandids = new ArrayList<>();
            List<String> emptyislands = new ArrayList<>();
            try (Connection connection = connector.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT islandid FROM VSkyblock_Island WHERE islandid NOT IN (SELECT islandid FROM VSkyblock_Player)")) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    emptyislands.add("VSkyblockIsland_" + resultSet.getInt("islandid"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(emptyislands));
        });
    }

    /**
     * Gets the island level from a player (database action).
     *
     * @param uuid      The unique id of a player.
     * @param callback  Returns the island level of a player.
     */
    public void getIslandLevelFromUuid(UUID uuid, Consumer<Integer> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = connector.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT i.islandlevel AS islandlevel FROM VSkyblock_Island AS i" +
                        " INNER JOIN VSkyblock_Player AS p" +
                        " ON i.islandid = p.islandid" +
                        " WHERE p.uuid = ?")) {
                preparedStatement.setString(1, uuid.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                int islandLevel;
                if (resultSet.next()) {
                    islandLevel = resultSet.getInt("islandlevel");
                } else {
                    islandLevel = 0;
                }
                Bukkit.getScheduler().runTask(plugin, () -> callback.accept(islandLevel));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Gets the challenge counts for a specific difficulty from a player (database action).
     *
     * @param islandid  The id of an island.
     * @param callback  Returns a cache with the challenge counts.
     */
    public void getIslandChallenges(final int islandid, final Consumer<ChallengesCache> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            ChallengesCache cache = new ChallengesCache();
            try (Connection connection = connector.getConnection()) {
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

            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(cache));
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
            Connection connection = connector.getConnection();
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
                    if (resultSet.next()) {
                        islandid = resultSet.getInt("islandid");
                    }
                    if (islandid != 0) {
                        PreparedStatement preparedStatement1;
                        preparedStatement1 = connection.prepareStatement("SELECT island FROM VSkyblock_Island WHERE islandid = ?");
                        preparedStatement1.setInt(1, islandid);
                        ResultSet resultSet1 = preparedStatement1.executeQuery();
                        if (resultSet1.next()) {
                            islandname = resultSet1.getString("island");
                        }
                        preparedStatement1.close();
                        PreparedStatement preparedStatementGetGeneratorLevel;
                        preparedStatementGetGeneratorLevel = connection.prepareStatement("SELECT cobblestonelevel FROM VSkyblock_Island WHERE islandid = ?");
                        preparedStatementGetGeneratorLevel.setInt(1, islandid);
                        ResultSet resultSet2 = preparedStatementGetGeneratorLevel.executeQuery();
                        if (resultSet2.next()) {
                            cobblestonelevel = resultSet2.getInt("cobblestonelevel");
                        }
                        preparedStatementGetGeneratorLevel.close();

                        PreparedStatement preparedStatementGetIslandLevel;
                        preparedStatementGetIslandLevel = connection.prepareStatement("SELECT islandlevel FROM VSkyblock_Island WHERE islandid = ?");
                        preparedStatementGetIslandLevel.setInt(1, islandid);
                        ResultSet resultSet3 = preparedStatementGetIslandLevel.executeQuery();
                        if (resultSet3.next()) {
                            islandlevel = resultSet3.getInt("islandlevel");
                        }
                        preparedStatementGetIslandLevel.close();


                        if (islandname != null) {
                            World w = plugin.getServer().getWorld(islandname);
                            PreparedStatement getIslandhome;
                            getIslandhome = connection.prepareStatement("SELECT * FROM VSkyblock_IslandLocations WHERE islandid = ?");
                            getIslandhome.setInt(1, islandid);
                            ResultSet r = getIslandhome.executeQuery();
                            if (r.next()) {
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
                        IslandCacheHandler.islandhomes.put(finalIslandname, finalLoc);
                    }
                    if (finalIslandname != null && !finalIslandname.equals("NULL")) {
                        IslandCacheHandler.playerislands.put(player.getUniqueId(), finalIslandname);
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
            try (Connection connection = connector.getConnection()) {
                int islandgeneratorLVL = 0;
                int islandlevel = 0;
                PreparedStatement preparedStatementGetGeneratorLevel;
                preparedStatementGetGeneratorLevel = connection.prepareStatement("SELECT cobblestonelevel FROM VSkyblock_Island WHERE island = ?");
                preparedStatementGetGeneratorLevel.setString(1, islandname);
                ResultSet resultSet = preparedStatementGetGeneratorLevel.executeQuery();
                if (resultSet.next()) {
                    islandgeneratorLVL = resultSet.getInt("cobblestonelevel");
                }
                CobblestoneGenerator.islandGenLevel.put(islandname, islandgeneratorLVL);
                preparedStatementGetGeneratorLevel.close();

                PreparedStatement preparedStatementGetIslandLevel;
                preparedStatementGetIslandLevel = connection.prepareStatement("SELECT islandlevel FROM VSkyblock_Island WHERE island = ?");
                preparedStatementGetIslandLevel.setString(1, islandname);
                ResultSet resultSet1 = preparedStatementGetIslandLevel.executeQuery();
                if (resultSet1.next()) {
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
    public void getHighestIslands(Consumer<List<String>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<Integer> islandids = new ArrayList<>();
            List<Integer> islandlevels = new ArrayList<>();
            List<String> playersperisland = new ArrayList<>();
            try (Connection connection = connector.getConnection()) {
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

            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(playersperisland));
        });
    }

    /**
     * Checks if an island is visitable.
     *
     * @param islandid  The id of the island.
     * @param callback  Returns true if the island is visitable (boolean).
     */
    public void isIslandVisitable(int islandid, Consumer<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean visitable = false;
            try (Connection connection = connector.getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Island WHERE islandid = ?");
                preparedStatement.setInt(1, islandid);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    visitable = resultSet.getBoolean("visit");
                }

                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            final boolean finalvisitable = visitable;
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(finalvisitable));
        });
    }

    /**
     * Checks if players need to request to visit the island.
     *
     * @param islandid  The id of the island.
     * @param callback  Returns true if players need to request.
     */
    public void islandNeedsRequestForVisit(int islandid, Consumer<Boolean> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean needsrequest = false;
            try (Connection connection = connector.getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Island WHERE islandid = ?");
                preparedStatement.setInt(1, islandid);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    needsrequest = resultSet.getBoolean("visitneedsrequest");
                }

                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            final boolean finalneedsrequest = needsrequest;
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(finalneedsrequest));
        });
    }

    /**
     * Returns all options for an island.
     *
     * @param islandid  The id of the island
     * @param callback  Returns all options for the given island.
     */
    public void getIslandOptions(final int islandid, final Consumer<IslandOptionsCache> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            IslandOptionsCache islandOptionsCache = new IslandOptionsCache();
            PreparedStatement preparedStatement;
            try (Connection connection = connector.getConnection()) {
                preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Island WHERE islandid = ?");
                preparedStatement.setInt(1, islandid);
                ResultSet r = preparedStatement.executeQuery();
                if (r.next()) {
                    islandOptionsCache.setVisit(r.getBoolean("visit"));
                    islandOptionsCache.setDifficulty(r.getString("difficulty"));
                    islandOptionsCache.setNeedRequest(r.getBoolean("visitneedsrequest"));
                }
                preparedStatement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(islandOptionsCache));
        });
    }

    /**
     * Refreshes deathcounts of all online players.
     *
     * @param onlineplayers A list of all players currently online.
     */
    public void refreshDeathCounts(List<Player> onlineplayers) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = connector.getConnection()) {
                for (Player player : onlineplayers) {
                    int deathcount = 0;
                    PreparedStatement preparedStatement;
                    preparedStatement = connection.prepareStatement("SELECT deaths FROM VSkyblock_Player WHERE uuid = ?");
                    preparedStatement.setString(1, player.getUniqueId().toString());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
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
    public void getLastLocation(final UUID uuid, final Consumer<Location> callback) {
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
            try (Connection connection = connector.getConnection()) {
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
                    if (plugin.getWorldManager().getLoadedWorlds().contains(lastWorld)) {
                        if (plugin.getServer().getWorld(lastWorld).getEnvironment().equals(World.Environment.NETHER) || lastWorld.equals(island)) {
                            World world = plugin.getServer().getWorld(lastWorld);
                            location = new Location(world, x, y, z, (float) yaw, (float) pitch);
                        }
                    }
                }
            }

            Location finalLocation = location;
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(finalLocation));
        });
    }

    /**
     * Calculates the challenge points for an island.
     * Used for the calculation of the islands level.
     *
     * @param islandid  The id of the island.
     * @param callback  Returns the points the island will get for all finished challenges.
     */
    public void getIslandsChallengePoints(int islandid, Consumer<Integer> callback) {
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
            callback.accept(totalChallengePoints);
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
    public void getNetherHome(final UUID uuid, final Consumer<Location> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Location location = null;
            double x = 0;
            double y = 0;
            double z = 0;
            double yaw = 0;
            String world = null;
            int islandid = 0;
            try (Connection connection = connector.getConnection()) {
                PreparedStatement getislandid;
                getislandid = connection.prepareStatement("SELECT islandid FROM VSkyblock_Player WHERE uuid = ?");
                getislandid.setString(1, uuid.toString());
                ResultSet r = getislandid.executeQuery();
                if (r.next()) {
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
                        if (plugin.getWorldManager().getLoadedWorlds().contains(world)) {
                            World w = plugin.getServer().getWorld(world);
                            location = new Location(w, x, y, z, (float) yaw, 0);
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            Location finalLocation = location;
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(finalLocation));
        });
    }

    /**
     * Returns the island home location.
     *
     * @param world     The name of the world.
     * @param callback  Returns the location.
     */
    public void getIslandSpawn(final String world, final Consumer<Location> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Location location = null;
            double x = 0;
            double y = 67;
            double z = 0;
            float yaw = 0;
            float pitch = 0;
            int islandid = 0;
            try (Connection connection = connector.getConnection()) {
                PreparedStatement getislandid;
                getislandid = connection.prepareStatement("SELECT islandid FROM VSkyblock_Island WHERE island = ?");
                getislandid.setString(1, world);
                ResultSet r = getislandid.executeQuery();
                if (r.next()) {
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
                        if (plugin.getWorldManager().getLoadedWorlds().contains(world)) {
                            World w = plugin.getServer().getWorld(world);
                            location = new Location(w, x, y, z, yaw, pitch);
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            Location finalLocation = location;
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(finalLocation));
        });
    }

}
