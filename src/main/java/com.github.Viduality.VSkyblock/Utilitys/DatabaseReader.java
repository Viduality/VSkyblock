package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.SQLConnector;
import com.github.Viduality.VSkyblock.VSkyblock;
import io.netty.util.concurrent.Future;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DatabaseReader {


    private SQLConnector getDatabase = new SQLConnector();
    private VSkyblock plugin = VSkyblock.getInstance();

    /**
     * Gets the data of an player (database action).
     *
     * @param uuid
     * @param callback
     */
    public void getPlayerData(final String uuid, final Callback callback) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                DatabaseCache databaseCache1 = new DatabaseCache();
                Connection connection = getDatabase.getConnection();
                PreparedStatement preparedStatement;
                try {
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

                finally {
                    getDatabase.closeConnection(connection);
                }


                final DatabaseCache databaseCache = databaseCache1;

                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(databaseCache);
                    }
                });
            }
        });
    }

    /**
     * Gets the name of the next island for the database (database action).
     * (Ignore the boolean it will be true all the time.)
     * @param callback
     */
    public void getLatestIsland(final CallbackStrings callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                int latestIsland = 0;
                Connection connection = getDatabase.getConnection();
                PreparedStatement preparedStatement;
                try {
                    preparedStatement = connection.prepareStatement("SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = \"VSkyblock\" AND TABLE_NAME = \"VSkyblock_Island\"");
                    ResultSet r = preparedStatement.executeQuery();
                    while (r.next()) {
                        latestIsland = r.getInt("AUTO_INCREMENT");
                    }
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    getDatabase.closeConnection(connection);
                }
                final String islandname = "VSkyblockIsland_" + latestIsland;
                final boolean a = true;
                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(islandname, a);
                    }
                });
            }
        });
    }

    /**
     * Gets the island id of an given islandname (database action).
     *
     * @param island
     * @param callback
     */
    public void getislandid(String island, CallbackINT callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                DatabaseCache databaseCache = new DatabaseCache();
                Connection connection = getDatabase.getConnection();
                try {
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
                finally {
                    getDatabase.closeConnection(connection);
                }
                final int islandid = databaseCache.getIslandId();
                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(islandid);
                    }
                });
            }
        });
    }

    /**
     * Checks if an island has members (database action).
     *
     * @param islandid
     * @param callback
     */
    public void hasislandmembers(int islandid, CallbackBoolean callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                DatabaseCache databaseCache = new DatabaseCache();
                Connection connection = getDatabase.getConnection();
                try {
                    PreparedStatement preparedStatement;
                    preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Player WHERE islandid = ?");
                    preparedStatement.setInt(1, islandid);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        databaseCache.addIslandMember(resultSet.getString("playername"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    getDatabase.closeConnection(connection);
                }
                boolean hasmembers;
                if (databaseCache.getislandmembers().size() > 1) {
                    hasmembers = true;
                } else {
                    hasmembers = false;
                }
                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(hasmembers);
                    }
                });
            }
        });
    }

    /**
     * Gets the island id from a player (database action).
     *
     * @param uuid
     * @param callback
     */
    public void getislandidfromplayer(String uuid, CallbackINT callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                DatabaseCache databaseCache = new DatabaseCache();
                Connection connection = getDatabase.getConnection();
                try {
                    PreparedStatement preparedStatement;
                    preparedStatement = connection.prepareStatement("SELECT islandid FROM VSkyblock_Player WHERE uuid = ?");
                    preparedStatement.setString(1, uuid);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        databaseCache.setIslandId(resultSet.getInt("islandid"));
                    }
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                finally {
                    getDatabase.closeConnection(connection);
                }
                final int islandid = databaseCache.getIslandId();
                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(islandid);
                    }
                });
            }
        });
    }

    /**
     * Gets all members of an island (database action).
     * Does not matter if a player is the owner of the island. He will be listed aswell.
     * @param islandid
     * @param callback
     */
    public void getIslandMembers(Integer islandid, CallbackList callback) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                List<String> islandmembers = new ArrayList<>();
                Connection connection = getDatabase.getConnection();
                try {
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
                finally {
                    getDatabase.closeConnection(connection);
                }

                final List<String> result = islandmembers;
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(result);
                    }
                });
            }
        });
    }

    /**
     * Gets all islands without members (database action).
     *
     * @param callback
     */
    public void getemptyIslands(CallbackList callback) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                List<Integer> islandids = new ArrayList<>();
                List<String> emptyislands = new ArrayList<>();
                Connection connection = getDatabase.getConnection();
                try {
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
                finally {
                    getDatabase.closeConnection(connection);
                }

                final List<String> result = emptyislands;
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(result);
                    }
                });
            }
        });
    }

    public void checkforfirstJoin(final String uuid, final Callback callback) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                DatabaseCache databaseCache1 = new DatabaseCache();
                Connection connection = getDatabase.getConnection();
                PreparedStatement preparedStatement;
                try {
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

                finally {
                    getDatabase.closeConnection(connection);
                }


                final DatabaseCache databaseCache = databaseCache1;

                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(databaseCache);
                    }
                });
            }
        });
    }

    /**
     * Gets the island level from a player (database action).
     *
     * @param uuid
     * @param callback
     */
    public void getislandlevelfromuuid(String uuid, CallbackINT callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                DatabaseCache databaseCache = new DatabaseCache();
                Connection connection = getDatabase.getConnection();
                try {
                    int islandid = 0;
                    PreparedStatement prep;
                    prep = connection.prepareStatement("SELECT islandid FROM VSkyblock_Player WHERE uuid = ?");
                    prep.setString(1, uuid);
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
                finally {
                    getDatabase.closeConnection(connection);
                }
                final int islandlevel = databaseCache.getIslandLevel();
                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(islandlevel);
                    }
                });
            }
        });
    }

    /**
     * Gets the challenge counts for a specific difficulty from a player (database action).
     *
     * @param uuid
     * @param challengeTable
     * @param callback
     */
    public void getPlayerChallenges(final String uuid, String challengeTable, final cCallback callback) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {

                ChallengesCache cache = new ChallengesCache();
                Connection connection = getDatabase.getConnection();
                PreparedStatement preparedStatement;
                try {
                    preparedStatement = connection.prepareStatement("SELECT * FROM " + challengeTable + " WHERE uuid = ?");
                    preparedStatement.setString(1, uuid);
                    ResultSet r = preparedStatement.executeQuery();
                    while (r.next()) {
                        cache.setc1(r.getInt("c1"));
                        cache.setc2(r.getInt("c2"));
                        cache.setc3(r.getInt("c3"));
                        cache.setc4(r.getInt("c4"));
                        cache.setc5(r.getInt("c5"));
                        cache.setc6(r.getInt("c6"));
                        cache.setc7(r.getInt("c7"));
                        cache.setc8(r.getInt("c8"));
                        cache.setc9(r.getInt("c9"));
                        cache.setc10(r.getInt("c10"));
                        cache.setc11(r.getInt("c11"));
                        cache.setc12(r.getInt("c12"));
                        cache.setc13(r.getInt("c13"));
                        cache.setc14(r.getInt("c14"));
                        cache.setc15(r.getInt("c15"));
                        cache.setc16(r.getInt("c16"));
                        cache.setc17(r.getInt("c17"));
                        cache.setc18(r.getInt("c18"));
                    }
                    preparedStatement.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                finally {
                    getDatabase.closeConnection(connection);
                }


                final ChallengesCache cache1 = cache;

                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(cache1);
                    }
                });
            }
        });
    }

    /**
     * Re-writes all active islands into an islandlist.
     * Used after a reload since the list loses its contents when the plugin is reloaded.
     * The list is used to load and unload the islands.
     * @param onlineplayers
     */
    public void refreshIslands(List<Player> onlineplayers) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Connection connection = getDatabase.getConnection();
                try {
                    for (int i = 0; i < onlineplayers.size(); i++) {
                        String islandname = null;
                        int islandid = 0;
                        PreparedStatement preparedStatement;
                        preparedStatement = connection.prepareStatement("SELECT islandid FROM VSkyblock_Player WHERE uuid = ?");
                        preparedStatement.setString(1, onlineplayers.get(i).getUniqueId().toString());
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
                        }
                        if (islandname != null && !islandname.equals("NULL")) {
                            Island.playerislands.put(onlineplayers.get(i).getUniqueId().toString(), islandname);
                        }
                        preparedStatement.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                finally {
                    getDatabase.closeConnection(connection);
                }
            }
        });
    }

    /**
     * Gets the 5 highest islands. Sorted by level.
     *
     * @param callback
     */
    public void getHighestIslands(CallbackList callback) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                List<Integer> islandids = new ArrayList<>();
                List<Integer> islandlevels = new ArrayList<>();
                List<String> playersperisland = new ArrayList<>();
                Connection connection = getDatabase.getConnection();
                try {
                    PreparedStatement preparedStatement;
                    preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Island ORDER BY islandlevel desc limit 5");
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
                finally {
                    getDatabase.closeConnection(connection);
                }

                final List<String> result = playersperisland;
                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(result);
                    }
                });
            }
        });
    }

    /**
     * Checks if an island is visitable.
     *
     * @param islandid
     * @param callback
     */
    public void isislandvisitable(int islandid, CallbackBoolean callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Connection connection = getDatabase.getConnection();
                boolean visitable = false;
                try {
                    PreparedStatement preparedStatement;
                    preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Island WHERE islandid = ?");
                    preparedStatement.setInt(1, islandid);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        visitable = resultSet.getBoolean("visit");
                        System.out.println(resultSet.getBoolean("visit"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    getDatabase.closeConnection(connection);
                }
                final boolean finalvisitable = visitable;
                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(finalvisitable);
                    }
                });
            }
        });
    }

    /**
     * Returns all options for an island.
     *
     * @param islandid
     * @param callback
     */
    public void getIslandOptions(final int islandid, final isoptionsCallback callback) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                IslandOptionsCache islandOptionsCache = new IslandOptionsCache();
                Connection connection = getDatabase.getConnection();
                PreparedStatement preparedStatement;
                try {
                    preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Island WHERE islandid = ?");
                    preparedStatement.setInt(1, islandid);
                    ResultSet r = preparedStatement.executeQuery();
                    while (r.next()) {
                        islandOptionsCache.setVisit(r.getBoolean("visit"));
                        islandOptionsCache.setDifficulty(r.getString("difficulty"));
                    }
                    preparedStatement.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                finally {
                    getDatabase.closeConnection(connection);
                }


                final IslandOptionsCache cache = islandOptionsCache;

                Bukkit.getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        callback.onQueryDone(cache);
                    }
                });
            }
        });
    }








    public interface Callback {
        public void onQueryDone(DatabaseCache result);
    }

    public interface cCallback {
        public void onQueryDone(ChallengesCache cache);
    }

    public interface isoptionsCallback {
        public void onQueryDone(IslandOptionsCache isoptionsCache);
    }

    public interface CallbackString {
        public void onQueryDone(String result);
    }

    public interface CallbackINT {
        public void onQueryDone(int result);
    }

    public interface CallbackBoolean {
        public void onQueryDone(boolean result);
    }

    public interface CallbackList {
        public void onQueryDone(List<String> result);
    }

    public interface CallbackStrings {
        public void onQueryDone(String result, boolean a);
    }
}
