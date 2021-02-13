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

import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DatabaseWriter {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private WorldManager wm = new WorldManager();

    /**
     * Adds a player to the database. (database action)
     * Is called when the player joins and does not exist in the database.
     * Also inserts the player into the challenges tables.
     * @param uuid
     * @param name
     */
    public void addPlayer(UUID uuid, String name) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;

                preparedStatement = connection.prepareStatement("INSERT INTO VSkyblock_Player(uuid, playername) VALUES(?, ?)");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setString(2, name);
                preparedStatement.executeUpdate();
                preparedStatement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Adds an island into the VSkyblock_Island table and assigns it to an player. (database action)
     * Updates the island AND the player data so the island belongs to the player as its owner.
     * @param island (islandname)
     * @param uuid
     */
    public void addIsland(String island, UUID uuid, String difficutly) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("INSERT INTO VSkyblock_Island(island, difficulty) VALUES(?, ?)");
                preparedStatement.setString(1, island);
                preparedStatement.setString(2, difficutly);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            final int[] newislandidarray = {0};
            databaseReader.getislandid(island, result -> {
                newislandidarray[0] = result;
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    int islandid = newislandidarray[0];
                    try (Connection connection = plugin.getdb().getConnection()) {
                        PreparedStatement preparedStatement;
                        preparedStatement = connection.prepareStatement("UPDATE VSkyblock_Player SET islandid = ?, islandowner = true WHERE uuid = ?");
                        preparedStatement.setInt(1, islandid);
                        preparedStatement.setString(2, uuid.toString());
                        preparedStatement.executeUpdate();
                        preparedStatement.close();

                        PreparedStatement preparedStatement1;
                        preparedStatement1 = connection.prepareStatement("INSERT INTO VSkyblock_IslandLocations(islandid) VALUES (?)");
                        preparedStatement1.setInt(1, islandid);
                        preparedStatement1.executeUpdate();
                        preparedStatement1.close();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            });
        });
    }

    /**
     * Removes an player from his island (database action).
     *
     * @param uuid
     */
    public void kickPlayerfromIsland(UUID uuid) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
            try (Connection connection = plugin.getdb().getConnection()) {
                    PreparedStatement preparedStatement;
                    preparedStatement = connection.prepareStatement("UPDATE VSkyblock_Player SET islandid = NULL, owneruuid = NULL, kicked = 1 WHERE uuid = ?");
                    preparedStatement.setString(1, uuid.toString());
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Removes the assignment "kicked" in the database from a specific player database action).
     *
     * @param uuid
     */
    public void removeKicked(UUID uuid) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("UPDATE VSkyblock_Player SET kicked = 0 WHERE uuid = ?");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Updates the owner of an island (database action).
     * Removes the owner title from the old owner and gives it to the new owner.
     * @param oldOwner (uuid)
     * @param newOwner (uuid)
     */
    public void updateOwner(UUID oldOwner, UUID newOwner) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("UPDATE VSkyblock_Player SET islandowner = 0 WHERE uuid = ?");
                preparedStatement.setString(1, oldOwner.toString());
                preparedStatement.executeUpdate();
                preparedStatement.close();
                PreparedStatement preparedStatement1;
                preparedStatement1 = connection.prepareStatement("UPDATE VSkyblock_Player SET islandowner = 1 WHERE uuid = ?");
                preparedStatement1.setString(1, newOwner.toString());
                preparedStatement1.executeUpdate();
                preparedStatement1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Removes an player from his current island (database action).
     *
     * @param uuid
     */
    public void leavefromIsland(UUID uuid) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("UPDATE VSkyblock_Player SET islandid = NULL, owneruuid = NULL, islandowner = 0 WHERE uuid = ?");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Updates an players island (database action).
     * Overwrites the old island with the new one.
     * @param uuid
     * @param islandid
     * @param islandowner
     */
    public void updatePlayersIsland(UUID uuid, int islandid, boolean islandowner) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Integer islandownerInt;
            if (islandowner) {
                islandownerInt = 1;
            } else {
                islandownerInt = 0;
            }
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;

                preparedStatement = connection.prepareStatement("UPDATE VSkyblock_Player SET islandid = ?, islandowner = ? WHERE uuid = ?");
                preparedStatement.setInt(1, islandid);
                preparedStatement.setString(2, String.valueOf(islandownerInt));
                preparedStatement.setString(3, uuid.toString());
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Deletes an island (database action).
     *
     * @param island
     */
    public void deleteIsland(String island) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                int islandid = 0;
                PreparedStatement getid;
                getid = connection.prepareStatement("SELECT * FROM VSkyblock_Island WHERE island = ?");
                getid.setString(1, island);
                ResultSet r = getid.executeQuery();
                while (r.next()) {
                    islandid = r.getInt("islandid");
                }
                getid.close();

                PreparedStatement deleteIsland;
                deleteIsland = connection.prepareStatement("DELETE FROM VSkyblock_Island WHERE island = ?");
                deleteIsland.setString(1, island);
                deleteIsland.executeUpdate();
                deleteIsland.close();

                if (islandid != 0) {
                    PreparedStatement deleteFromLocations;
                    deleteFromLocations = connection.prepareStatement("DELETE FROM VSkyblock_IslandLocations WHERE islandid = ?");
                    deleteFromLocations.setInt(1, islandid);
                    deleteFromLocations.executeUpdate();
                    deleteIsland.close();

                    PreparedStatement deleteFromChallenges;
                    deleteFromChallenges = connection.prepareStatement("DELETE FROM VSkyblock_Challenges WHERE islandid = ?");
                    deleteFromChallenges.setInt(1, islandid);
                    deleteFromChallenges.executeUpdate();
                    deleteFromChallenges.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Updates the count of an challenge (island).
     *
     * @param islandid  The id of an island.
     * @param mySQLKey  The mySQL column name.
     * @param count     The new challenge count.
     */
    public void updateChallengeCount(int islandid, String mySQLKey, int count) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                String statement = "INSERT INTO VSkyblock_Challenges(islandid, count, challenge) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE count = VALUES(count)";
                PreparedStatement updateCount = connection.prepareStatement(statement);
                updateCount.setInt(1, islandid);
                updateCount.setInt(2, count);
                updateCount.setString(3, mySQLKey);
                updateCount.executeUpdate();
                updateCount.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Updates the level of an island (database action).
     *
     * @param islandid
     * @param level
     */
    public void updateIslandLevel(int islandid, Integer level, Integer totalblocks, UUID uuid) {
        databaseReader.getIslandMembers(islandid, result -> plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement updateChallengeCount;
                updateChallengeCount = connection.prepareStatement("UPDATE VSkyblock_Island SET islandlevel = ?, totalblocks = ? WHERE islandid = ?");
                updateChallengeCount.setInt(1, level);
                updateChallengeCount.setInt(2, totalblocks);
                updateChallengeCount.setInt(3, islandid);
                updateChallengeCount.executeUpdate();
                updateChallengeCount.close();

                PreparedStatement updatehighestreachedlevel;
                updatehighestreachedlevel = connection.prepareStatement("UPDATE VSkyblock_Player SET highestreachedlevel = ? WHERE uuid = ? AND highestreachedlevel < ?");
                updatehighestreachedlevel.setInt(1, level);
                updatehighestreachedlevel.setString(2, uuid.toString());
                updatehighestreachedlevel.setInt(3, level);
                updatehighestreachedlevel.executeUpdate();
                updatehighestreachedlevel.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));
    }

    /**
     * Writes the given options for the island of the given player into the database.
     * @param player
     * @param visit
     * @param needsRequest
     * @param difficulty
     * @param callback
     */
    public void updateIslandOptions(Player player, boolean visit, boolean needsRequest, String difficulty, final Callback callback) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                int islandid = 0;
                String islandname = null;
                PreparedStatement preparedStatementREAD;
                preparedStatementREAD = connection.prepareStatement("SELECT islandid FROM VSkyblock_Player WHERE uuid = ?");
                preparedStatementREAD.setString(1, player.getUniqueId().toString());
                ResultSet resultSet = preparedStatementREAD.executeQuery();
                while (resultSet.next()) {
                    islandid = resultSet.getInt("islandid");
                }
                preparedStatementREAD.close();


                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("UPDATE VSkyblock_Island SET visit = ?, visitneedsrequest = ?, difficulty = ? WHERE islandid = ?");
                preparedStatement.setBoolean(1, visit);
                preparedStatement.setBoolean(2, needsRequest);
                preparedStatement.setString(3, difficulty);
                preparedStatement.setInt(4, islandid);
                preparedStatement.executeUpdate();
                preparedStatement.close();

                PreparedStatement preparedStatementGetIslandName;
                preparedStatementGetIslandName = connection.prepareStatement("SELECT island FROM VSkyblock_Island WHERE islandid = ?");
                preparedStatementGetIslandName.setInt(1, islandid);
                ResultSet resultSet1 = preparedStatementGetIslandName.executeQuery();
                while (resultSet1.next()) {
                    islandname = resultSet1.getString("island");
                }
                preparedStatementGetIslandName.close();

                wm.setOption(islandname, "difficulty", difficulty);

                Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(true));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Updates the level of an islands cobblestonegenerator (database action).
     *
     * @param islandname
     * @param level
     */
    public void updateCobblestoneGeneratorLevel(String islandname, Integer level) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement updateGeneratorLevel;
                updateGeneratorLevel = connection.prepareStatement("UPDATE VSkyblock_Island SET cobblestonelevel = ? WHERE island = ?");
                updateGeneratorLevel.setInt(1, level);
                updateGeneratorLevel.setString(2, islandname);
                updateGeneratorLevel.executeUpdate();
                updateGeneratorLevel.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Updates the death count of a specific player.
     *  @param uuid
     * @param count
     */
    public void updateDeathCount(UUID uuid, int count) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("UPDATE VSkyblock_Player SET deaths = ? WHERE uuid = ?");
                preparedStatement.setInt(1, count);
                preparedStatement.setString(2, uuid.toString());
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Updates the players name after a name change.
     *
     * @param uuid
     * @param name
     */
    public void updatePlayerName(UUID uuid, String name) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("UPDATE VSkyblock_Player SET playername = ? WHERE uuid = ?");
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, uuid.toString());
                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Saves the last location of a specific player.
     *
     * @param uuid
     * @param loc
     */
    public void savelastLocation(UUID uuid, Location loc) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        double pitch = loc.getPitch();
        double yaw = loc.getYaw();
        String world = loc.getWorld().getName();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement savelastLoc;
                savelastLoc = connection.prepareStatement("UPDATE VSkyblock_Player SET lastX = ?, lastY = ?, lastZ = ?, lastPitch = ?, lastYaw = ?, lastWorld = ? WHERE uuid = ?");
                savelastLoc.setDouble(1, x);
                savelastLoc.setDouble(2, y);
                savelastLoc.setDouble(3, z);
                savelastLoc.setDouble(4, pitch);
                savelastLoc.setDouble(5, yaw);
                savelastLoc.setString(6, world);
                savelastLoc.setString(7, uuid.toString());
                savelastLoc.executeUpdate();
                savelastLoc.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Saves the given location as the nether home of the given island.
     * Rotates the location to the cardinal direction he is facing.
     *
     * @param islandid  The id of the island
     * @param loc       The location of the home point.
     */
    public void saveNetherHome(int islandid, Location loc) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        boolean northsouth = false;

        if (yaw > 135 && yaw <= 225) {
            loc.setYaw(180);
            northsouth = true;
        } else if (yaw > 315 && yaw <= 360) {
            loc.setYaw(0);
            northsouth = true;
        } else if (yaw > 0 && yaw <= 45) {
            loc.setYaw(0);
            northsouth = true;
        } else if (yaw > 45 && yaw <= 135) {
            loc.setYaw(90);
        } else {
            loc.setYaw(270);
        }
        World world = loc.getWorld();
        boolean facingPortal = false;

        if (northsouth) {
            for (int i = -3; i < 4; i++) {
                if (i != 0) {
                    double currentZ = z + i;
                    Location l = new Location(world, x, y, currentZ);
                    if (world.getBlockAt(l).getType().equals(Material.OBSIDIAN)) {
                        facingPortal = true;
                        break;
                    }
                }
            }
        } else {
            for (int i = -3; i < 4; i++) {
                if (i != 0) {
                    double currentX = x + i;
                    Location l = new Location(world, currentX, y, z);
                    if (world.getBlockAt(l).getType().equals(Material.OBSIDIAN)) {
                        facingPortal = true;
                        break;
                    }
                }
            }
        }
        double rotation = loc.getYaw();
        if (facingPortal) {
            rotation = rotation + 90;
        }
        loc.setYaw((float) rotation);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement savelastLoc;
                savelastLoc = connection.prepareStatement("UPDATE VSkyblock_IslandLocations SET netherX = ?, netherY = ?, netherZ = ?, netherYaw = ?, netherWorld = ? WHERE islandid = ?");
                savelastLoc.setDouble(1, x);
                savelastLoc.setDouble(2, y);
                savelastLoc.setDouble(3, z);
                savelastLoc.setFloat(4, loc.getYaw());
                savelastLoc.setString(5, world.getName());
                savelastLoc.setString(6, String.valueOf(islandid));
                savelastLoc.executeUpdate();
                savelastLoc.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Saves the given location as the islands home point.
     *
     * @param islandid  The id of the island.
     * @param loc       The location of the home point.
     */
    public void setIslandSpawn(int islandid, Location loc) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = plugin.getdb().getConnection()) {
                PreparedStatement savelastLoc;
                savelastLoc = connection.prepareStatement("UPDATE VSkyblock_IslandLocations SET spawnX = ?, spawnY = ?, spawnZ = ?, spawnYaw = ?, spawnPitch = ? WHERE islandid = ?");
                savelastLoc.setDouble(1, x);
                savelastLoc.setDouble(2, y);
                savelastLoc.setDouble(3, z);
                savelastLoc.setFloat(4, yaw);
                savelastLoc.setFloat(5, pitch);
                savelastLoc.setString(6, String.valueOf(islandid));
                savelastLoc.executeUpdate();
                savelastLoc.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public interface Callback {
        public void onQueryDone(boolean done);
    }
}
