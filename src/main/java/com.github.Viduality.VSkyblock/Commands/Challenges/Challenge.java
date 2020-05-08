package com.github.Viduality.VSkyblock.Commands.Challenges;

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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Challenge {

    private String challengeName;
    private String mySQLKey;
    private Material shownItem;
    private Difficulty difficulty;
    private ChallengeType challengeType;
    private String description;
    private String neededText;
    private String rewardText;
    private String repeatRewardText;
    private List<ItemStack> rewards;
    private List<ItemStack> repeatRewards;
    private List<ItemStack> neededItems;
    private Integer neededLevel;
    private Integer slot;
    private Integer radius;


    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setMySQLKey(String mySQLKey) {
        this.mySQLKey = mySQLKey;
    }

    public String getMySQLKey() {
        return mySQLKey;
    }

    public void setShownItem(Material shownItem) {
        this.shownItem = shownItem;
    }

    public Material getShownItem() {
        return shownItem;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setChallengeType(ChallengeType challengeType) {
        this.challengeType = challengeType;
    }

    public ChallengeType getChallengeType() {
        return challengeType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setNeededText(String neededText) {
        this.neededText = neededText;
    }

    public String getNeededText() {
        return neededText;
    }

    public void setRewardText(String rewardText) {
        this.rewardText = rewardText;
    }

    public String getRewardText() {
        return rewardText;
    }

    public void setRepeatRewardText(String repeatRewardText) {
        this.repeatRewardText = repeatRewardText;
    }

    public String getRepeatRewardText() {
        return repeatRewardText;
    }

    public void setRewards(List<ItemStack> rewards) {
        this.rewards = rewards;
    }

    public List<ItemStack> getRewards() {
        return rewards;
    }

    public void setRepeatRewards(List<ItemStack> repeatRewards) {
        this.repeatRewards = repeatRewards;
    }

    public List<ItemStack> getRepeatRewards() {
        return repeatRewards;
    }

    public void setNeededItems(List<ItemStack> neededItems) {
        this.neededItems = neededItems;
    }

    public List<ItemStack> getNeededItems() {
        return neededItems;
    }

    public void setNeededLevel(Integer neededLevel) {
        this.neededLevel = neededLevel;
    }

    public Integer getNeededLevel() {
        return neededLevel;
    }

    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    public Integer getSlot() {
        return slot;
    }

    public Integer getInventorySlot() {
        int s = slot;
        while (s > 18) {
            s = s - 18;
        }
        return s - 1;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public Integer getRadius() {
        return radius;
    }

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    public enum ChallengeType {
        onPlayer, onIsland, islandLevel
    }
}
