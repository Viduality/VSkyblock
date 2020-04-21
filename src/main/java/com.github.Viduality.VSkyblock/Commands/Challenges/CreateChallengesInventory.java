package com.github.Viduality.VSkyblock.Commands.Challenges;

import com.github.Viduality.VSkyblock.Utilitys.ChallengesCache;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CreateChallengesInventory {

    private VSkyblock plugin = VSkyblock.getInstance();

    private DatabaseReader databaseReader = new DatabaseReader();

    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_RESET = "\u001B[0m";

    /**
     * Creates an inventory for up to 18 challenges for the given difficulty.
     *
     * @param player
     * @param difficulty
     */
    public void createChallenges(Player player, String difficulty) {
        databaseReader.getPlayerChallenges(player.getUniqueId().toString(), "VSkyblock_Challenges_" + difficulty, new DatabaseReader.cCallback() {
            @Override
            public void onQueryDone(ChallengesCache cache) {
                Inventory cinv = Bukkit.createInventory(null, 27, "Challenges " + ConfigShorts.getChallengesConfig().getString("Difficulty." + difficulty));
                Set<String> challenges = ConfigShorts.getChallengesConfig().getConfigurationSection(difficulty).getKeys(false);
                String descriptioncolor = ConfigShorts.getChallengesConfig().getString("ItemOverlay.DescriptionColor");
                String challengeNameColor = ConfigShorts.getChallengesConfig().getString("ItemOverlay.ChallengeNameColor");
                List<Integer> usedSlots = new ArrayList<>();
                int completedchallenges = 0;
                int challengecount = 0;
                for (String currentChallenge : challenges) {
                    challengecount = challengecount + 1;
                    String shownItem = ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".ShownItem");
                    String description = ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".Description");
                    List<String> rewards = new ArrayList<>();
                    List<String> itemRewards = new ArrayList<>();
                    List<Integer> itemRewardsamounts = new ArrayList<>();
                    Material item;
                    int slot = -1;
                    if (isInt(ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".Slot"))) {
                        slot = ConfigShorts.getChallengesConfig().getInt(difficulty + "." + currentChallenge + ".Slot");
                    }
                    String itemsneededText = ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".NeededText");
                    String itemsrewardText;
                    if (ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".Type").equals("onPlayer")) {
                        if (cache.getCurrentChallengeCount(challengecount) != 0) {
                            itemsrewardText = ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".RepeatRewardText");
                            completedchallenges = completedchallenges + 1;
                        } else {
                            itemsrewardText = ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".RewardText");
                        }
                    } else {
                        itemsrewardText = ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".RewardText");
                    }
                    if (ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".Type").equals("onPlayer")) {
                        List<String> itemsneeded = ConfigShorts.getChallengesConfig().getStringList( difficulty + "." + currentChallenge + ".Needed");
                        List<String> neededItems = new ArrayList<>();
                        List<Integer> neededItemsamounts = new ArrayList<>();

                        if (cache.getCurrentChallengeCount(challengecount) != 0) {
                            rewards = ConfigShorts.getChallengesConfig().getStringList(difficulty + "." + currentChallenge + ".RepeatReward");
                        } else {
                            rewards = ConfigShorts.getChallengesConfig().getStringList(difficulty + "." + currentChallenge + ".Reward");
                        }
                        if (Material.matchMaterial(shownItem.toUpperCase()) != null) {
                            if (description != null) {
                                if (!itemsneeded.isEmpty()) {
                                    if (!rewards.isEmpty()) {
                                        if (itemsneededText != null) {
                                            if (itemsrewardText != null) {
                                                if (slot > 0 && slot < 18) {
                                                    item = Material.getMaterial(shownItem.toUpperCase());

                                                    for (String itemsneeded1 : itemsneeded) {
                                                        if (itemsneeded1.contains(";")) {
                                                            String[] current = itemsneeded1.split(";");
                                                            if (Material.matchMaterial(current[0].toUpperCase()) != null) {
                                                                if (isInt(current[1])) {
                                                                    Integer amount = Integer.valueOf(current[1]);
                                                                    neededItems.add(current[0]);
                                                                    neededItemsamounts.add(amount);
                                                                } else {
                                                                    System.out.println(ChatColor.RED + "Could not set challenge correctly! Challenge: " + currentChallenge + " Needed Item: " + itemsneeded1);
                                                                }
                                                            } else {
                                                                System.out.println(ChatColor.RED + "Could not set challenge correctly! Challenge: " + currentChallenge + " Needed Item: " + itemsneeded1);
                                                            }
                                                        } else {
                                                            if (Material.matchMaterial(itemsneeded1.toUpperCase()) != null) {
                                                                neededItems.add(itemsneeded1);
                                                                neededItemsamounts.add(1);
                                                            } else {
                                                                System.out.println(ChatColor.RED + "Could not set challenge correctly! Challenge: " + currentChallenge + " Needed Item: " + itemsneeded1);
                                                            }
                                                        }
                                                    }

                                                    for (String anrewards : rewards) {
                                                        if (anrewards.contains(";")) {
                                                            String[] current = anrewards.split(";");
                                                            if (Material.matchMaterial(current[0].toUpperCase()) != null) {
                                                                if (isInt(current[1])) {
                                                                    Integer amount = Integer.valueOf(current[1]);
                                                                    itemRewards.add(current[0]);
                                                                    itemRewardsamounts.add(amount);
                                                                } else {
                                                                    System.out.println(ChatColor.RED + "Could not set challenge correctly! Challenge: " + currentChallenge + " Reward: " + anrewards);
                                                                }
                                                            } else {
                                                                System.out.println(ChatColor.RED + "Could not set challenge correctly! Challenge: " + currentChallenge + " Reward: " + anrewards);
                                                            }
                                                        } else {
                                                            if (Material.matchMaterial(anrewards) != null) {
                                                                itemRewards.add(anrewards);
                                                                itemRewardsamounts.add(1);
                                                            } else {
                                                                System.out.println(ChatColor.RED + "Could not set challenge correctly! Challenge: " + currentChallenge + " Reward: " + anrewards);
                                                            }
                                                        }
                                                    }

                                                    List<String> lore = new ArrayList<>();
                                                    lore.add(ConfigShorts.getChallengesConfig().getString("ItemOverlay.Lore") + ":");
                                                    lore.addAll(splitString(description, descriptioncolor));
                                                    lore.add(ConfigShorts.getChallengesConfig().getString("ItemOverlay.Needed.onPlayer") + ":");
                                                    lore.addAll(splitString(itemsneededText, descriptioncolor));

                                                    lore.add(ConfigShorts.getChallengesConfig().getString("ItemOverlay.Reward") + ":");
                                                    lore.addAll(splitString(itemsrewardText, descriptioncolor));

                                                    int count = cache.getCurrentChallengeCount(challengecount);
                                                    String challengeCount = ConfigShorts.getChallengesConfig().getString("ItemOverlay.Completed").replace("%amount%", ChatColor.GREEN + Integer.toString(count) + ChatColor.DARK_PURPLE);

                                                    lore.add("");
                                                    lore.add(challengeCount);


                                                    ItemStack challenge = new ItemStack(item, 1);
                                                    ItemMeta challengemeta = challenge.getItemMeta();

                                                    challengemeta.setDisplayName(challengeNameColor + currentChallenge);
                                                    challengemeta.setLore(lore);


                                                    /*
                                                     * Adds cool glowing effect.
                                                     */

                                                    challengemeta.addEnchant(Enchantment.DURABILITY, 1, true);
                                                    challengemeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                                                    challengemeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                                                    challengemeta.addItemFlags(ItemFlag.values());


                                                    challenge.setItemMeta(challengemeta);

                                                    if (!usedSlots.contains(slot)) {
                                                        cinv.setItem(slot - 1, challenge);
                                                    } else {
                                                        System.out.println(ANSI_RED + "Could not set challenge correctly! SLOT already in use! Challenge: " + currentChallenge + ANSI_RESET);
                                                    }
                                                    usedSlots.add(slot);
                                                    if (challengecount > 17) {
                                                        break;
                                                    }
                                                } else {
                                                    System.out.println(ANSI_RED + "Could not set challenge correctly! Correct the SLOT! Challenge: " + currentChallenge + ANSI_RESET);
                                                }
                                            } else {
                                                System.out.println(ANSI_RED + "Could not set challenge correctly! Correct the REWARD TEXT! Challenge: " + currentChallenge + ANSI_RESET);
                                            }
                                        } else {
                                            System.out.println(ANSI_RED + "Could not set challenge correctly! Correct the NEEDED ITEMS TEXT! Challenge: " + currentChallenge + ANSI_RESET);
                                        }
                                    } else {
                                        System.out.println(ANSI_RED + "Could not set challenge correctly! Add REWARDS! Challenge: " + currentChallenge + ANSI_RESET);
                                    }
                                } else {
                                    System.out.println(ANSI_RED + "Could not set challenge correctly! Add NEEDED ITEMS! Challenge: " + currentChallenge + ANSI_RESET);
                                }
                            } else {
                                System.out.println(ANSI_RED + "Could not set challenge correctly! Add a DESCRIPTION! Challenge: " + currentChallenge + ANSI_RESET);
                            }
                        } else {
                            System.out.println(ANSI_RED + "Could not set challenge correctly! Use a valid MATERIAL for the shown item! Challenge: " + currentChallenge + ANSI_RESET);
                        }
                    } else if (ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".Type").equals("islandlevel") || ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".Type").equals("onIsland")) {

                        String levelneeded = ConfigShorts.getChallengesConfig().getString( difficulty + "." + currentChallenge + ".Needed");
                        rewards = ConfigShorts.getChallengesConfig().getStringList(difficulty + "." + currentChallenge + ".Reward");

                        if (Material.matchMaterial(shownItem.toUpperCase()) != null) {
                            if (description != null) {
                                if (levelneeded != null) {
                                    if (!rewards.isEmpty()) {
                                        if (itemsneededText != null) {
                                            if (itemsrewardText != null) {
                                                if (slot > 0 && slot < 18) {
                                                    item = Material.getMaterial(shownItem.toUpperCase());
                                                    String neededText = ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".NeededText");

                                                    for (String anrewards : rewards) {
                                                        if (anrewards.contains(";")) {
                                                            String[] current = anrewards.split(";");
                                                            if (Material.matchMaterial(current[0].toUpperCase()) != null) {
                                                                if (isInt(current[1])) {
                                                                    Integer amount = Integer.valueOf(current[1]);
                                                                    itemRewards.add(current[0]);
                                                                    itemRewardsamounts.add(amount);
                                                                } else {
                                                                    System.out.println(ChatColor.RED + "Could not set challenge correctly! Challenge: " + currentChallenge + " Reward: " + anrewards);
                                                                }
                                                            } else {
                                                                System.out.println(ChatColor.RED + "Could not set challenge correctly! Challenge: " + currentChallenge + " Reward: " + anrewards);
                                                            }
                                                        } else {
                                                            if (Material.matchMaterial(anrewards) != null) {
                                                                itemRewards.add(anrewards);
                                                                itemRewardsamounts.add(1);
                                                            } else {
                                                                System.out.println(ChatColor.RED + "Could not set challenge correctly! Challenge: " + currentChallenge + " Reward: " + anrewards);
                                                            }
                                                        }
                                                    }

                                                    List<String> lore = new ArrayList<>();
                                                    lore.add(ConfigShorts.getChallengesConfig().getString("ItemOverlay.Lore") + ":");
                                                    lore.addAll(splitString(description, descriptioncolor));
                                                    if (ConfigShorts.getChallengesConfig().getString(difficulty + "." + currentChallenge + ".Type").equals("islandlevel")) {
                                                        lore.add(ConfigShorts.getChallengesConfig().getString("ItemOverlay.Needed.islandlevel") + ":");
                                                    } else {
                                                        lore.add(ConfigShorts.getChallengesConfig().getString("ItemOverlay.Needed.onIsland") + ":");
                                                    }

                                                    lore.addAll(splitString(neededText, descriptioncolor));
                                                    lore.add(ConfigShorts.getChallengesConfig().getString("ItemOverlay.Reward") + ":");
                                                    lore.addAll(splitString(itemsrewardText, descriptioncolor));

                                                    if (cache.getCurrentChallengeCount(challengecount) != 0) {
                                                        completedchallenges = completedchallenges + 1;
                                                        lore.add("");
                                                        lore.add(ChatColor.DARK_RED + ConfigShorts.getChallengesConfig().getString("ItemOverlay.NotRepeatable"));
                                                    }

                                                    ItemStack challenge = new ItemStack(item, 1);
                                                    ItemMeta challengemeta = challenge.getItemMeta();

                                                    challengemeta.setDisplayName(challengeNameColor + currentChallenge);
                                                    challengemeta.setLore(lore);


                                                    /*
                                                     * Adds cool glowing effect.
                                                     */

                                                    challengemeta.addEnchant(Enchantment.DURABILITY, 1, true);
                                                    challengemeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                                                    challengemeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                                                    challengemeta.addItemFlags(ItemFlag.values());


                                                    challenge.setItemMeta(challengemeta);

                                                    if (!usedSlots.contains(slot)) {
                                                        cinv.setItem(slot - 1, challenge);
                                                    } else {
                                                        System.out.println(ANSI_RED + "Could not set challenge correctly! SLOT already in use! Challenge: " + currentChallenge + ANSI_RESET);
                                                    }
                                                    usedSlots.add(slot);
                                                    if (challengecount > 17) {
                                                        break;
                                                    }
                                                } else {
                                                    System.out.println(ANSI_RED + "Could not set challenge correctly! Correct the SLOT! Challenge: " + currentChallenge + ANSI_RESET);
                                                }
                                            } else {
                                                System.out.println(ANSI_RED + "Could not set challenge correctly! Correct the REWARD TEXT! Challenge: " + currentChallenge + ANSI_RESET);
                                            }
                                        } else {
                                            System.out.println(ANSI_RED + "Could not set challenge correctly! Correct the NEEDED ITEMS TEXT! Challenge: " + currentChallenge + ANSI_RESET);
                                        }
                                    } else {
                                        System.out.println(ANSI_RED + "Could not set challenge correctly! Add REWARDS! Challenge: " + currentChallenge + ANSI_RESET);
                                    }
                                } else {
                                    System.out.println(ANSI_RED + "Could not set challenge correctly! Add NEEDED ITEMS! Challenge: " + currentChallenge + ANSI_RESET);
                                }
                            } else {
                                System.out.println(ANSI_RED + "Could not set challenge correctly! Add a DESCRIPTION! Challenge: " + currentChallenge + ANSI_RESET);
                            }
                        } else {
                            System.out.println(ANSI_RED + "Could not set challenge correctly! Use a valid MATERIAL for the shown item! Challenge: " + currentChallenge + ANSI_RESET);
                        }
                    } else {
                        System.out.println(ANSI_RED + "Could not set challenge correctly! Use a valid TYPE! Challenge: " + currentChallenge + ANSI_RESET);
                    }
                }
                String mineasycomp = ConfigShorts.getChallengesConfig().getString("CompletedChallenges.MinEasyCompleted");
                String minmediumcomp = ConfigShorts.getChallengesConfig().getString("CompletedChallenges.MinMediumCompleted");
                int mineasycompleted = 5;
                int minmediumcompleted = 5;
                if (isInt(mineasycomp)) {
                    mineasycompleted = Integer.parseInt(mineasycomp);
                }
                if (isInt(minmediumcomp)) {
                    minmediumcompleted = Integer.parseInt(minmediumcomp);
                }
                if (!difficulty.equals("Hard")) {
                    ItemStack nextSite = new ItemStack(Material.LIME_WOOL, 1);
                    ItemMeta nextSiteMeta = nextSite.getItemMeta();

                    if (difficulty.equals("Easy")) {
                        if (completedchallenges >= mineasycompleted) {
                            nextSiteMeta.setDisplayName(ChatColor.GREEN + ConfigShorts.getChallengesConfig().getString("Difficulty.Medium"));
                        } else {
                            nextSite.setType(Material.BARRIER);
                            nextSiteMeta.setDisplayName(ChatColor.RED + ConfigShorts.getChallengesConfig().getString("CompletedChallenges.MinEasyCompleted"));
                        }
                    } else if (difficulty.equals("Medium")) {
                        if (completedchallenges >= minmediumcompleted) {
                            nextSiteMeta.setDisplayName(ChatColor.GREEN + ConfigShorts.getChallengesConfig().getString("Difficulty.Hard"));
                        } else {
                            nextSite.setType(Material.BARRIER);
                            nextSiteMeta.setDisplayName(ChatColor.RED + ConfigShorts.getChallengesConfig().getString("CompletedChallenges.MinMediumCompleted"));
                        }

                    }

                    nextSiteMeta.addEnchant(Enchantment.WATER_WORKER, 1, true);
                    nextSiteMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    nextSite.setItemMeta(nextSiteMeta);
                    cinv.setItem(26, nextSite);
                }


                if (!difficulty.equals("Easy")) {
                    ItemStack previousSite = new ItemStack(Material.RED_WOOL, 1);
                    ItemMeta previousSiteMeta = previousSite.getItemMeta();
                    if (difficulty.equals("Medium")) {
                        previousSiteMeta.setDisplayName(ChatColor.RED + ConfigShorts.getChallengesConfig().getString("Difficulty.Easy"));
                    } else if (difficulty.equals("Hard")) {
                        previousSiteMeta.setDisplayName(ChatColor.RED + ConfigShorts.getChallengesConfig().getString("Difficulty.Medium"));
                    }

                    previousSiteMeta.addEnchant(Enchantment.WATER_WORKER, 1, true);
                    previousSiteMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    previousSite.setItemMeta(previousSiteMeta);
                    cinv.setItem(18, previousSite);
                }
                player.openInventory(cinv);
            }
        });
    }

    /**
     * Checks if an String is from type Integer.
     *
     * @param s
     * @return boolean
     */
    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Splits an string.
     *
     * @param string
     * @param colorCode
     * @return List of String
     */
    private List<String> splitString(String string, String colorCode) {
        List<String> wordbyword = new ArrayList<>();
        if (string.length() < 30) {
            wordbyword.add(string);
        } else {
            wordbyword = Arrays.asList(string.split(" "));
        }

        List<String> splittedString = new ArrayList<>();
        int i = 0;
        String currentLine = null;
        for (String word : wordbyword) {
            i = i + word.length();
            if (i > 30) {
                if (word.length() >= 30) {
                    splittedString.add(currentLine);
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
