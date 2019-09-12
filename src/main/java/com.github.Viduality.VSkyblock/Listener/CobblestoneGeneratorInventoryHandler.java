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

    private VSkyblock plugin = VSkyblock.getInstance();
    private CobblestoneGeneratorUpgrade cobblestoneGeneratorUpgrade = new CobblestoneGeneratorUpgrade();


    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getView().getTitle().equalsIgnoreCase(getInvName())) {
            inventoryClickEvent.setCancelled(true);
            if (inventoryClickEvent.getRawSlot() == 16) {
                ConfigShorts.loadOptionsConfig();
                if (inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(plugin.getConfig().getString("CobblestoneGenerator.Upgrade.DisplayName"))) {
                    ConfigShorts.loaddefConfig();
                    cobblestoneGeneratorUpgrade.checkForGeneratorUpgrade(Island.playerislands.get(inventoryClickEvent.getWhoClicked().getUniqueId().toString()), (Player) inventoryClickEvent.getWhoClicked());
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
        ConfigShorts.loadOptionsConfig();
        String displayname = plugin.getConfig().getString("CobblestoneGenerator.DisplayName");
        ConfigShorts.loaddefConfig();
        if (displayname != null) {
            return displayname;
        } else {
            return "Cobblestone-Generator";
        }
    }
}
