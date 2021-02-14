package com.github.Viduality.VSkyblock.Challenges;

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

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class CreateChallengesInventory {

    public static final NamespacedKey CHALLENGE_KEY = new NamespacedKey(VSkyblock.getInstance(), "challengeid");
    private DatabaseReader databaseReader = new DatabaseReader();

    public static final String descriptioncolor = ConfigShorts.getChallengesConfig().getString("ItemOverlay.DescriptionColor");
    private final String challengeNameColor = ConfigShorts.getChallengesConfig().getString("ItemOverlay.ChallengeNameColor");
    private final String notRepeatable = ConfigShorts.getChallengesConfig().getString("ItemOverlay.NotRepeatable");
    private final String completed = ConfigShorts.getChallengesConfig().getString("ItemOverlay.Completed");
    private final String track = ConfigShorts.getChallengesConfig().getString("ItemOverlay.Track");
    private final String untrack = ConfigShorts.getChallengesConfig().getString("ItemOverlay.Untrack");
    public static final String loreString = ConfigShorts.getChallengesConfig().getString("ItemOverlay.Lore");
    public static final String neededonPlayer = ConfigShorts.getChallengesConfig().getString("ItemOverlay.Needed.onPlayer");
    public static final String neededonIsland = ConfigShorts.getChallengesConfig().getString("ItemOverlay.Needed.onIsland");
    public static final String neededislandlevel = ConfigShorts.getChallengesConfig().getString("ItemOverlay.Needed.islandlevel");
    private final String reward = ConfigShorts.getChallengesConfig().getString("ItemOverlay.Reward");

    private final String mineasycomp = ConfigShorts.getChallengesConfig().getString("CompletedChallenges.MinEasyCompleted");
    private final String minmediumcomp = ConfigShorts.getChallengesConfig().getString("CompletedChallenges.MinMediumCompleted");

    /**
     * Creates an inventory for up to 18 challenges for the given difficulty.
     *
     * @param player      The player.
     * @param difficulty  The difficulty
     * @param site        The site of the challenges difficulty.
     */
    public void createChallenges(Player player, Challenge.Difficulty difficulty, int site) {
        databaseReader.getislandidfromplayer(player.getUniqueId(), (islandid) -> databaseReader.getIslandChallenges(islandid, (islandChallenges) -> {
            Inventory cinv = Bukkit.createInventory(null, 27, "Challenges " + ConfigShorts.getChallengesConfig().getString("Difficulty." + getDifficulty(difficulty)));
            if (getChallenges(difficulty) != null) {
                if (!getChallenges(difficulty).isEmpty()) {
                    int completedchallenges = 0;
                    int slotrange = ((site - 1) * 18) + 1;
                    for (Challenge challenge : getChallenges(difficulty).values()) {
                        if (islandChallenges.getChallengeCount(challenge.getMySQLKey()) > 0) {
                            completedchallenges = completedchallenges + 1;
                        }
                    }
                    for (int i = slotrange; i <= slotrange + 17; i++) {
                        Challenge c = getChallenge(difficulty, i);
                        if (c != null) {
                            cinv.setItem(c.getInventorySlot(), createChallengeItem(c, islandChallenges.getChallengeCount(c.getMySQLKey()), islandChallenges.getTrackedChallenges().contains(c.getMySQLKey())));
                        }
                    }
                    int mineasycompleted = 5;
                    int minmediumcompleted = 5;
                    if (mineasycomp != null) {
                        if (isInt(mineasycomp)) {
                            mineasycompleted = Integer.parseInt(mineasycomp);
                        }
                    }
                    if (minmediumcomp != null) {
                        if (isInt(minmediumcomp)) {
                            minmediumcompleted = Integer.parseInt(minmediumcomp);
                        }
                    }
                    if (!difficulty.equals(Challenge.Difficulty.HARD)) {
                        ItemStack nextDifficulty = new ItemStack(Material.LIME_WOOL, 1);
                        ItemMeta nextDifficultyMeta = nextDifficulty.getItemMeta();

                        if (difficulty.equals(Challenge.Difficulty.EASY)) {
                            if (completedchallenges >= mineasycompleted) {
                                nextDifficultyMeta.setDisplayName(ChatColor.GREEN + ConfigShorts.getChallengesConfig().getString("Difficulty.Medium"));
                                nextDifficultyMeta.addEnchant(Enchantment.WATER_WORKER, 1, true);
                                nextDifficultyMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            } else {
                                nextDifficulty.setType(Material.BARRIER);
                                nextDifficultyMeta.setDisplayName(ChatColor.RED + ConfigShorts.getChallengesConfig().getString("CompletedChallenges.MinEasyCompleted"));
                            }
                        } else if (difficulty.equals(Challenge.Difficulty.MEDIUM)) {
                            if (completedchallenges >= minmediumcompleted) {
                                nextDifficultyMeta.setDisplayName(ChatColor.GREEN + ConfigShorts.getChallengesConfig().getString("Difficulty.Hard"));
                                nextDifficultyMeta.addEnchant(Enchantment.WATER_WORKER, 1, true);
                                nextDifficultyMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            } else {
                                nextDifficulty.setType(Material.BARRIER);
                                nextDifficultyMeta.setDisplayName(ChatColor.RED + ConfigShorts.getChallengesConfig().getString("CompletedChallenges.MinMediumCompleted"));
                            }

                        }
                        nextDifficulty.setItemMeta(nextDifficultyMeta);
                        cinv.setItem(26, nextDifficulty);
                    }

                    if (!difficulty.equals(Challenge.Difficulty.EASY)) {
                        ItemStack previousDifficulty = new ItemStack(Material.RED_WOOL, 1);
                        ItemMeta previousDifficultyMeta = previousDifficulty.getItemMeta();
                        if (difficulty.equals(Challenge.Difficulty.MEDIUM)) {
                            previousDifficultyMeta.setDisplayName(ChatColor.RED + ConfigShorts.getChallengesConfig().getString("Difficulty.Easy"));
                        } else if (difficulty.equals(Challenge.Difficulty.HARD)) {
                            previousDifficultyMeta.setDisplayName(ChatColor.RED + ConfigShorts.getChallengesConfig().getString("Difficulty.Medium"));
                        }

                        previousDifficultyMeta.addEnchant(Enchantment.WATER_WORKER, 1, true);
                        previousDifficultyMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        previousDifficulty.setItemMeta(previousDifficultyMeta);
                        cinv.setItem(18, previousDifficulty);
                    }

                    ItemStack currentSite = new ItemStack(Material.PAPER, site);
                    ItemMeta currentSiteMeta = currentSite.getItemMeta();
                    currentSiteMeta.setDisplayName(ChatColor.GOLD + ConfigShorts.getChallengesConfig().getString("Sites.CurrentSite") + site);

                    currentSite.setItemMeta(currentSiteMeta);
                    cinv.setItem(22, currentSite);

                    if (getChallengeListSize(difficulty) >= slotrange + 18) {
                        ItemStack nextSite = new ItemStack(Material.ARROW, 1);
                        ItemMeta nextSiteMeta = nextSite.getItemMeta();
                        nextSiteMeta.setDisplayName(ChatColor.GOLD + ConfigShorts.getChallengesConfig().getString("Sites.NextSite"));

                        nextSiteMeta.addEnchant(Enchantment.WATER_WORKER, 1, true);
                        nextSiteMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        nextSite.setItemMeta(nextSiteMeta);
                        cinv.setItem(23, nextSite);
                    }

                    if (site > 1) {
                        ItemStack previousSite = new ItemStack(Material.ARROW, 1);
                        ItemMeta previousSiteMeta = previousSite.getItemMeta();
                        previousSiteMeta.setDisplayName(ChatColor.GOLD + ConfigShorts.getChallengesConfig().getString("Sites.PreviousSite"));

                        previousSiteMeta.addEnchant(Enchantment.WATER_WORKER, 1, true);
                        previousSiteMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        previousSite.setItemMeta(previousSiteMeta);
                        cinv.setItem(21, previousSite);
                    }

                    player.openInventory(cinv);
                }
            }
        }));
    }

    private Challenge getChallenge(Challenge.Difficulty difficulty, int slot) {
        int slot1 = slot - 1;
        switch (difficulty) {
            case EASY:
                if (ChallengesHandler.sortedChallengesEasy.size() >= slot) {
                    return ChallengesHandler.sortedChallengesEasy.get(slot1);
                }
                break;
            case MEDIUM:
                if (ChallengesHandler.sortedChallengesMedium.size() >= slot) {
                    return ChallengesHandler.sortedChallengesMedium.get(slot1);
                }
                break;
            case HARD:
                if (ChallengesHandler.sortedChallengesHard.size() >= slot) {
                    return ChallengesHandler.sortedChallengesHard.get(slot1);
                }
                break;
        }
        return null;
    }

    /**
     * Creates an item for the given challenge.
     *
     * @param challenge       The challenge you want to create an item for.
     * @param challengeCount  The challenge count for the given challenge.
     * @param isTracked
     * @return ItemStack of the challenge
     */
    public ItemStack createChallengeItem(Challenge challenge, int challengeCount, boolean isTracked) {
        String challengeName = challengeNameColor + challenge.getChallengeName();
        List<String> lore = new ArrayList<>();
        lore.add(loreString);
        lore.addAll(splitString(challenge.getDescription(), descriptioncolor));
        switch (challenge.getChallengeType()) {
            case onPlayer:
                lore.add(neededonPlayer);
                break;
            case onIsland:
                lore.add(neededonIsland);
                break;
            case islandLevel:
                lore.add(neededislandlevel);
                break;
        }
        lore.addAll(splitString(challenge.getNeededText(), descriptioncolor));

        lore.add(reward);
        if (challenge.getChallengeType().equals(Challenge.ChallengeType.onPlayer)) {
            if (challengeCount != 0) {
                lore.addAll(splitString(challenge.getRepeatRewardText(), descriptioncolor));
            } else {
                lore.addAll(splitString(challenge.getRewardText(), descriptioncolor));
            }
        } else {
            lore.addAll(splitString(challenge.getRewardText(), descriptioncolor));
        }



        if (challenge.getChallengeType().equals(Challenge.ChallengeType.onIsland) ||
                challenge.getChallengeType().equals(Challenge.ChallengeType.islandLevel)) {
            if (challengeCount != 0) {
                lore.add("");
                lore.add(ChatColor.DARK_RED + notRepeatable);
            } else if (isTracked) {
                lore.add(untrack);
            } else {
                lore.add(track);
            }
        } else {
            lore.add("");
            lore.add(completed.replace("%amount%", ChatColor.GREEN + Integer.toString(challengeCount)));
            if (isTracked) {
                lore.add(untrack);
            } else {
                lore.add(track);
            }
        }
        ItemStack c = new ItemStack(challenge.getShownItem(), Math.min(Math.max(1, challengeCount), 64));
        ItemMeta challengemeta = c.getItemMeta();

        challengemeta.setDisplayName(challengeName);
        challengemeta.setLore(lore);


        /*
         * Adds cool glowing effect if the challenge is available.
         */
        if (challengeCount == 0 || challenge.getRepeatRewards() != null && !challenge.getRepeatRewards().isEmpty()) {
            challengemeta.addEnchant(Enchantment.DURABILITY, 1, true);
        }
        challengemeta.addItemFlags(ItemFlag.values());

        challengemeta.getPersistentDataContainer().set(
                CHALLENGE_KEY,
                PersistentDataType.STRING,
                challenge.getMySQLKey());

        c.setItemMeta(challengemeta);
        return c;
    }

    /**
     * Returns a hashmap with all challenges of the given difficulty
     *
     * @param difficulty  Challenge.Difficulty
     * @return HashMap
     */
    private Map<String, Challenge> getChallenges(Challenge.Difficulty difficulty) {
        switch (difficulty) {
            case EASY: return ChallengesHandler.challengesEasy;
            case MEDIUM: return ChallengesHandler.challengesMedium;
            case HARD: return ChallengesHandler.challengesHard;
            default: return null;
        }
    }

    /**
     * Returns a String of the Challenge.Difficulty
     *
     * @param difficulty
     * @return String difficulty
     */
    private String getDifficulty(Challenge.Difficulty difficulty) {
        switch (difficulty) {
            case EASY: return "Easy";
            case MEDIUM: return "Medium";
            case HARD: return "Hard";
            default: return null;
        }
    }

    /**
     * Returns the size of the sortedChallenges list.
     * Note: The size does not equal the amount of challenges!
     * @param difficulty  The challenge difficulty.
     * @return int size
     */
    private int getChallengeListSize(Challenge.Difficulty difficulty) {
        switch (difficulty) {
            case EASY: return ChallengesHandler.sortedChallengesEasy.size();
            case MEDIUM: return ChallengesHandler.sortedChallengesMedium.size();
            case HARD: return ChallengesHandler.sortedChallengesHard.size();
            default: return 0;
        }
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
