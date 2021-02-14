package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.event.Listener;

public class PortalAccessor implements Listener {

    private final VSkyblock plugin;

    public PortalAccessor(VSkyblock plugin) {
        this.plugin = plugin;
    }

    // TODO implement portals
    // NOT IN USE YET
    /*
    @EventHandler
    public void portalAccessor(PlayerMoveEvent playerMoveEvent) {
        Player player = playerMoveEvent.getPlayer();
        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.END_PORTAL_FRAME)) {
            Inventory portals = Bukkit.createInventory(null, 9, ChatColor.DARK_PURPLE + "Teleporter");
            ConfigShorts.loadTeleporterConfig();
            String waterworldname = ChatColor.BLUE + plugin.getConfig().getString("WaterTemple.Name");
            ItemStack waterworld = new ItemStack(Material.PRISMARINE_SHARD, 1);
            ItemMeta waterworldmeta = waterworld.getItemMeta();
            waterworldmeta.setDisplayName(waterworldname);
            waterworldmeta.addEnchant(Enchantment.DURABILITY, 1, false);
            waterworldmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            waterworld.setItemMeta(waterworldmeta);

            portals.setItem(0, waterworld);
            player.openInventory(portals);
        }
    }
     */

}
