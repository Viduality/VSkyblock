package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.CobblestoneGeneratorUpgrade;
import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class CobblestoneGeneratorInventoryHandler implements Listener {

    private final VSkyblock plugin;
    private final CobblestoneGeneratorUpgrade cobblestoneGeneratorUpgrade;

    public CobblestoneGeneratorInventoryHandler(VSkyblock plugin) {
        this.plugin = plugin;
        cobblestoneGeneratorUpgrade = new CobblestoneGeneratorUpgrade();
    }


    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getView().getTitle().equalsIgnoreCase(getInvName())) {
            inventoryClickEvent.setCancelled(true);
            if (inventoryClickEvent.getRawSlot() == 16) {
                if (inventoryClickEvent.getCurrentItem() != null && inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Upgrade.DisplayName"))) {
                    cobblestoneGeneratorUpgrade.checkForGeneratorUpgrade(Island.playerislands.get(inventoryClickEvent.getWhoClicked().getUniqueId()), (Player) inventoryClickEvent.getWhoClicked());
                }
            }
        }
    }

    @EventHandler
    public void inventoryDragEvent(InventoryDragEvent inventoryDragEvent) {
        if (inventoryDragEvent.getView().getTitle().equalsIgnoreCase(getInvName())) {
            inventoryDragEvent.setCancelled(true);
        }
    }

    /**
     * Returns the cobblestone generators inventory name from the config.
     *
     * @return String
     */
    private String getInvName() {
        String displayname = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.DisplayName");
        if (displayname != null) {
            return displayname;
        } else {
            return "Cobblestone-Generator";
        }
    }
}
