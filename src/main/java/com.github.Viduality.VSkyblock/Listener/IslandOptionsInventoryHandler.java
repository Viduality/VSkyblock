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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class IslandOptionsInventoryHandler implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseWriter databaseWriter = new DatabaseWriter();
    private DatabaseReader databaseReader = new DatabaseReader();


    @EventHandler
    public void isOptionsHandler(InventoryClickEvent inventoryClickEvent) {
        ConfigShorts.loadOptionsConfig();
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        if (inventoryClickEvent.getView().getTitle().equalsIgnoreCase(getInvName())) {
            inventoryClickEvent.setCancelled(true);
            int slot = inventoryClickEvent.getRawSlot();
            int visitslot = 3;
            int difficultyslot = 5;
            int confirmslot = 17;
            if (slot == visitslot) {
                if (inventoryClickEvent.getCurrentItem().getType().equals(getMaterial(plugin.getConfig().getString("Visit.AllowedItem")))) {
                    inventoryClickEvent.getInventory().setItem(visitslot, getItemStack("visit", "NotAllowed"));
                } else {
                    inventoryClickEvent.getInventory().setItem(visitslot, getItemStack("visit", "Allowed"));
                }
            } else if (slot == difficultyslot) {
                if (inventoryClickEvent.getCurrentItem().getType().equals(getMaterial(plugin.getConfig().getString("Difficulty.NormalItem")))) {
                    inventoryClickEvent.getInventory().setItem(difficultyslot, getItemStack("difficulty", "Hard"));
                } else if (inventoryClickEvent.getCurrentItem().getType().equals(getMaterial(plugin.getConfig().getString("Difficulty.HardItem")))) {
                    inventoryClickEvent.getInventory().setItem(difficultyslot, getItemStack("difficulty", "Easy"));
                } else if (inventoryClickEvent.getCurrentItem().getType().equals(getMaterial(plugin.getConfig().getString("Difficulty.EasyItem")))) {
                    inventoryClickEvent.getInventory().setItem(difficultyslot, getItemStack("difficulty", "Normal"));
                }
            } else if (slot == confirmslot) {
                boolean visit = true;
                String difficulty = "NORMAL";
                if (!inventoryClickEvent.getInventory().getItem(visitslot).getType().equals(getMaterial(plugin.getConfig().getString("Visit.AllowedItem")))) {
                    visit = false;
                }
                if (inventoryClickEvent.getInventory().getItem(difficultyslot).getType().equals(getMaterial(plugin.getConfig().getString("Difficulty.HardItem")))) {
                    difficulty = "HARD";
                } else if (inventoryClickEvent.getInventory().getItem(difficultyslot).getType().equals(getMaterial(plugin.getConfig().getString("Difficulty.EasyItem")))) {
                    difficulty = "EASY";
                }
                player.closeInventory();
                ConfigShorts.loaddefConfig();
                ConfigShorts.messagefromString("UpdatedIslandOptions", player);
                String finalDifficulty = difficulty;
                databaseWriter.updateIslandOptions(player, visit, difficulty, done ->
                        databaseReader.getislandidfromplayer(player.getUniqueId().toString(), result ->
                                updateIsland(result, finalDifficulty)));
            }
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
        Material mat;
        switch (item) {
            case "visit":
                mat = getMaterial(plugin.getConfig().getString("Visit." + option + "Item"));
                break;
            case "difficulty":
                mat = getMaterial(plugin.getConfig().getString("Difficulty." + option + "Item"));
                break;
            default:
                mat = Material.BARRIER;
                break;
        }
        ItemStack itemStack = new ItemStack(mat, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            switch (item) {
                case "visit":
                    itemMeta.setDisplayName(getDisplayNameVisit(option));
                case "difficulty":
                    itemMeta.setDisplayName(getDisplayNameDifficulty(option));
            }
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addItemFlags(ItemFlag.values());
            itemStack.setItemMeta(itemMeta);
        }
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
        if (Material.matchMaterial(material) != null) {
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
        if (plugin.getConfig().getString("InventoryName") != null) {
            return plugin.getConfig().getString("InventoryName");
        } else {
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
        String displayname = plugin.getConfig().getString("Difficulty." + difficulty);
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
        String displayname = plugin.getConfig().getString("Visit." + allowed);
        if (displayname != null) {
            return displayname;
        } else {
            return "Visitors allowed";
        }
    }
}
