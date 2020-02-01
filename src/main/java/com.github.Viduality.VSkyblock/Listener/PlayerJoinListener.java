package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.github.Viduality.VSkyblock.WorldGenerator.WorldGenerator;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private DatabaseWriter databaseWriter = new DatabaseWriter();
    private WorldManager wm = new WorldManager();



    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
        ConfigShorts.loaddefConfig();


        if (wm.getUnloadedWorlds().contains(plugin.getConfig().getString("SpawnWorld"))) {
            wm.loadWorld(plugin.getConfig().getString("SpawnWorld"));
        }
        if (wm.getUnloadedWorlds().contains(plugin.getConfig().getString("NetherWorld"))) {
            wm.loadWorld(plugin.getConfig().getString("NetherWorld"));
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
                            wm.loadWorld(result.getIslandname());
                            Island.playerislands.put(result.getuuid(), result.getIslandname());
                            databaseReader.addToCobbleStoneGenerators(result.getIslandname());
                            if (wm.getSpawnLocation(result.getIslandname()).getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
                                wm.getSpawnLocation(result.getIslandname()).getBlock().getRelative(BlockFace.DOWN).setType(Material.INFESTED_COBBLESTONE);
                            }
                            player.teleport(wm.getSpawnLocation(result.getIslandname()));
                        } else {
                            Island.playerislands.put(result.getuuid(), result.getIslandname());
                            if (wm.getSpawnLocation(result.getIslandname()).getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
                                wm.getSpawnLocation(result.getIslandname()).getBlock().getRelative(BlockFace.DOWN).setType(Material.INFESTED_COBBLESTONE);
                            }
                            player.teleport(wm.getSpawnLocation(result.getIslandname()));
                        }
                    } else {
                        player.teleport(wm.getSpawnLocation(plugin.getConfig().getString("SpawnWorld")));
                        if (result.isKicked()) {
                            ConfigShorts.messagefromString("KickedFromIslandOffline", player);
                            databaseWriter.removeKicked(result.getuuid());
                        }
                    }

                }
            }
        });


        if (plugin.getServer().getWorld("VSkyblockMasterIsland") == null && !wm.getUnloadedWorlds().contains("VSkyblockMasterIsland")) {
            ConfigShorts.broadcastfromString("MasterIsland");
            WorldGenerator.CreateNewMasterIsland(new WorldGenerator.Callback() {
                @Override
                public void onQueryDone(String result) {
                    wm.unloadWorld(result);
                    ConfigShorts.broadcastfromString("MasterIslandReady");
                }
            });
        }
    }
}
