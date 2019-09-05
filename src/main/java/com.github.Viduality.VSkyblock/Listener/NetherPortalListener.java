package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;


public class NetherPortalListener implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private WorldManager wm = new WorldManager();

    @EventHandler
    public void onNetherPortalUse(PlayerPortalEvent playerPortalEvent) {
        Player player = playerPortalEvent.getPlayer();
        playerPortalEvent.setCancelled(true);
        System.out.println("YES");
        if (playerPortalEvent.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
            System.out.println("PORTAL");
            databaseReader.getPlayerData(player.getUniqueId().toString(), new DatabaseReader.Callback() {
                @Override
                public void onQueryDone(DatabaseCache result) {
                    if (player.getWorld().getName().equals(result.getIslandname())) {
                        player.teleport(wm.getSpawnLocation(plugin.getConfig().getString("NetherWorld")));
                    }
                }
            });
        }
    }
}
