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
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChallengesHandler {

    public static final Map<String, Challenge> challenges = new HashMap<>();
    static final Map<String, Challenge> challengesEasy = new HashMap<>(); //DisplayName and challenge
    static final Map<String, Challenge> challengesMedium = new HashMap<>(); //DisplayName and challenge
    static final Map<String, Challenge> challengesHard = new HashMap<>(); //DisplayName and challenge
    public static List<Challenge> sortedChallengesEasy = new ArrayList<>();
    public static List<Challenge> sortedChallengesMedium = new ArrayList<>();
    public static List<Challenge> sortedChallengesHard = new ArrayList<>();

    private final DatabaseReader databaseReader = new DatabaseReader();
    private final DatabaseWriter databaseWriter = new DatabaseWriter();
    private final CreateChallengesInventory cc = new CreateChallengesInventory();

    public static Cache<UUID, Integer> onIslandDelay = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    public static Cache<UUID, Integer> islandLevelDelay = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build();

    /**
     * Checks if a challenge can be completed.
     * Checks if a player or island does match the requirements for the challenge and executes all needed updates.
     * @param challenge    The Challenge.
     * @param player       The player who wants to complete the challenge.
     */
    public void checkChallenge(Challenge challenge, Player player, Inventory inv, int challengeSlot) {
        databaseReader.getislandidfromplayer(player.getUniqueId(), (islandid) -> databaseReader.getIslandChallenges(islandid, (islandChallenges) -> {
            boolean repeat = false;
            if (islandChallenges.getChallengeCount(challenge.getMySQLKey()) != 0) {
                repeat = true;
            }

            if (challenge.getChallengeType().equals(Challenge.ChallengeType.onPlayer)) {
                List<ItemStack> rewards;
                if (repeat) {
                    rewards = challenge.getRepeatRewards();
                } else {
                    rewards = challenge.getRewards();
                }
                boolean enoughItems = true;
                for (ItemStack i : challenge.getNeededItems()) {
                    int neededamount = i.getAmount();
                    for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                        if (player.getInventory().getItem(slot) != null) {
                            if (player.getInventory().getItem(slot).getType().equals(i.getType())) {
                                neededamount = neededamount - player.getInventory().getItem(slot).getAmount();
                            }
                        }
                    }
                    if (neededamount > 0) {
                        enoughItems = false;
                    }
                }
                if (player.getGameMode().equals(GameMode.CREATIVE)) {
                    enoughItems = true;
                }
                if (enoughItems) {
                    List<Integer> emptySlots = getEmptySlots(player.getInventory());
                    if (emptySlots.size() >= rewards.size()) {
                        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                            clearItems(player.getInventory(), challenge.getNeededItems());
                        }
                        giveRewards(player.getInventory(), rewards);
                        databaseWriter.updateChallengeCount(islandid, challenge.getMySQLKey(), islandChallenges.getChallengeCount(challenge.getMySQLKey()) + 1);
                        inv.setItem(challengeSlot, cc.createChallengeItem(challenge, islandChallenges.getChallengeCount(challenge.getMySQLKey()) + 1, islandChallenges.getTrackedChallenges().contains(challenge.getMySQLKey())));
                        unTrack(challenge, player);
                        if (!repeat) {
                            ConfigShorts.broadcastChallengeCompleted("ChallengeComplete", player.getName(), challenge);
                            if (ConfigShorts.getDefConfig().isConfigurationSection("ChallengeCompleteFirst")) {
                                try {
                                    Sound sound = Sound.valueOf(ConfigShorts.getDefConfig().getString("ChallengeCompleteFirst.Sound").toUpperCase());
                                    float pitch = (float) ConfigShorts.getDefConfig().getDouble("ChallengeCompleteFirst.Pitch");
                                    for (Player p : player.getWorld().getPlayers()) {
                                        p.playSound(p.getLocation(), sound, 1, pitch);
                                    }
                                } catch (IllegalArgumentException e) {
                                    VSkyblock.getInstance().getLogger().warning("ChallengeCompleteFirst sound is invalid! " + e.getMessage());
                                }
                            }
                        } else if (ConfigShorts.getDefConfig().isConfigurationSection("ChallengeComplete")) {
                            try {
                                Sound sound = Sound.valueOf(ConfigShorts.getDefConfig().getString("ChallengeComplete.Sound").toUpperCase());
                                float pitch = (float) ConfigShorts.getDefConfig().getDouble("ChallengeComplete.Pitch");
                                player.playSound(player.getLocation(), sound, 1, pitch);
                            } catch (IllegalArgumentException e) {
                                VSkyblock.getInstance().getLogger().warning("ChallengeComplete sound is invalid! " + e.getMessage());
                            }
                        }
                    } else {
                        ConfigShorts.messagefromString("NotEnoughInventorySpace", player);
                        player.closeInventory();
                    }
                } else {
                    ConfigShorts.messagefromString("NotEnoughItems", player);
                    player.closeInventory();
                }
            } else if (challenge.getChallengeType().equals(Challenge.ChallengeType.islandLevel)) {
                if (!repeat) {
                    if (!islandLevelDelay.asMap().containsKey(player.getUniqueId())) {
                        islandLevelDelay.put(player.getUniqueId(), 1);
                        databaseReader.getislandlevelfromuuid(player.getUniqueId(), (islandLevel) -> {
                            if (islandLevel >= challenge.getNeededLevel()) {
                                if (getEmptySlots(player.getInventory()).size() >= challenge.getRewards().size()) {
                                    giveRewards(player.getInventory(), challenge.getRewards());
                                    ConfigShorts.broadcastChallengeCompleted("ChallengeComplete", player.getName(), challenge);
                                    if (ConfigShorts.getDefConfig().isConfigurationSection("ChallengeCompleteFirst")) {
                                        try {
                                            Sound sound = Sound.valueOf(ConfigShorts.getDefConfig().getString("ChallengeCompleteFirst.Sound").toUpperCase());
                                            float pitch = (float) ConfigShorts.getDefConfig().getDouble("ChallengeCompleteFirst.Pitch");
                                            for (Player p : player.getWorld().getPlayers()) {
                                                p.playSound(p.getLocation(), sound, 1, pitch);
                                            }
                                        } catch (IllegalArgumentException e) {
                                            VSkyblock.getInstance().getLogger().warning("ChallengeCompleteFirst sound is invalid! " + e.getMessage());
                                        }
                                    }
                                    databaseWriter.updateChallengeCount(islandid, challenge.getMySQLKey(), islandChallenges.getChallengeCount(challenge.getMySQLKey()) + 1);
                                    inv.setItem(challengeSlot, cc.createChallengeItem(challenge, islandChallenges.getChallengeCount(challenge.getMySQLKey()) + 1, islandChallenges.getTrackedChallenges().contains(challenge.getMySQLKey())));
                                } else {
                                    ConfigShorts.messagefromString("NotEnoughInventorySpace", player);
                                }
                            } else {
                                ConfigShorts.messagefromString("IslandLevelNotHighEnough", player);
                            }
                        });
                    } else {
                        ConfigShorts.messagefromString("AlreadyCheckedIsland", player);
                    }
                } else {
                    ConfigShorts.messagefromString("ChallengeNotRepeatable", player);
                    inv.setItem(challengeSlot, cc.createChallengeItem(challenge, islandChallenges.getChallengeCount(challenge.getMySQLKey()) + 1, islandChallenges.getTrackedChallenges().contains(challenge.getMySQLKey())));
                }
            } else if (challenge.getChallengeType().equals(Challenge.ChallengeType.onIsland)) {
                if (!repeat) {
                    if (!onIslandDelay.asMap().containsKey(player.getUniqueId())) {
                        onIslandDelay.put(player.getUniqueId(), 1);

                        ConfigShorts.messagefromString("CheckingIslandForChallenge", player);
                        HashMap<Material, Integer> result = getBlocks(player, challenge.getRadius());

                        boolean enoughItems = true;
                        for (ItemStack i : challenge.getNeededItems()) {
                            if (result.containsKey(i.getType())) {
                                if (result.get(i.getType()) < i.getAmount()) {
                                    enoughItems = false;
                                }
                            } else {
                                enoughItems = false;
                            }
                        }
                        if (enoughItems) {
                            if (getEmptySlots(player.getInventory()).size() >= challenge.getRewards().size()) {
                                giveRewards(player.getInventory(), challenge.getRewards());
                                ConfigShorts.broadcastChallengeCompleted("ChallengeComplete", player.getName(), challenge);
                                if (ConfigShorts.getDefConfig().isConfigurationSection("ChallengeCompleteFirst")) {
                                    try {
                                        Sound sound = Sound.valueOf(ConfigShorts.getDefConfig().getString("ChallengeCompleteFirst.Sound").toUpperCase());
                                        float pitch = (float) ConfigShorts.getDefConfig().getDouble("ChallengeCompleteFirst.Pitch");
                                        for (Player p : player.getWorld().getPlayers()) {
                                            p.playSound(p.getLocation(), sound, 1, pitch);
                                        }
                                    } catch (IllegalArgumentException e) {
                                        VSkyblock.getInstance().getLogger().warning("ChallengeCompleteFirst sound is invalid! " + e.getMessage());
                                    }
                                }
                                databaseWriter.updateChallengeCount(islandid, challenge.getMySQLKey(), islandChallenges.getChallengeCount(challenge.getMySQLKey()) + 1);
                                inv.setItem(challengeSlot, cc.createChallengeItem(challenge, islandChallenges.getChallengeCount(challenge.getMySQLKey()) + 1, islandChallenges.getTrackedChallenges().contains(challenge.getMySQLKey())));
                            } else {
                                ConfigShorts.messagefromString("NotEnoughInventorySpace", player);
                            }
                        } else {
                            ConfigShorts.messagefromString("IslandDoesNotMatchRequirements", player);
                        }
                    } else {
                        ConfigShorts.messagefromString("AlreadyCheckedIsland", player);
                    }
                } else {
                    ConfigShorts.messagefromString("ChallengeNotRepeatable", player);
                    inv.setItem(challengeSlot, cc.createChallengeItem(challenge, islandChallenges.getChallengeCount(challenge.getMySQLKey()) + 1, islandChallenges.getTrackedChallenges().contains(challenge.getMySQLKey())));
                }
            }
        }));
    }

    /**
     * Removes a given list of item stacks from the given inventory.
     * @param inv    The inventory.
     * @param items  The list of items to be removed
     */
    private void clearItems(Inventory inv, List<ItemStack> items) {
        for (ItemStack i : items) {
            int amount = i.getAmount();
            for (int slot = 0; slot < inv.getSize(); slot++) {
                ItemStack is = inv.getItem(slot);
                if (is != null) {
                    if (is.getType().equals(i.getType())) {
                        int newAmount = is.getAmount() - amount;
                        if (newAmount > 0) {
                            is.setAmount(newAmount);
                            break;
                        } else {
                            inv.clear(slot);
                            amount = amount - is.getAmount();
                            if (amount == 0) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Gives the rewards for the challenge.
     *
     * @param inv
     * @param items
     */
    private void giveRewards(Inventory inv, List<ItemStack> items) {
        for (ItemStack current : items) {
            inv.addItem(current.clone());
        }
    }

    /**
     * Returns a list of all empty slots in the inventory of a player.
     *
     * @param inv
     * @return Empty inventory slot numbers.
     */
    private List<Integer> getEmptySlots(Inventory inv) {
        List<Integer> emptySlots = new ArrayList<>();
        for (int currentSlot = 0; currentSlot < 36; currentSlot++) {
            if (inv.getItem(currentSlot) == null) {
                emptySlots.add(currentSlot);
            }
        }
        return emptySlots;
    }

    /**
     * Get all blocks in a specific radius around the player.
     *
     * @param player
     * @param radius
     * @return The block type counts
     */
    private HashMap<Material, Integer> getBlocks(Player player, Integer radius) {
        HashMap<Material, Integer> blocks = new HashMap<>();
        Location loc = player.getLocation();
        int blockx = (int) loc.getX();
        int blocky = (int) loc.getY();
        int blockz = (int) loc.getZ();

        for (int x = blockx - radius; x < blockx + radius; x++ ) {
            for (int z = blockz - radius; z < blockz + radius; z++) {
                if (player.getWorld().isChunkLoaded(x >> 4, z >> 4)) {
                    for (int y = blocky - radius; y < blocky + radius; y++) {
                        Material blockType = player.getWorld().getBlockAt(x, y, z).getType();
                        if (!blockType.isAir()) {
                            blocks.put(blockType, blocks.getOrDefault(blockType, 0) + 1);
                        }
                    }
                }
            }
        }
        return blocks;
    }

    public void toggleTracked(Challenge challenge, Player player) {
        databaseReader.getislandidfromplayer(player.getUniqueId(), (islandId) -> databaseReader.getIslandChallenges(islandId, (challenges) -> {
            if (challenges.getTrackedChallenges().size() < 10
                    && challenges.getChallengeCount(challenge.getChallengeName()) == 0 || challenge.getRepeatRewards() != null) {
                if (challenges.getTrackedChallenges().contains(challenge.getMySQLKey())) {
                    challenges.removeTrackedChallenge(challenge.getMySQLKey());
                    databaseWriter.updateChallengeTracked(islandId, challenge.getMySQLKey(), false);
                } else {
                    challenges.addTrackedChallenge(challenge.getMySQLKey());
                    databaseWriter.updateChallengeTracked(islandId, challenge.getMySQLKey(), true);
                }
                VSkyblock.getInstance().getScoreboardManager().updateTracked(islandId, challenges);
            }
        }));
    }

    public void unTrack(Challenge challenge, Player player) {
        databaseReader.getislandidfromplayer(player.getUniqueId(), (islandId) -> databaseReader.getIslandChallenges(islandId, (challenges) -> {
            if (challenges.removeTrackedChallenge(challenge.getMySQLKey())) {
                databaseWriter.updateChallengeTracked(islandId, challenge.getMySQLKey(), false);
                VSkyblock.getInstance().getScoreboardManager().updateTracked(islandId, challenges);
            }
        }));
    }
}
