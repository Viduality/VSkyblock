package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.SQLConnector;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.github.Viduality.VSkyblock.WorldGenerator.WorldGenerator;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerJoinListener implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private DatabaseWriter databaseWriter = new DatabaseWriter();
    private SQLConnector getDatabase = new SQLConnector();



    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
        ConfigShorts.loaddefConfig();
        if (plugin.getMV().getCore().getMVWorldManager().getUnloadedWorlds().contains(plugin.getConfig().getString("SpawnWorld"))) {
            plugin.getMV().getCore().getMVWorldManager().loadWorld(plugin.getConfig().getString("SpawnWorld"));
        }

        if (plugin.getMV().getCore().getMVWorldManager().getUnloadedWorlds().contains(plugin.getConfig().getString("NetherWorld"))) {
            plugin.getMV().getCore().getMVWorldManager().loadWorld(plugin.getConfig().getString("NetherWorld"));
        }
        Player player = playerJoinEvent.getPlayer();
        databaseReader.getPlayerData(player.getUniqueId().toString(), new DatabaseReader.Callback() {
            @Override
            public void onQueryDone(DatabaseCache result) {
                if (result.getuuid() == null) {
                    databaseWriter.addPlayer(player.getUniqueId().toString(), player.getName());
                } else {
                    if (result.getIslandname() != null) {
                        if (!Island.playerislands.containsValue(result.getIslandname())) {
                            plugin.getMV().getCore().getMVWorldManager().loadWorld(result.getIslandname());
                            Island.playerislands.put(result.getuuid(), result.getIslandname());
                            if (plugin.getMV().getCore().getMVWorldManager().getMVWorld(result.getIslandname()).getSpawnLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
                                plugin.getMV().getCore().getMVWorldManager().getMVWorld(result.getIslandname()).getSpawnLocation().getBlock().getRelative(BlockFace.DOWN).setType(Material.INFESTED_COBBLESTONE);
                            }
                            player.teleport(plugin.getMV().getCore().getMVWorldManager().getMVWorld(result.getIslandname()).getSpawnLocation());
                        } else {
                            Island.playerislands.put(result.getuuid(), result.getIslandname());
                            if (plugin.getMV().getCore().getMVWorldManager().getMVWorld(result.getIslandname()).getSpawnLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
                                plugin.getMV().getCore().getMVWorldManager().getMVWorld(result.getIslandname()).getSpawnLocation().getBlock().getRelative(BlockFace.DOWN).setType(Material.INFESTED_COBBLESTONE);
                            }
                            player.teleport(plugin.getMV().getCore().getMVWorldManager().getMVWorld(result.getIslandname()).getSpawnLocation());
                        }
                    } else {
                        player.teleport(plugin.getMV().getCore().getMVWorldManager().getMVWorld(plugin.getConfig().getString("SpawnWorld")).getSpawnLocation());

                    }

                }
            }
        });




        if (plugin.getMV().getCore().getMVWorldManager().getMVWorld("VSkyblockMasterIsland") == null && !plugin.getMV().getCore().getMVWorldManager().getUnloadedWorlds().contains("VSkyblockMasterIsland")) {
            ConfigShorts.broadcastfromString("MasterIsland");
            WorldGenerator.CreateNewMasterIsland(new WorldGenerator.Callback() {
                @Override
                public void onQueryDone(String result) {
                    plugin.getMV().getCore().getMVWorldManager().unloadWorld(result);
                    ConfigShorts.broadcastfromString("MasterIslandReady");
                }
            });
        }
    }






    private void checkforfirstJoin(Player player) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                String uuid = player.getUniqueId().toString();
                DatabaseCache databaseCache = new DatabaseCache();
                Connection connection = getDatabase.getConnection();

                PreparedStatement preparedStatement;
                try {
                    preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Player WHERE uuid = ?");
                    preparedStatement.setString(1, uuid);
                    ResultSet r = preparedStatement.executeQuery();
                    while (r.next()) {
                        databaseCache.setUuid(r.getString("uuid"));
                    }
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (databaseCache.getuuid() == null) {
                    databaseWriter.addPlayer(player.getUniqueId().toString(), player.getName());
                } else {
                    loadWorld(databaseCache.getIslandname(), player.getUniqueId().toString());
                }
            }
        });
    }


    private void loadWorld(String world, String player) {
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
