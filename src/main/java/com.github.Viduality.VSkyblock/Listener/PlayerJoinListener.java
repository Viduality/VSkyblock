package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.*;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.github.Viduality.VSkyblock.WorldGenerator.WorldGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayDeque;
import java.util.Deque;

public class PlayerJoinListener implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private DatabaseWriter databaseWriter = new DatabaseWriter();
    private WorldManager wm = new WorldManager();

    private Deque<DatabaseCache> toLoad = new ArrayDeque<>();


    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        PotionEffect potionEffectBlindness = new PotionEffect(PotionEffectType.BLINDNESS, 600, 1);
        PotionEffect potionEffectNightVision = new PotionEffect(PotionEffectType.NIGHT_VISION, 600, 1);
        player.addPotionEffect(potionEffectBlindness);
        player.addPotionEffect(potionEffectNightVision);
        Location location = player.getLocation();
        location.setPitch(-90);
        player.teleport(location);


        if (wm.getUnloadedWorlds().contains(ConfigShorts.getDefConfig().getString("SpawnWorld"))) {
            wm.loadWorld(ConfigShorts.getDefConfig().getString("SpawnWorld"));
        }
        if (wm.getUnloadedWorlds().contains(ConfigShorts.getDefConfig().getString("NetherWorld"))) {
            wm.loadWorld(ConfigShorts.getDefConfig().getString("NetherWorld"));
        }


        databaseReader.getPlayerData(player.getUniqueId().toString(), new DatabaseReader.Callback() {
            @Override
            public void onQueryDone(DatabaseCache result) {
                if (result.getUuid() == null) {
                    databaseWriter.addPlayer(player.getUniqueId(), player.getName());
                } else {
                    if (!result.getName().equals(player.getName())) {
                        databaseWriter.updatePlayerName(player.getUniqueId(), player.getName());
                    }
                    if (plugin.scoreboardmanager.doesobjectiveexist("deaths")) {
                        if (plugin.scoreboardmanager.addPlayerToObjective(player, "deaths")) {
                            plugin.scoreboardmanager.updatePlayerScore(player.getName(), "deaths", result.getDeathCount());
                        }
                    }
                    if (result.getIslandname() != null) {
                        BukkitTask task = Island.emptyloadedislands.remove(result.getIslandname());
                        if (task != null) {
                            task.cancel();
                        }
                        toLoad.add(result);
                        if (toLoad.size() == 1) {
                            loadWorld(result);
                        }
                    } else {
                        player.teleport(wm.getSpawnLocation(ConfigShorts.getDefConfig().getString("SpawnWorld")));
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                        if (result.isKicked()) {
                            ConfigShorts.messagefromString("KickedFromIslandOffline", player);
                            player.setTotalExperience(0);
                            player.setExp(0);
                            player.getEnderChest().clear();
                            player.getInventory().clear();
                            databaseWriter.removeKicked(result.getUuid());
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

    private void loadWorld(DatabaseCache result) {
        if (!Island.playerislands.containsValue(result.getIslandname())) {
            databaseReader.addToCobbleStoneGenerators(result.getIslandname());
        }
        Island.playerislands.put(result.getUuid(), result.getIslandname());
        wm.loadWorld(result.getIslandname());
        if (wm.getSpawnLocation(result.getIslandname()).getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
            wm.getSpawnLocation(result.getIslandname()).getBlock().getRelative(BlockFace.DOWN).setType(Material.INFESTED_COBBLESTONE);
        }
        databaseReader.getlastLocation(result.getUuid(), loc -> {
            Player player = result.getPlayer();
            if (player != null) {
                if (loc != null) {
                    player.teleport(loc);
                } else {
                    player.teleport(wm.getSpawnLocation(result.getIslandname()));
                }
                player.removePotionEffect(PotionEffectType.BLINDNESS);
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            } else {
                Island.emptyloadedislands.put(result.getIslandname(), plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    wm.unloadWorld(result.getIslandname());
                }, 20 * 60));
            }
            toLoad.remove(result);
            DatabaseCache nextResult = toLoad.peekFirst();
            if (nextResult != null) {
                loadWorld(nextResult);
            }
        });
    }
}
