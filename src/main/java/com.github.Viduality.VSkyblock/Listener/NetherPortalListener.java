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
                        // player.teleport(plugin.getMV().getCore().getMVWorldManager().getMVWorld(plugin.getConfig().getString("NetherWorld")).getSpawnLocation());
                    }
                }
            });
        }
    }



    private Inventory getportals() {
        Inventory netherinv = Bukkit.createInventory(null, 9, ChatColor.AQUA + "Portals");
        ItemStack nether = new ItemStack(Material.NETHERRACK, 1);
        ItemMeta netherMeta = nether.getItemMeta();
        netherMeta.setDisplayName("Nether");
        List<String> netherlore = Arrays.asList("Teleportiert dich in", "deinen eigenen Nether");
        netherMeta.setLore(netherlore);
        nether.setItemMeta(netherMeta);
        netherinv.setItem(3, nether);

        ItemStack trade = new ItemStack(Material.DIAMOND,1);
        ItemMeta tradeMeta = trade.getItemMeta();
        tradeMeta.setDisplayName("Handelsposten");
        List<String> tradelore = Arrays.asList("Teleportiert dich in", "Handelsposten");
        tradeMeta.setLore(tradelore);
        trade.setItemMeta(tradeMeta);
        netherinv.setItem(6, trade);
        return netherinv;
    }
}
