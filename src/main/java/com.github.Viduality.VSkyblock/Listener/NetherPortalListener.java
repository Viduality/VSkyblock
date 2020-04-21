package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Utilitys.*;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class NetherPortalListener implements Listener {

    private final VSkyblock plugin = VSkyblock.getInstance();
    private final DatabaseReader databaseReader = new DatabaseReader();
    private final DatabaseWriter databaseWriter = new DatabaseWriter();
    private final WorldManager wm = new WorldManager();

    public static Cache<UUID, Location> setNetherHome = CacheBuilder.newBuilder()
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build();

    public static Cache<UUID, Location> teleportToNetherHome = CacheBuilder.newBuilder()
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .build();

    @EventHandler
    public void onNetherPortalUse(PlayerPortalEvent playerPortalEvent) {
        Player player = playerPortalEvent.getPlayer();
        playerPortalEvent.setCancelled(true);
        final Location location = playerPortalEvent.getFrom();
        if (playerPortalEvent.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
            databaseReader.getPlayerData(player.getUniqueId().toString(), (result) -> {
                if (player.getWorld().getName().equals(result.getIslandname())) {
                    player.teleport(wm.getSpawnLocation(ConfigShorts.getDefConfig().getString("NetherWorld")));
                    ConfigShorts.messagefromString("NetherJoin1", player);
                    ConfigShorts.messagefromString("NetherJoin2", player);
                    databaseWriter.addIslandintoLocationsTable(result.getIslandId());
                    databaseReader.getNetherHome(result.getUuid(), (netherhome) -> {
                        if (netherhome != null) {
                            teleportToNetherHome.put(result.getUuid(), netherhome);
                            ConfigShorts.messagefromString("TeleportToNetherHome", player);
                        }
                    });
                } else if (player.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                    if (result.getIslandname() != null) {
                        player.teleport(wm.getSpawnLocation(result.getIslandname()));
                        databaseReader.getNetherHome(result.getUuid(), (netherhome) -> {
                            if (netherhome != null) {
                                if (result.isIslandowner()) {
                                    if (netherhome.distance(location) > 10) {
                                        setNetherHome.put(result.getUuid(), location);
                                        ConfigShorts.messagefromString("SetNewNetherHome", player);
                                    }
                                }
                            } else {
                                databaseWriter.saveNetherHome(result.getIslandId(), location);
                            }
                        });
                    } else {
                        player.teleport(wm.getSpawnLocation(ConfigShorts.getDefConfig().getString("SpawnWorld")));
                    }
                }
            });
        }
    }
}
