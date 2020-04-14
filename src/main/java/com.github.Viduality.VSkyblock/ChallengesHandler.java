package com.github.Viduality.VSkyblock;

import com.github.Viduality.VSkyblock.Utilitys.*;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ChallengesHandler {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private DatabaseWriter databaseWriter = new DatabaseWriter();

    public static Cache<UUID, Integer> onIslandDelay = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    /**
     * Checks if a challenge can be completed.
     * Checks if a player or island does match the requirements for the challenge and executes all needed updates.
     * @param challenge
     * @param challengeName
     * @param difficulty
     * @param player
     */
    public void checkChallenge(int challenge, String challengeName, String difficulty, Player player) {
        databaseReader.getPlayerChallenges(player.getUniqueId().toString(), "VSkyblock_Challenges_" + difficulty, new DatabaseReader.cCallback() {
            @Override
            public void onQueryDone(ChallengesCache cache) {
                boolean repeat;
                if (cache.getCurrentChallengeCount(challenge) != 0) {
                    repeat = true;
                } else {
                    repeat = false;
                }
                List<Integer> emptySlots = getEmptySlots(player.getInventory());
                if (plugin.getConfig().getString(difficulty + "." + challengeName + ".Type").equals("onPlayer")) {
                    List<String> needed = getNeeded(challengeName, difficulty);
                    List<Integer> neededamount = getNeededAmounts(challengeName, difficulty);
                    List<String> reward = getRewards(challengeName, difficulty, repeat);
                    List<Integer> rewardamounts = getRewardAmounts(challengeName, difficulty, repeat);

                    int enoughItems = 0;
                    List<ItemStack> rewards = new ArrayList<>();

                    for (int i = 0; i < reward.size(); i++) {
                        ItemStack current = new ItemStack(Material.getMaterial(reward.get(i).toUpperCase()), rewardamounts.get(i));
                        rewards.add(current);
                    }

                    for (int i = 0; i < needed.size(); i++) {
                        int currentneededamount = neededamount.get(i);
                        int currentamount = 0;
                        for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                            if (player.getInventory().getItem(slot) != null) {
                                if (player.getInventory().getItem(slot).getType().equals(Material.getMaterial(needed.get(i).toUpperCase()))) {
                                    currentamount = currentamount + player.getInventory().getItem(slot).getAmount();
                                }
                            }
                        }
                        if (currentamount < currentneededamount) {
                            enoughItems = 1;
                        }
                    }

                    if (enoughItems == 0) {
                        if (emptySlots.size() >= rewards.size()) {
                            clearItems(player.getInventory(), needed, neededamount);
                            giveRewards(player.getInventory(), rewards);
                            databaseWriter.updateChallengeCount(player.getUniqueId(), "VSkyblock_Challenges_" + difficulty, challenge, cache.getCurrentChallengeCount(challenge) + 1);
                            if (!repeat) {
                                ConfigShorts.broadcastChallengeCompleted("ChallengeComplete", player.getName(), challengeName);
                            }
                        } else {
                            ConfigShorts.messagefromString("NotEnoughInventorySpace", player);
                            player.closeInventory();
                        }
                    } else {
                        ConfigShorts.messagefromString("NotEnoughItems", player);
                        player.closeInventory();
                    }
                } else if (plugin.getConfig().getString(difficulty + "." + challengeName + ".Type").equals("islandlevel")) {
                    player.closeInventory();
                    if (!repeat) {
                        Integer neededlevel = plugin.getConfig().getInt(difficulty + "." + challengeName + ".Needed");
                        databaseReader.getislandlevelfromuuid(player.getUniqueId(), new DatabaseReader.CallbackINT() {
                            @Override
                            public void onQueryDone(int result) {
                                if (result >= neededlevel) {
                                    List<String> reward = getRewards(challengeName, difficulty, repeat);
                                    List<Integer> rewardamounts = getRewardAmounts(challengeName, difficulty, repeat);
                                    if (emptySlots.size() >= reward.size()) {
                                        List<ItemStack> rewards = new ArrayList<>();

                                        for (int i = 0; i < reward.size(); i++) {
                                            ItemStack current = new ItemStack(Material.getMaterial(reward.get(i).toUpperCase()), rewardamounts.get(i));
                                            rewards.add(current);
                                        }
                                        giveRewards(player.getInventory(), rewards);
                                        ConfigShorts.broadcastChallengeCompleted("ChallengeComplete", player.getName(), challengeName);
                                        databaseWriter.updateChallengeCount(player.getUniqueId(), "VSkyblock_Challenges_" + difficulty, challenge, 1);
                                    } else {
                                        ConfigShorts.messagefromString("NotEnoughInventorySpace", player);
                                    }
                                } else {
                                    ConfigShorts.messagefromString("IslandLevelNotHighEnough", player);
                                }
                            }
                        });
                    } else {
                        ConfigShorts.messagefromString("ChallengeNotRepeatable", player);
                    }
                } else if (plugin.getConfig().getString(difficulty + "." + challengeName + ".Type").equals("onIsland")) {
                    if (!onIslandDelay.asMap().containsKey(player.getUniqueId())) {
                        player.closeInventory();
                        onIslandDelay.put(player.getUniqueId(), 1);
                        if (!repeat) {
                            List<String> needed = getNeeded(challengeName, difficulty);
                            List<Integer> neededamount = getNeededAmounts(challengeName, difficulty);
                            List<String> reward = getRewards(challengeName, difficulty, repeat);
                            List<Integer> rewardamounts = getRewardAmounts(challengeName, difficulty, repeat);
                            Integer radius = plugin.getConfig().getInt(difficulty + "." + challengeName + ".Radius");
                            ConfigShorts.messagefromString("CheckingIslandForChallenge", player);
                            HashMap<Material, Integer> result = getBlocks(player, radius);
                            int b = 0;
                            for (int i = 0; i < needed.size(); i++) {
                                if (result.containsKey(Material.getMaterial(needed.get(i).toUpperCase()))) {
                                    if (result.get(Material.getMaterial(needed.get(i).toUpperCase())) < (neededamount.get(i))) {
                                        b = b + 1;
                                    }
                                } else {
                                    b = b + 1;
                                }
                            }
                            if (b == 0) {
                                if (emptySlots.size() >= reward.size()) {
                                    List<ItemStack> rewards = new ArrayList<>();
                                    for (int i = 0; i < reward.size(); i++) {
                                        ItemStack current = new ItemStack(Material.getMaterial(reward.get(i).toUpperCase()), rewardamounts.get(i));
                                        rewards.add(current);
                                    }
                                    giveRewards(player.getInventory(), rewards);
                                    ConfigShorts.broadcastChallengeCompleted("ChallengeComplete", player.getName(), challengeName);
                                    databaseWriter.updateChallengeCount(player.getUniqueId(), "VSkyblock_Challenges_" + difficulty, challenge, 1);
                                } else {
                                    ConfigShorts.messagefromString("NotEnoughInventorySpace", player);
                                }
                            } else {
                                ConfigShorts.messagefromString("IslandDoesNotMatchRequirements", player);
                            }
                        } else {
                            ConfigShorts.messagefromString("ChallengeNotRepeatable", player);
                        }
                    } else {
                        ConfigShorts.messagefromString("AlreadyCheckedIsland", player);
                    }
                }
            }
        });
    }

    /**
     * Get needed items for the challenge.
     *
     * @param challenge
     * @param difficulty
     * @return List of needed items for the challenge.
     */
    private List<String> getNeeded(String challenge, String difficulty) {
        List<String> itemsneeded = plugin.getConfig().getStringList( difficulty + "." + challenge + ".Needed");
        List<String> needed = new ArrayList<>();
        for (String current : itemsneeded) {
            if (current.contains(";")) {
                String[] split = current.split(";");
                needed.add(split[0]);
            } else {
                needed.add(current);
            }
        }
        return needed;
    }

    /**
     * Get needed item amounts for the challenge.
     *
     * @param challenge
     * @param difficulty
     * @return List of amounts for the List of needed items for the challenge.
     */
    private List<Integer> getNeededAmounts(String challenge, String difficulty) {
        List<String> itemsneeded = plugin.getConfig().getStringList( difficulty + "." + challenge + ".Needed");
        List<Integer> neededAmounts = new ArrayList<>();
        for (String current : itemsneeded) {
            if (current.contains(";")) {
                String[] split = current.split(";");
                neededAmounts.add(Integer.valueOf(split[1]));
            } else {
                neededAmounts.add(1);
            }
        }
        return neededAmounts;
    }

    /**
     * Get rewards for the challenge.
     *
     * @param challenge
     * @param difficulty
     * @param repeat
     * @return List of rewards for the challenge.
     */
    private List<String> getRewards(String challenge, String difficulty, boolean repeat) {
        List<String> rewardsfromconfig = new ArrayList<>();
        List<String> rewards = new ArrayList<>();
        if (repeat) {
            rewardsfromconfig = plugin.getConfig().getStringList(difficulty + "." + challenge + ".RepeatReward");
        } else {
            rewardsfromconfig = plugin.getConfig().getStringList(difficulty + "." + challenge + ".Reward");
        }

        for (String current : rewardsfromconfig) {
            if (current.contains(";")) {
                String[] split = current.split(";");
                rewards.add(split[0]);
            } else {
                rewards.add(current);
            }
        }
        return rewards;
    }

    /**
     * Get rewarded item amounts for the challenge.
     *
     * @param challenge
     * @param difficulty
     * @param repeat
     * @return List of amounts for the list of rewards for the challenge.
     */
    private List<Integer> getRewardAmounts(String challenge, String difficulty, boolean repeat) {
        List<String> rewardamountsfromconfig = new ArrayList<>();
        List<Integer> rewardamounts = new ArrayList<>();
        if (repeat) {
            rewardamountsfromconfig = plugin.getConfig().getStringList(difficulty + "." + challenge + ".RepeatReward");
        } else {
            rewardamountsfromconfig = plugin.getConfig().getStringList(difficulty + "." + challenge + ".Reward");
        }

        for (String current : rewardamountsfromconfig) {
            if (current.contains(";")) {
                String[] split = current.split(";");
                rewardamounts.add(Integer.valueOf(split[1]));
            } else {
                rewardamounts.add(1);
            }
        }
        return rewardamounts;
    }

    /**
     * Clears the needed items from the players inventory.
     * Loops through the inventory of the player and removes all needed items.
     * @param inv
     * @param items
     * @param amounts
     */
    private void clearItems(Inventory inv, List<String> items, List<Integer> amounts) {
        for (int i = 0; i < items.size(); i++) {
            int amount = amounts.get(i);
            Material current = Material.getMaterial(items.get(i).toUpperCase());
            int x = 0;
            while (x < amount) {
                for (int slot = 0; slot < inv.getSize(); slot++) {
                    ItemStack is = inv.getItem(slot);
                    if (is != null) {
                        if (is.getType().equals(current)) {
                            int newAmount = is.getAmount() - amount;
                            if (newAmount > 0) {
                                is.setAmount(newAmount);
                                x = amount;
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
    }

    /**
     * Gives the rewards for the challenge.
     *
     * @param inv
     * @param items
     */
    private void giveRewards(Inventory inv, List<ItemStack> items) {
        for (ItemStack current : items) {
            inv.addItem(current);
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
}
