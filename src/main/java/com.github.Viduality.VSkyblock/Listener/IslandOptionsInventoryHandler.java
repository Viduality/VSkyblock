package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
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
            int visitslot = 2;
            int generatorslot = 4;
            int difficultyslot = 6;
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
            } else if (slot == generatorslot) {
                if (inventoryClickEvent.getCurrentItem().getType().equals(Material.COBBLESTONE)) {
                    inventoryClickEvent.getWhoClicked().closeInventory();
                    inventoryClickEvent.getWhoClicked().openInventory(getGeneratorMenu(inventoryClickEvent.getWhoClicked().getUniqueId().toString()));
                    ConfigShorts.loaddefConfig();
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
                        databaseReader.getislandnamefromplayer(player.getUniqueId().toString(), result ->
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


    /**
     * Creates the menu for the cobblestone generator.
     *
     * @return Inventory
     */
    private Inventory getGeneratorMenu(String playerUUID) {
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

    private ItemStack getUpgradeButton(String playerUUID) {
        ConfigShorts.loadOptionsConfig();
        String island = Island.playerislands.get(playerUUID);
        Integer currentGeneratorLevel = CobblestoneGenerator.islandGenLevel.get(island);
        Integer nextGeneratorLevel = currentGeneratorLevel + 1;
        String upgradeButtonItem;
        if (nextGeneratorLevel > 7) {
            upgradeButtonItem = plugin.getConfig().getString("CobblestoneGenerator.Upgrade.Max_Level.Item");
        } else {
            upgradeButtonItem = plugin.getConfig().getString("CobblestoneGenerator.Upgrade.Level_" + nextGeneratorLevel + ".Item");
        }
        Material mat = getMaterial(upgradeButtonItem);
        ItemStack upgradeButton = new ItemStack(mat, 1);
        ItemMeta upgradeButtonMeta = upgradeButton.getItemMeta();

        upgradeButtonMeta.setDisplayName(getDisplayNameGeneratorUpgradeButton(nextGeneratorLevel));
        upgradeButtonMeta.setLore(getDescriptionGeneratorUpgradeButton(nextGeneratorLevel));

        ConfigShorts.loaddefConfig();

        upgradeButtonMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        upgradeButtonMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        upgradeButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        upgradeButtonMeta.addItemFlags(ItemFlag.values());
        upgradeButton.setItemMeta(upgradeButtonMeta);
        return upgradeButton;
    }

    private ItemStack getCobbleDropChanceInfo(String playerUUID) {
        ConfigShorts.loadOptionsConfig();
        String island = Island.playerislands.get(playerUUID);
        Integer currentIslandLevel = CobblestoneGenerator.islandlevels.get(island);
        Double levelIntervall = CobblestoneGenerator.generatorValues.get("CobblestoneLevelIntervall");
        int maxDrops = (int) ((currentIslandLevel/levelIntervall) + 1);


        Material mat = Material.COBBLESTONE;
        ItemStack cobbleDropChanceInfo = new ItemStack(mat, 1);
        ItemMeta cobbleDropChanceInfoMeta = cobbleDropChanceInfo.getItemMeta();

        cobbleDropChanceInfoMeta.setDisplayName(getDisplayNameCobbleDropChance());
        cobbleDropChanceInfoMeta.setLore(getDescriptionCobblestoneDropChance(maxDrops));

        ConfigShorts.loaddefConfig();

        cobbleDropChanceInfoMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        cobbleDropChanceInfoMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        cobbleDropChanceInfoMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        cobbleDropChanceInfoMeta.addItemFlags(ItemFlag.values());
        cobbleDropChanceInfo.setItemMeta(cobbleDropChanceInfoMeta);
        return cobbleDropChanceInfo;
    }

    private ItemStack getChancesOverview(String playerUUID) {
        ConfigShorts.loadOptionsConfig();
        String island = Island.playerislands.get(playerUUID);
        Integer currentgeneratorLevel = CobblestoneGenerator.islandGenLevel.get(island);

        String chancesOverviewItem = plugin.getConfig().getString("CobblestoneGenerator.Chances.Level_" + currentgeneratorLevel + "_Item");
        Material mat = getMaterial(chancesOverviewItem);

        ItemStack chancesOverview = new ItemStack(mat, 1);
        ItemMeta chancesOverviewMeta = chancesOverview.getItemMeta();

        chancesOverviewMeta.setDisplayName(getDisplayNameChancesOverview());
        chancesOverviewMeta.setLore(getDescriptionChancesOverview(currentgeneratorLevel));
        ConfigShorts.loaddefConfig();

        chancesOverviewMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        chancesOverviewMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        chancesOverviewMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
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
        String displayname = plugin.getConfig().getString("CobblestoneGenerator.DisplayName");
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
        if (nextGeneratorLevel > 7) {
            displayname = plugin.getConfig().getString("CobblestoneGenerator.Upgrade.DisplayNameMaxLevel");
        } else {
            displayname = plugin.getConfig().getString("CobblestoneGenerator.Upgrade.DisplayName");
        }
        if (displayname != null) {
            return displayname;
        } else {
            return "Upgrade";
        }
    }

    private String getDisplayNameCobbleDropChance() {
        String displayname = plugin.getConfig().getString("CobblestoneGenerator.CobbleDropChance");
        if (displayname != null) {
            return displayname;
        } else {
            return "Cobblestone Drops";
        }
    }

    private String getDisplayNameChancesOverview() {
        String displayname = plugin.getConfig().getString("CobblestoneGenerator.Chances.Overview");
        if (displayname != null) {
            return displayname;
        } else {
            return "Cobblestone-Generator Drops";
        }
    }

    private List<String> getDescriptionGeneratorUpgradeButton(Integer nextGeneratorLevel) {
        List<String> descriptionList = new ArrayList<>();
        if (nextGeneratorLevel > 7) {
            String description = plugin.getConfig().getString("CobblestoneGenerator.Upgrade.Max_Level.Description");
            descriptionList.addAll(splitString(description));
        } else {
            String overlay = plugin.getConfig().getString("CobblestoneGenerator.Upgrade.Adds");
            String description = plugin.getConfig().getString("CobblestoneGenerator.Upgrade.Level_" + nextGeneratorLevel + ".AddsFeature");
            if (overlay != null) {
                descriptionList.addAll(splitString(overlay));
            }
            if (description != null) {
                descriptionList.addAll(splitString(description));
            }

            String neededItemsColorCode = plugin.getConfig().getString("CobblestoneGenerator.Upgrade.NeededColor");

            String neededLevel = plugin.getConfig().getString("CobblestoneGenerator.Upgrade.NeededLevel");
            String neededLevelNumber = neededItemsColorCode + getRequiredIslandLevel(nextGeneratorLevel);
            neededLevel = neededLevel.replace("%number%", neededLevelNumber);

            String neededItemsStart = plugin.getConfig().getString("CobblestoneGenerator.Upgrade.Needed");
            String neededItems = plugin.getConfig().getString("CobblestoneGenerator.Upgrade.Level_" + nextGeneratorLevel + ".NeededText");
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
        String maxDropsLine = plugin.getConfig().getString("CobblestoneGenerator.CobbleDropChanceMax");
        if (maxDropsLine != null) {
            if (maxDropsLine.contains("%number%")) {
                maxDropsLine = maxDropsLine.replace("%number%", String.valueOf(maxDrops));
            } else {
                maxDropsLine = maxDropsLine + " " + String.valueOf(maxDrops);
            }
        } else {
            maxDropsLine = "Max Drops: " + String.valueOf(maxDrops);
        }
        String description = plugin.getConfig().getString("CobblestoneGenerator.CobbleDropChanceDescription");
        List<String> descriptionList = new ArrayList<>();
        descriptionList.addAll(splitString(maxDropsLine));
        if (description != null) {
            descriptionList.addAll(splitString(description));
        }
        return descriptionList;
    }

    private List<String> getDescriptionChancesOverview(Integer currentGeneratorLevel) {
        if (currentGeneratorLevel == 0) {
            if (plugin.getConfig().getString("CobblestoneGenerator.Chances.Level_0_Info") != null) {
                return splitString(plugin.getConfig().getString("CobblestoneGenerator.Chances.Level_0_Info"));
            } else {
                return splitString("No additional items, only cobblestone");
            }
        } else {
            boolean showPercentagePerItem = true;
            if (plugin.getConfig().getString("CobblestoneGenerator.Chances.ShowPercentagePerItem").equalsIgnoreCase("false")) {
                showPercentagePerItem = false;
            }
            List<String> descriptionList = new ArrayList<>();

            for (int i = 1; i <= currentGeneratorLevel; i++) {
                String chanceInfoText = plugin.getConfig().getString("CobblestoneGenerator.Chances.Level_" + i + "_Info");
                String chanceInfo = plugin.getConfig().getString("CobblestoneGenerator.Chances.ChanceInfo");
                if (chanceInfo.contains("%number%")) {
                    switch (i) {
                        case 1:
                            chanceInfo = chanceInfo.replace("%number%", String.valueOf(CobblestoneGenerator.generatorValues.get("CoalChance")));
                            break;
                        case 2:
                            chanceInfo = chanceInfo.replace("%number%", String.valueOf(CobblestoneGenerator.generatorValues.get("IronChance")));
                            break;
                        case 3:
                            chanceInfo = chanceInfo.replace("%number%", String.valueOf(CobblestoneGenerator.generatorValues.get("RedstoneChance")));
                            break;
                        case 4:
                            chanceInfo = chanceInfo.replace("%number%", String.valueOf(CobblestoneGenerator.generatorValues.get("LapisChance")));
                            break;
                        case 5:
                            chanceInfo = chanceInfo.replace("%number%", String.valueOf(CobblestoneGenerator.generatorValues.get("GoldChance")));
                            break;
                        case 6:
                            chanceInfo = chanceInfo.replace("%number%", String.valueOf(CobblestoneGenerator.generatorValues.get("EmeraldChance")));
                            break;
                        case 7:
                            chanceInfo = chanceInfo.replace("%number%", String.valueOf(CobblestoneGenerator.generatorValues.get("DiamondChance")));
                            break;

                    }
                }
                descriptionList.addAll(splitString(chanceInfoText));
                if (showPercentagePerItem) {
                    descriptionList.addAll(splitString(chanceInfo));
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
                colorCode.append(withoutcolorCode.substring(0, 2));
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

    private Double getRequiredIslandLevel(int nextGeneratorLevel) {
        switch (nextGeneratorLevel) {
            case 1:
                return CobblestoneGenerator.generatorValues.get("CoalLevel");
            case 2:
                return CobblestoneGenerator.generatorValues.get("IronLevel");
            case 3:
                return CobblestoneGenerator.generatorValues.get("RedstoneLevel");
            case 4:
                return CobblestoneGenerator.generatorValues.get("LapisLevel");
            case 5:
                return CobblestoneGenerator.generatorValues.get("GoldLevel");
            case 6:
                return CobblestoneGenerator.generatorValues.get("EmeraldLevel");
            case 7:
                return CobblestoneGenerator.generatorValues.get("DiamondLevel");
            default:
                return null;
        }
    }
}
