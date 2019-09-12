package com.github.Viduality.VSkyblock;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Listener.CobblestoneGenerator;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CobblestoneGeneratorUpgrade {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseWriter databaseWriter = new DatabaseWriter();
    private DatabaseReader databaseReader = new DatabaseReader();

    /**
     * Checks if the current cobblestone generator can be upgraded.
     * @param island
     * @param player
     */
    public void checkForGeneratorUpgrade(String island, Player player) {
        int currentlevel = CobblestoneGenerator.islandGenLevel.get(island);
        int nextlevel = currentlevel + 1;

        Double requiredIslandLevel = getRequiredIslandLevel(nextlevel);
        int islandlevel = CobblestoneGenerator.islandlevels.get(Island.playerislands.get(player.getUniqueId().toString()));
        if (islandlevel >= requiredIslandLevel) {
            List<Material> neededItems = getneededItems(nextlevel);
            List<Integer> neededAmounts = getneededItemsAmount(nextlevel);

            boolean enoughItems = true;

            for (int i = 0; i < neededItems.size(); i++) {
                int neededAmount = neededAmounts.get(i);
                int currentamount = 0;
                for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                    if (player.getInventory().getItem(slot) != null) {
                        if (player.getInventory().getItem(slot).getType().equals(neededItems.get(i))) {
                            currentamount = currentamount + player.getInventory().getItem(slot).getAmount();
                        }
                    }
                }
                if (currentamount < neededAmount) {
                    enoughItems = false;
                }
            }

            if (enoughItems) {
                upgradeCobbleGenerator(island, nextlevel);
                removeItems(neededItems, neededAmounts, player);
                player.closeInventory();
                databaseReader.getislandid(Island.playerislands.get(player.getUniqueId().toString()), new DatabaseReader.CallbackINT() {
                    @Override
                    public void onQueryDone(int result) {
                        databaseReader.getIslandMembers(result, new DatabaseReader.CallbackList() {
                            @Override
                            public void onQueryDone(List<String> result) {
                                for (String member : result) {
                                    OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(member);
                                    if (offlinePlayer.isOnline()) {
                                        Player onlinePlayer = (Player) offlinePlayer;
                                        ConfigShorts.custommessagefromString("UpgradedYourCobblestoneGenerator", onlinePlayer, player.getName());
                                    }
                                }
                            }
                        });
                    }
                });
            } else {
                ConfigShorts.messagefromString("NotEnoughItemsGenerator", player);
            }
        } else {
            ConfigShorts.messagefromString("IslandLevelNotHighEnough", player);
        }
    }

    /**
     * Returns the needed items to upgrade the generator.
     * Use with getneededItemsAmount method.
     * @param nextLevel
     * @return Material List
     */
    private List<Material> getneededItems(int nextLevel) {
        ConfigShorts.loadOptionsConfig();
        List<String> neededItemsStringwithAmount = plugin.getConfig().getStringList("CobblestoneGenerator.Upgrade.Level_" + nextLevel + ".Needed");
        List<String> neededItemsString = new ArrayList<>();
        for (String current : neededItemsStringwithAmount) {
            String[] String = current.split(";");
            neededItemsString.add(String[0]);
        }
        List<Material> neededItems = new ArrayList<>();
        for (String current : neededItemsString) {
            if (Material.matchMaterial(current.toUpperCase()) != null) {
                neededItems.add(Material.getMaterial(current.toUpperCase()));
            }
        }
        ConfigShorts.loaddefConfig();
        return neededItems;
    }

    /**
     * Returns the needed item amount to upgrade the generator.
     * Use with getneededItems method.
     * @param nextLevel
     * @return Integer List
     */
    private List<Integer> getneededItemsAmount(int nextLevel) {
        ConfigShorts.loadOptionsConfig();
        List<String> neededAmountStringwithItems = plugin.getConfig().getStringList("CobblestoneGenerator.Upgrade.Level_" + nextLevel + ".Needed");
        List<Integer> neededAmount = new ArrayList<>();
        for (String current : neededAmountStringwithItems) {
            String[] String = current.split(";");
            neededAmount.add(Integer.valueOf(String[1]));
        }
        ConfigShorts.loaddefConfig();
        return neededAmount;
    }

    /**
     * Upgrades the current cobblestone generator.
     * @param island
     * @param nextLevel
     */
    private void upgradeCobbleGenerator(String island, int nextLevel) {
        databaseWriter.updateCobblestoneGeneratorLevel(island, nextLevel);
        CobblestoneGenerator.islandGenLevel.put(island, nextLevel);
    }

    /**
     * Deletes the given Items from the players inventory.
     *
     * @param items
     * @param amounts
     * @param player
     */
    private void removeItems(List<Material> items, List<Integer> amounts, Player player) {
        for (int i = 0; i < items.size(); i++) {
            int neededamount = amounts.get(i);
            Material currentItem = items.get(i);

            int x = 0;

            while (x < neededamount) {
                for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                    ItemStack currentItemSlot = player.getInventory().getItem(slot);
                    if (currentItemSlot != null) {
                        if (currentItemSlot.getType().equals(currentItem)) {
                            int newAmount = currentItemSlot.getAmount() - neededamount;
                            if (newAmount > 0) {
                                currentItemSlot.setAmount(newAmount);
                                x = neededamount;
                                break;
                            } else {
                                player.getInventory().clear(slot);
                                neededamount = neededamount - currentItemSlot.getAmount();
                                if (neededamount == 0) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the required island level to upgrade the current cobblestone generator.
     *
     * @param nextGeneratorLevel
     * @return Double
     */
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
