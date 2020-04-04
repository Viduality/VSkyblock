package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.*;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.github.Viduality.VSkyblock.WorldGenerator.WorldGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerJoinListener implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private DatabaseWriter databaseWriter = new DatabaseWriter();
    private WorldManager wm = new WorldManager();



    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        PotionEffect potionEffectBlindness = new PotionEffect(PotionEffectType.BLINDNESS, 50, 1);
        PotionEffect potionEffectNightVision = new PotionEffect(PotionEffectType.NIGHT_VISION, 50, 1);
        player.addPotionEffect(potionEffectBlindness);
        player.addPotionEffect(potionEffectNightVision);
        Location location = player.getLocation();
        location.setPitch(-90);
        player.teleport(location);

        ConfigShorts.loaddefConfig();


        if (wm.getUnloadedWorlds().contains(plugin.getConfig().getString("SpawnWorld"))) {
            wm.loadWorld(plugin.getConfig().getString("SpawnWorld"));
        }
        if (wm.getUnloadedWorlds().contains(plugin.getConfig().getString("NetherWorld"))) {
            wm.loadWorld(plugin.getConfig().getString("NetherWorld"));
        }


        databaseReader.getPlayerData(player.getUniqueId().toString(), new DatabaseReader.Callback() {
            @Override
            public void onQueryDone(DatabaseCache result) {
                if (result.getuuid() == null) {
                    databaseWriter.addPlayer(player.getUniqueId().toString(), player.getName());
                } else {
                    if (!result.getName().equals(player.getName())) {
                        databaseWriter.updatePlayerName(player.getUniqueId().toString(), player.getName());
                    }
                    if (plugin.scoreboardmanager.doesobjectiveexist("deaths")) {
                        if (plugin.scoreboardmanager.addPlayerToObjective(player, "deaths")) {
                            plugin.scoreboardmanager.updatePlayerScore(player.getName(), "deaths", result.getDeathCount());
                        }
                    }
                    if (result.getIslandname() != null) {
                        if (Island.emptyloadedislands.asMap().containsKey(result.getIslandname())) {
                            Island.emptyloadedislands.asMap().remove(result.getIslandname());
                        }
                        if (!Island.playerislands.containsValue(result.getIslandname())) {
                            wm.loadWorld(result.getIslandname());
                            Island.playerislands.put(result.getuuid(), result.getIslandname());
                            databaseReader.addToCobbleStoneGenerators(result.getIslandname());
                            if (wm.getSpawnLocation(result.getIslandname()).getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
                                wm.getSpawnLocation(result.getIslandname()).getBlock().getRelative(BlockFace.DOWN).setType(Material.INFESTED_COBBLESTONE);
                            }
                            wm.loadWorld(result.getIslandname());
                            databaseReader.getlastLocation(result.getuuid(), new DatabaseReader.CallbackLocation() {
                                @Override
                                public void onQueryDone(Location loc) {
                                    if (loc != null) {
                                        player.teleport(loc);
                                    } else {
                                        player.teleport(wm.getSpawnLocation(result.getIslandname()));
                                    }
                                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                                }
                            });

                        } else {
                            Island.playerislands.put(result.getuuid(), result.getIslandname());
                            if (wm.getSpawnLocation(result.getIslandname()).getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
                                wm.getSpawnLocation(result.getIslandname()).getBlock().getRelative(BlockFace.DOWN).setType(Material.INFESTED_COBBLESTONE);
                            }
                            wm.loadWorld(result.getIslandname());
                            databaseReader.getlastLocation(result.getuuid(), new DatabaseReader.CallbackLocation() {
                                @Override
                                public void onQueryDone(Location loc) {
                                    if (loc != null) {
                                        player.teleport(loc);
                                    } else {
                                        player.teleport(wm.getSpawnLocation(result.getIslandname()));
                                    }
                                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                                }
                            });
                        }
                    } else {
                        player.teleport(wm.getSpawnLocation(plugin.getConfig().getString("SpawnWorld")));
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
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
