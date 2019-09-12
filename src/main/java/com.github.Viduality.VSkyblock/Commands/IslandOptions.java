package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.IslandOptionsCache;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class IslandOptions implements SubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();



    @Override
    public void execute(DatabaseCache databaseCache) {
        if (databaseCache.getIslandId() != 0) {
            databaseReader.getIslandOptions(databaseCache.getIslandId(), isoptionsCache ->
                    createOptionsInventory(isoptionsCache, databaseCache.getPlayer(), databaseCache.isIslandowner()));
        } else {
            ConfigShorts.messagefromString("NoIsland", databaseCache.getPlayer());
        }
    }

    /**
     * Creates the options inventory and opens it for the player.
     * @param cache
     * @param player
     * @param islandOwner
     */
    private void createOptionsInventory(IslandOptionsCache cache, Player player, boolean islandOwner) {
        String difficulty = cache.getDifficulty();
        boolean visit = cache.getVisit();
        ConfigShorts.loadOptionsConfig();
        Inventory isOptions = Bukkit.createInventory(null, 18, getInvName());

        //VISIT

        Material visitBlock;
        if (visit) {
            visitBlock = getMaterial(plugin.getConfig().getString("Visit.AllowedItem"));
        } else {
            visitBlock = getMaterial(plugin.getConfig().getString("Visit.NotAllowedItem"));
        }
        ItemStack visitSlot = new ItemStack(visitBlock, 1);
        ItemMeta visitSlotItemMeta = visitSlot.getItemMeta();
        if (visitSlotItemMeta != null) {
            if (visit) {
                visitSlotItemMeta.setDisplayName("§2" + getDisplayNameVisit("Allowed"));
            } else {
                visitSlotItemMeta.setDisplayName("§c" + getDisplayNameVisit("NotAllowed"));
            }
            visitSlotItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            visitSlotItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            visitSlotItemMeta.addItemFlags(ItemFlag.values());
        }

        visitSlot.setItemMeta(visitSlotItemMeta);
        isOptions.setItem(2, visitSlot);

        //DIFFICULTY

        Material difficultyBlock;
        switch (difficulty) {
            case "EASY":
                difficultyBlock = getMaterial(plugin.getConfig().getString("Difficulty.EasyItem"));
                break;
            case "HARD":
                difficultyBlock = getMaterial(plugin.getConfig().getString("Difficulty.HardItem"));
                break;
            default:
                difficultyBlock = getMaterial(plugin.getConfig().getString("Difficulty.NormalItem"));
                break;
        }
        ItemStack difficultySlot = new ItemStack(difficultyBlock, 1);
        ItemMeta difficultySlotItemMeta = difficultySlot.getItemMeta();
        if (difficultySlotItemMeta != null) {
            switch (difficulty) {
                case "EASY":
                    difficultySlotItemMeta.setDisplayName(getDisplayNameDifficulty("Easy"));
                    break;
                case "HARD":
                    difficultySlotItemMeta.setDisplayName(getDisplayNameDifficulty("Hard"));
                    break;
                default:
                    difficultySlotItemMeta.setDisplayName(getDisplayNameDifficulty("Normal"));
                    break;
            }
            difficultySlotItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            difficultySlotItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            difficultySlotItemMeta.addItemFlags(ItemFlag.values());
        }
        difficultySlot.setItemMeta(difficultySlotItemMeta);
        isOptions.setItem(6, difficultySlot);

        //COBBLESTONEGENERATOR

        ItemStack generatorSlot = new ItemStack(Material.COBBLESTONE, 1);
        ItemMeta generatorSlotItemMeta = difficultySlot.getItemMeta();
        if (generatorSlotItemMeta != null) {

            generatorSlotItemMeta.setDisplayName(getDisplayNameGenerator());

            generatorSlotItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            generatorSlotItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            generatorSlotItemMeta.addItemFlags(ItemFlag.values());
        }
        generatorSlot.setItemMeta(generatorSlotItemMeta);
        isOptions.setItem(4, generatorSlot);

        //CONFIRM BUTTON

        if (islandOwner) {
            Material acceptBlock = Material.LIME_WOOL;
            ItemStack acceptBlockSlot = new ItemStack(acceptBlock, 1);
            ItemMeta acceptBlockSlotItemMeta = acceptBlockSlot.getItemMeta();
            if (acceptBlockSlotItemMeta != null) {
                acceptBlockSlotItemMeta.setDisplayName(getAccept());
                acceptBlockSlotItemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                acceptBlockSlotItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                acceptBlockSlotItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                acceptBlockSlotItemMeta.addItemFlags(ItemFlag.values());
            }
            acceptBlockSlot.setItemMeta(acceptBlockSlotItemMeta);
            isOptions.setItem(17, acceptBlockSlot);
        }
        player.openInventory(isOptions);
        ConfigShorts.loaddefConfig();
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

    /**
     * Checks given string and returns it if not null.
     *
     * @return String
     */
    private String getDisplayNameGenerator() {
        String displayname = plugin.getConfig().getString("CobblestoneGenerator.DisplayName");
        if (displayname != null) {
            return displayname;
        } else {
            return "Cobblestone-Generator";
        }
    }

    /**
     * Returns the accept string for the island options.
     *
     * @return String
     */
    private String getAccept() {
        String accept = plugin.getConfig().getString("Accept");
        if (accept != null) {
            return accept;
        } else {
            return "Accept";
        }
    }
}
