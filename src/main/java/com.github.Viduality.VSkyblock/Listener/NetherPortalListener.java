package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class NetherPortalListener implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private WorldManager wm = new WorldManager();

    @EventHandler
    public void onNetherPortalUse(PlayerPortalEvent playerPortalEvent) {
        Player player = playerPortalEvent.getPlayer();
        if (playerPortalEvent.getFrom().getBlock().getType().equals(Material.NETHER_PORTAL)) {
            databaseReader.getPlayerData(player.getUniqueId().toString(), new DatabaseReader.Callback() {
                @Override
                public void onQueryDone(DatabaseCache result) {
                    if (player.getWorld().getName().equals(result.getIslandname())) {
                        player.teleport(wm.getSpawnLocation("NetherWorld"));
                    }
                }
            });
        }
    }
}
