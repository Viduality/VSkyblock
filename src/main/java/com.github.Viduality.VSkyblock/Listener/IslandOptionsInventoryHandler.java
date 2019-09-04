package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class IslandOptionsInventoryHandler implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseWriter databaseWriter = new DatabaseWriter();
    private DatabaseReader databaseReader = new DatabaseReader();


    @EventHandler
    public void isOptionsHandler(InventoryClickEvent inventoryClickEvent) {
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        if (inventoryClickEvent.getView().getTitle().equalsIgnoreCase(getInvName())) {
            inventoryClickEvent.setCancelled(true);
            int slot = inventoryClickEvent.getRawSlot();
            int visitslot = 3;
            int difficultyslot = 5;
            int confirmslot = 17;

            ConfigShorts.loadOptionsConfig();
            String visitalloweditem = plugin.getConfig().getString("Visit.AllowedItem");
            String difficultynormalitem = plugin.getConfig().getString("Difficulty.NormalItem");
            String difficultyharditem = plugin.getConfig().getString("Difficulty.HardItem");
            String difficultyeasyitem = plugin.getConfig().getString("Difficulty.EasyItem");

            if (slot == visitslot) {
                if (inventoryClickEvent.getCurrentItem().getType().equals(getMaterial(visitalloweditem))) {
                    inventoryClickEvent.getInventory().setItem(visitslot, getItemStack("visit", "NotAllowed"));
                } else {
                    inventoryClickEvent.getInventory().setItem(visitslot, getItemStack("visit", "Allowed"));
                }
            } else if (slot == difficultyslot) {
                if (inventoryClickEvent.getCurrentItem().getType().equals(getMaterial(difficultynormalitem))) {
                    inventoryClickEvent.getInventory().setItem(difficultyslot, getItemStack("difficulty", "Hard"));
                } else if (inventoryClickEvent.getCurrentItem().getType().equals(getMaterial(difficultyharditem))) {
                    inventoryClickEvent.getInventory().setItem(difficultyslot, getItemStack("difficulty", "Easy"));
                } else if (inventoryClickEvent.getCurrentItem().getType().equals(getMaterial(difficultyeasyitem))) {
                    inventoryClickEvent.getInventory().setItem(difficultyslot, getItemStack("difficulty", "Normal"));
                }


            } else if (slot == confirmslot && inventoryClickEvent.getCurrentItem().getType().equals(Material.LIME_WOOL)) {
                boolean visit = true;
                String difficulty = "NORMAL";
                if (!inventoryClickEvent.getInventory().getItem(visitslot).getType().equals(getMaterial(visitalloweditem))) {
                    visit = false;
                }
                if (inventoryClickEvent.getInventory().getItem(difficultyslot).getType().equals(getMaterial(difficultyharditem))) {
                    difficulty = "HARD";
                } else if (inventoryClickEvent.getInventory().getItem(difficultyslot).getType().equals(getMaterial(difficultyeasyitem))) {
                    difficulty = "EASY";
                }
                player.closeInventory();
                ConfigShorts.messagefromString("UpdatedIslandOptions", player);
                String finalDifficulty = difficulty;
                ConfigShorts.loaddefConfig();
                databaseWriter.updateIslandOptions(player, visit, difficulty, done ->
                        databaseReader.getislandidfromplayer(player.getUniqueId().toString(), result ->
                                updateIsland(result, finalDifficulty)));
            }
        }
    }

    @EventHandler
    public void isOptionsHandler2(InventoryDragEvent inventoryDragEvent) {
        if (inventoryDragEvent.getView().getTitle().equalsIgnoreCase(getInvName())) {
            inventoryDragEvent.setCancelled(true);
        }
    }

    /**
     * Creates an ItemStack for the island options.
     *
     * @param item
     * @param option
     * @return ItemStack
     */
    private ItemStack getItemStack(String item, String option) {
        ConfigShorts.loadOptionsConfig();
        Material mat;
        switch (item) {
            case "visit":
                String visititem = plugin.getConfig().getString("Visit." + option + "Item");
                mat = getMaterial(visititem);
                break;
            case "difficulty":
                String difficultyitem = plugin.getConfig().getString("Difficulty." + option + "Item");
                mat = getMaterial(difficultyitem);
                break;
            default:
                mat = Material.BARRIER;
                break;
        }
        ItemStack itemStack = new ItemStack(mat, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        switch (item) {
            case "visit":
                itemMeta.setDisplayName(getDisplayNameVisit(option));
                break;
            case "difficulty":
                itemMeta.setDisplayName(getDisplayNameDifficulty(option));
                break;
            default:
                break;
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.values());
        itemStack.setItemMeta(itemMeta);
        ConfigShorts.loaddefConfig();
        return itemStack;
    }

    /**
     * Updates the islands difficulty.
     *
     * @param islandid
     * @param difficulty
     */
    private void updateIsland(int islandid, String difficulty) {
        String islandName = "VSkyblockIsland_" + islandid;
        plugin.getServer().getWorld(islandName).setDifficulty(Difficulty.valueOf(difficulty.toUpperCase()));
    }

    /**
     * Checks given material and returns it if existing.
     *
     * @param material
     * @return Material
     */
    private Material getMaterial(String material) {
        if (Material.getMaterial(material) != null) {
            return Material.getMaterial(material);
        } else {
            return Material.BARRIER;
        }
    }

    /**
     * Returns the options inventorys name.
     *
     * @return String
     */
    private String getInvName() {
        ConfigShorts.loadOptionsConfig();
        if (plugin.getConfig().getString("InventoryName") != null) {
            String invname = plugin.getConfig().getString("InventoryName");
            ConfigShorts.loaddefConfig();
            return invname;
        } else {
            ConfigShorts.loaddefConfig();
            return "Island options";
        }
    }

    /**
     * Checks given string and returns it if difficulty enum exists.
     *
     * @param difficulty
     * @return String
     */
    private String getDisplayNameDifficulty(String difficulty) {
        ConfigShorts.loadOptionsConfig();
        String displayname = plugin.getConfig().getString("Difficulty." + difficulty);
        ConfigShorts.loaddefConfig();
        if (displayname != null) {
            return displayname;
        } else {
            return difficulty;
        }
    }

    /**
     * Checks given string and returns it if not null.
     *
     * @param allowed
     * @return String
     */
    private String getDisplayNameVisit(String allowed) {
        ConfigShorts.loadOptionsConfig();
        String displayname = plugin.getConfig().getString("Visit." + allowed);
        ConfigShorts.loaddefConfig();
        if (displayname != null) {
            return displayname;
        } else {
            return allowed;
        }
    }
}
