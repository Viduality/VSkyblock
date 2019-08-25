package com.github.Viduality.VSkyblock.Listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class TeleporterInventoryHandler implements Listener {

    // TODO implement portals
    // NOT IN USE YET
    @EventHandler
    public void onInventoryKlick(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getView().getTitle().equals(ChatColor.DARK_PURPLE + "Teleporter")) {
            inventoryClickEvent.setCancelled(true);
            if (inventoryClickEvent.getSlot() == 0) {
                // Implement World
            }
        }
    }
}
