package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Utilitys.IslandCacheHandler;
import com.github.Viduality.VSkyblock.Utilitys.*;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class NetherPortalListener implements Listener {

    private final VSkyblock plugin;

    public static Cache<UUID, Location> setNetherHome = CacheBuilder.newBuilder()
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build();

    public static Cache<UUID, Location> teleportToNetherHome = CacheBuilder.newBuilder()
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build();

    public NetherPortalListener(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNetherPortalUse(PlayerPortalEvent playerPortalEvent) {
        Player player = playerPortalEvent.getPlayer();
        playerPortalEvent.setCancelled(true);
        final Location location = playerPortalEvent.getFrom();
        if (playerPortalEvent.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
            plugin.getDb().getReader().getPlayerData(player.getUniqueId().toString(), (result) -> {
                if (player.getWorld().getName().equals(result.getIslandName())) {
                    player.teleportAsync(plugin.getWorldManager().getSpawnLocation(ConfigShorts.getDefConfig().getString("NetherWorld"))).whenComplete((b, e) -> {
                        ConfigShorts.messagefromString("NetherJoin1", player);
                        ConfigShorts.messagefromString("NetherJoin2", player);
                        plugin.getDb().getReader().getNetherHome(result.getUuid(), (netherhome) -> {
                            if (netherhome != null) {
                                teleportToNetherHome.put(result.getUuid(), netherhome);
                                ConfigShorts.messagefromString("TeleportToNetherHome", player);
                            }
                        });
                        if (e != null) {
                            e.printStackTrace();
                        }
                    });

                } else if (player.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                    if (result.getIslandName() != null) {
                        player.teleportAsync(IslandCacheHandler.islandhomes.get(result.getIslandName()));
                        plugin.getDb().getReader().getNetherHome(result.getUuid(), (netherhome) -> {
                            if (netherhome != null) {
                                if (result.isIslandOwner()) {
                                    if (netherhome.distance(location) > 10) {
                                        setNetherHome.put(result.getUuid(), location);
                                        ConfigShorts.messagefromString("SetNewNetherHome", player);
                                    }
                                }
                            } else {
                                plugin.getDb().getWriter().saveNetherHome(result.getIslandId(), location);
                            }
                        });
                    } else {
                        player.teleportAsync(plugin.getWorldManager().getSpawnLocation(ConfigShorts.getDefConfig().getString("SpawnWorld")));
                    }
                }
            });
        }
    }
}
