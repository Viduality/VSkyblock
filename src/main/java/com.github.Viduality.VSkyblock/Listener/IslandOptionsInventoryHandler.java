package com.github.Viduality.VSkyblock.Listener;

/*
 * VSkyblock
 * Copyright (C) 2020  Viduality
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class IslandOptionsInventoryHandler implements Listener {

    private final VSkyblock plugin;

    public IslandOptionsInventoryHandler(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void isOptionsHandler(InventoryClickEvent inventoryClickEvent) {
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        if (inventoryClickEvent.getView().getTitle().equalsIgnoreCase(getInvName())) {
            inventoryClickEvent.setCancelled(true);
            int slot = inventoryClickEvent.getRawSlot();
            int visitslot = 2;
            int generatorslot = 4;
            int difficultyslot = 6;
            int confirmslot = 17;

            String visitalloweditem = ConfigShorts.getOptionsConfig().getString("Visit.AllowedItem");
            String visitnotalloweditem = ConfigShorts.getOptionsConfig().getString("Visit.NotAllowedItem");
            String visitneedrequestitem = ConfigShorts.getOptionsConfig().getString("Visit.NeedsRequestItem");
            String difficultynormalitem = ConfigShorts.getOptionsConfig().getString("Difficulty.NormalItem");
            String difficultyharditem = ConfigShorts.getOptionsConfig().getString("Difficulty.HardItem");
            String difficultyeasyitem = ConfigShorts.getOptionsConfig().getString("Difficulty.EasyItem");

            if (slot == visitslot) {
                if (inventoryClickEvent.getCurrentItem().getType().equals(getMaterial(visitnotalloweditem))) {
                    inventoryClickEvent.getInventory().setItem(visitslot, getItemStack("visit", "Allowed"));
                } else if (inventoryClickEvent.getCurrentItem().getType().equals(getMaterial(visitalloweditem))) {
                    inventoryClickEvent.getInventory().setItem(visitslot, getItemStack("visit", "NeedsRequest"));
                } else {
                    inventoryClickEvent.getInventory().setItem(visitslot, getItemStack("visit", "NotAllowed"));
                }
            } else if (slot == difficultyslot) {
                if (inventoryClickEvent.getCurrentItem().getType().equals(getMaterial(difficultynormalitem))) {
                    inventoryClickEvent.getInventory().setItem(difficultyslot, getItemStack("difficulty", "Hard"));
                } else if (inventoryClickEvent.getCurrentItem().getType().equals(getMaterial(difficultyharditem))) {
                    inventoryClickEvent.getInventory().setItem(difficultyslot, getItemStack("difficulty", "Easy"));
                } else if (inventoryClickEvent.getCurrentItem().getType().equals(getMaterial(difficultyeasyitem))) {
                    inventoryClickEvent.getInventory().setItem(difficultyslot, getItemStack("difficulty", "Normal"));
                }
            } else if (slot == generatorslot) {
                if (inventoryClickEvent.getCurrentItem().getType().equals(Material.COBBLESTONE)) {
                    inventoryClickEvent.getWhoClicked().closeInventory();
                    inventoryClickEvent.getWhoClicked().openInventory(getGeneratorMenu(inventoryClickEvent.getWhoClicked().getUniqueId()));
                }



            } else if (slot == confirmslot && inventoryClickEvent.getCurrentItem().getType().equals(Material.LIME_WOOL)) {
                boolean visit = true;
                boolean needRequest = false;
                String difficulty = "NORMAL";
                if (inventoryClickEvent.getInventory().getItem(visitslot).getType().equals(getMaterial(visitnotalloweditem))) {
                    visit = false;
                } else if (inventoryClickEvent.getInventory().getItem(visitslot).getType().equals(getMaterial(visitneedrequestitem))) {
                    needRequest = true;
                }
                if (inventoryClickEvent.getInventory().getItem(difficultyslot).getType().equals(getMaterial(difficultyharditem))) {
                    difficulty = "HARD";
                } else if (inventoryClickEvent.getInventory().getItem(difficultyslot).getType().equals(getMaterial(difficultyeasyitem))) {
                    difficulty = "EASY";
                }
                player.closeInventory();
                ConfigShorts.messagefromString("UpdatedIslandOptions", player);
                String finalDifficulty = difficulty;
                plugin.getDb().getWriter().updateIslandOptions(player, visit, needRequest, difficulty, done ->
                        plugin.getDb().getReader().getIslandNameFromPlayer(player.getUniqueId(), result ->
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
        Material mat;
        switch (item) {
            case "visit":
                String visititem = ConfigShorts.getOptionsConfig().getString("Visit." + option + "Item");
                mat = getMaterial(visititem);
                break;
            case "difficulty":
                String difficultyitem = ConfigShorts.getOptionsConfig().getString("Difficulty." + option + "Item");
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
        return itemStack;
    }

    /**
     * Updates the islands difficulty.
     *
     * @param islandname
     * @param difficulty
     */
    private void updateIsland(String islandname, String difficulty) {
        if (plugin.getServer().getWorld(islandname) != null) {
            plugin.getServer().getWorld(islandname).setDifficulty(Difficulty.valueOf(difficulty.toUpperCase()));
        }
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
        if (ConfigShorts.getOptionsConfig().getString("InventoryName") != null) {
            String invname = ConfigShorts.getOptionsConfig().getString("InventoryName");
            return invname;
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
        String displayname = ConfigShorts.getOptionsConfig().getString("Difficulty." + difficulty);
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
        String displayname = ConfigShorts.getOptionsConfig().getString("Visit." + allowed);
        if (displayname != null) {
            return displayname;
        } else {
            return allowed;
        }
    }


    /**
     * Creates the menu for the cobblestone generator.
     *
     * @return Inventory
     */
    private Inventory getGeneratorMenu(UUID playerUUID) {
        Inventory genInv = Bukkit.createInventory(null, 27, getDisplayNameGenerator());

        int slotCobbleDropChance = 10;
        int slotChancesOverview = 13;
        int slotUpgradeButton = 16;

        //Set Frame

        ItemStack frames = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta framesMeta = frames.getItemMeta();
        framesMeta.setDisplayName(" ");
        frames.setItemMeta(framesMeta);
        for (int i = 0; i < 27; i++) {
            if (i != slotCobbleDropChance) {
                if (i != slotChancesOverview) {
                    if (i != slotUpgradeButton) {
                        genInv.setItem(i, frames);
                    }
                }
            }
        }

        //Upgrade Button

        ItemStack upgradeButton = getUpgradeButton(playerUUID);
        genInv.setItem(slotUpgradeButton, upgradeButton);

        //Cobble Drop Chance

        ItemStack cobbleDropChance = getCobbleDropChanceInfo(playerUUID);
        genInv.setItem(slotCobbleDropChance, cobbleDropChance);

        //Chances Overview

        ItemStack chancesOverview = getChancesOverview(playerUUID);
        genInv.setItem(slotChancesOverview, chancesOverview);

        return genInv;
    }

    private ItemStack getUpgradeButton(UUID playerUUID) {
        String island = Island.playerislands.get(playerUUID);
        Integer currentGeneratorLevel = CobblestoneGenerator.islandGenLevel.get(island);
        Integer nextGeneratorLevel = currentGeneratorLevel + 1;
        String upgradeButtonItem;
        boolean canUpgrade = nextGeneratorLevel <= 8;
        if (!canUpgrade) {
            upgradeButtonItem = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Upgrade.Max_Level.Item");
        } else {
            upgradeButtonItem = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Upgrade.Level_" + nextGeneratorLevel + ".Item");
        }
        Material mat = getMaterial(upgradeButtonItem);
        ItemStack upgradeButton = new ItemStack(mat, 1);
        ItemMeta upgradeButtonMeta = upgradeButton.getItemMeta();

        upgradeButtonMeta.setDisplayName(getDisplayNameGeneratorUpgradeButton(nextGeneratorLevel));
        upgradeButtonMeta.setLore(getDescriptionGeneratorUpgradeButton(nextGeneratorLevel));

        if (canUpgrade) {
            upgradeButtonMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        }
        upgradeButtonMeta.addItemFlags(ItemFlag.values());
        upgradeButton.setItemMeta(upgradeButtonMeta);
        return upgradeButton;
    }

    private ItemStack getCobbleDropChanceInfo(UUID playerUUID) {
        String island = Island.playerislands.get(playerUUID);
        Integer currentIslandLevel = CobblestoneGenerator.islandlevels.get(island);
        int levelIntervall = CobblestoneGenerator.cobblestoneLevelInterval;
        int maxDrops = currentIslandLevel / levelIntervall + 1;


        Material mat = Material.COBBLESTONE;
        ItemStack cobbleDropChanceInfo = new ItemStack(mat, 1);
        ItemMeta cobbleDropChanceInfoMeta = cobbleDropChanceInfo.getItemMeta();

        cobbleDropChanceInfoMeta.setDisplayName(getDisplayNameCobbleDropChance());
        cobbleDropChanceInfoMeta.setLore(getDescriptionCobblestoneDropChance(maxDrops));

        cobbleDropChanceInfoMeta.addItemFlags(ItemFlag.values());
        cobbleDropChanceInfo.setItemMeta(cobbleDropChanceInfoMeta);
        return cobbleDropChanceInfo;
    }

    private ItemStack getChancesOverview(UUID playerUUID) {
        String island = Island.playerislands.get(playerUUID);
        Integer currentgeneratorLevel = CobblestoneGenerator.islandGenLevel.get(island);

        String chancesOverviewItem = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Chances.Level_" + currentgeneratorLevel + "_Item");
        Material mat = getMaterial(chancesOverviewItem);

        ItemStack chancesOverview = new ItemStack(mat, 1);
        ItemMeta chancesOverviewMeta = chancesOverview.getItemMeta();

        chancesOverviewMeta.setDisplayName(getDisplayNameChancesOverview());
        chancesOverviewMeta.setLore(getDescriptionChancesOverview(currentgeneratorLevel));

        chancesOverviewMeta.addItemFlags(ItemFlag.values());
        chancesOverview.setItemMeta(chancesOverviewMeta);
        return chancesOverview;
    }

    /**
     * Checks given string and returns it if not null.
     *
     * @return String
     */
    private String getDisplayNameGenerator() {
        String displayname = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.DisplayName");
        if (displayname != null) {
            return displayname;
        } else {
            return "Cobblestone-Generator";
        }
    }

    /**
     * Checks given string and returns it if not null.
     *
     * @return String
     */
    private String getDisplayNameGeneratorUpgradeButton(Integer nextGeneratorLevel) {
        String displayname;
        if (nextGeneratorLevel > 8) {
            displayname = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Upgrade.DisplayNameMaxLevel");
        } else {
            displayname = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Upgrade.DisplayName");
        }
        if (displayname != null) {
            return displayname;
        } else {
            return "Upgrade";
        }
    }

    private String getDisplayNameCobbleDropChance() {
        String displayname = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.CobbleDropChance");
        if (displayname != null) {
            return displayname;
        } else {
            return "Cobblestone Drops";
        }
    }

    private String getDisplayNameChancesOverview() {
        String displayname = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Chances.Overview");
        if (displayname != null) {
            return displayname;
        } else {
            return "Cobblestone-Generator Drops";
        }
    }

    private List<String> getDescriptionGeneratorUpgradeButton(Integer nextGeneratorLevel) {
        List<String> descriptionList = new ArrayList<>();
        if (nextGeneratorLevel > 8) {
            String description = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Upgrade.Max_Level.Description");
            descriptionList.addAll(splitString(description));
        } else {
            String overlay = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Upgrade.Adds");
            String description = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Upgrade.Level_" + nextGeneratorLevel + ".AddsFeature");
            if (overlay != null) {
                descriptionList.addAll(splitString(overlay));
            }
            if (description != null) {
                descriptionList.addAll(splitString(description));
            }

            String neededItemsColorCode = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Upgrade.NeededColor");

            String neededLevel = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Upgrade.NeededLevel");
            String neededLevelNumber = neededItemsColorCode + CobblestoneGenerator.getRequiredIslandLevel(nextGeneratorLevel);
            neededLevel = neededLevel.replace("%number%", neededLevelNumber);

            String neededItemsStart = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Upgrade.Needed");
            String neededItems = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Upgrade.Level_" + nextGeneratorLevel + ".NeededText");
            if (neededItemsColorCode != null) {
                neededItems = neededItemsColorCode + neededItems;
            }
            descriptionList.add("");
            descriptionList.add(neededLevel);
            descriptionList.addAll(splitString(neededItemsStart));
            descriptionList.addAll(splitString(neededItems));
        }
        return descriptionList;
    }

    private List<String> getDescriptionCobblestoneDropChance(Integer maxDrops) {
        String maxDropsLine = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.CobbleDropChanceMax");
        if (maxDropsLine != null) {
            if (maxDropsLine.contains("%number%")) {
                maxDropsLine = maxDropsLine.replace("%number%", String.valueOf(maxDrops));
            } else {
                maxDropsLine = maxDropsLine + " " + maxDrops;
            }
        } else {
            maxDropsLine = "Max Drops: " + maxDrops;
        }
        String description = ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.CobbleDropChanceDescription");
        List<String> descriptionList = new ArrayList<>();
        descriptionList.addAll(splitString(maxDropsLine));
        if (description != null) {
            descriptionList.addAll(splitString(description));
        }
        return descriptionList;
    }

    private List<String> getDescriptionChancesOverview(Integer currentGeneratorLevel) {
        if (currentGeneratorLevel == 0) {
            if (ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Chances.Level_0_Info") != null) {
                return splitString(ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Chances.Level_0_Info"));
            } else {
                return splitString("No additional items, only cobblestone");
            }
        } else {
            boolean showPercentagePerItem = ConfigShorts.getOptionsConfig().getBoolean("CobblestoneGenerator.Chances.ShowPercentagePerItem", true);
            List<String> descriptionList = new ArrayList<>();

            for (CobblestoneGenerator.Level level : CobblestoneGenerator.LEVELS) {
                if (level.getLevel() <= currentGeneratorLevel) {
                    descriptionList.addAll(splitString(ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Chances.Level_" + level.getLevel() + "_Info")));
                    if (showPercentagePerItem) {
                        descriptionList.addAll(splitString(ConfigShorts.getOptionsConfig().getString("CobblestoneGenerator.Chances.ChanceInfo")
                                .replace("%number%", String.valueOf(level.getChance()))));
                    }
                }
            }
            return descriptionList;
        }
    }

    private List<String> splitString(String string) {


        StringBuilder colorCode = new StringBuilder();

        String withoutcolorCode = string;
        for (int i = 1; i > 0; i++) {
            if (withoutcolorCode.contains("§")) {
                colorCode.append(withoutcolorCode, 0, 2);
                withoutcolorCode = withoutcolorCode.substring(2);
            } else {
                i = -1;
            }
        }




        List<String> wordbyword = new ArrayList<>();
        if (string.length() < 35) {
            wordbyword.add(string);
        } else {
            wordbyword = Arrays.asList(string.split(" "));
        }

        List<String> splittedString = new ArrayList<>();
        int i = 0;
        String currentLine = null;
        for (String word : wordbyword) {
            i = i + word.length();
            if (i > 35) {
                if (word.length() >= 35) {
                    splittedString.add(colorCode + currentLine);
                    splittedString.add(word);
                    i = 0;
                    currentLine = null;
                } else {
                    splittedString.add(colorCode + currentLine);
                    currentLine = word;
                    i = word.length();
                }
            } else {
                if (currentLine == null) {
                    currentLine = word;
                } else {
                    currentLine = currentLine + " " + word;
                }
            }
            if (wordbyword.get(wordbyword.size() - 1).equals(word)) {
                splittedString.add(colorCode + currentLine);
            }
        }
        return splittedString;
    }
}
