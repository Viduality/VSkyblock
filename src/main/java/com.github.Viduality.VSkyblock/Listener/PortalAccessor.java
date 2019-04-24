package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PortalAccessor implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();

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
}
