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

import com.github.Viduality.VSkyblock.ChallengesHandler;
import com.github.Viduality.VSkyblock.Commands.Challenges.Challenge;
import com.github.Viduality.VSkyblock.Commands.Challenges.Challenges;
import com.github.Viduality.VSkyblock.Commands.Challenges.CreateChallengesInventory;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;


public class ChallengesInventoryHandler implements Listener {

    private CreateChallengesInventory cc = new CreateChallengesInventory();
    private ChallengesHandler cH = new ChallengesHandler();


    @EventHandler
    public void cinvHandler(InventoryClickEvent inventoryClickEvent) {
        if (inventoryClickEvent.getView().getTitle().equalsIgnoreCase("Challenges " + ConfigShorts.getChallengesConfig().getString("Difficulty.Easy")) ||
                inventoryClickEvent.getView().getTitle().equalsIgnoreCase("Challenges " + ConfigShorts.getChallengesConfig().getString("Difficulty.Medium")) ||
                inventoryClickEvent.getView().getTitle().equalsIgnoreCase("Challenges " + ConfigShorts.getChallengesConfig().getString("Difficulty.Hard"))) {
            inventoryClickEvent.setCancelled(true);
            if (inventoryClickEvent.getCurrentItem() != null) {
                if (inventoryClickEvent.getRawSlot() < 27) {
                    if (inventoryClickEvent.getSlot() == 18 && !inventoryClickEvent.getCurrentItem().getType().equals(Material.AIR) && !inventoryClickEvent.getCurrentItem().getType().equals(Material.BARRIER)) {
                        getpreviousChallengeinv(inventoryClickEvent.getView(), (Player) inventoryClickEvent.getWhoClicked(), 1, true);
                    }
                    if (inventoryClickEvent.getSlot() == 26 && !inventoryClickEvent.getCurrentItem().getType().equals(Material.AIR) && !inventoryClickEvent.getCurrentItem().getType().equals(Material.BARRIER)) {
                        getnextChallengeinv(inventoryClickEvent.getView(), (Player) inventoryClickEvent.getWhoClicked(), 1, true);
                    }
                    if (inventoryClickEvent.getSlot() == 21 && !inventoryClickEvent.getCurrentItem().getType().equals(Material.AIR)) {
                        getpreviousChallengeinv(inventoryClickEvent.getView(), (Player) inventoryClickEvent.getWhoClicked(), getPreviousSite(inventoryClickEvent.getView()), false);
                    }
                    if (inventoryClickEvent.getSlot() == 23 && !inventoryClickEvent.getCurrentItem().getType().equals(Material.AIR)) {
                        getnextChallengeinv(inventoryClickEvent.getView(), (Player) inventoryClickEvent.getWhoClicked(), getNextSite(inventoryClickEvent.getView()), false);
                    }
                    if (inventoryClickEvent.getSlot() >= 0 && inventoryClickEvent.getSlot() <= 17 ) {
                        if (!inventoryClickEvent.getCurrentItem().getType().equals(Material.AIR)) {
                            String challenge = getChallengeName(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName());
                            if (inventoryClickEvent.getView().getTitle().equals("Challenges " + ConfigShorts.getChallengesConfig().getString("Difficulty.Easy"))) {
                                cH.checkChallenge(Challenges.challengesEasy.get(challenge), (Player) inventoryClickEvent.getWhoClicked(), inventoryClickEvent.getClickedInventory(), inventoryClickEvent.getSlot());
                            } else if (inventoryClickEvent.getView().getTitle().equalsIgnoreCase("Challenges " + ConfigShorts.getChallengesConfig().getString("Difficulty.Medium"))) {
                                cH.checkChallenge(Challenges.challengesMedium.get(challenge), (Player) inventoryClickEvent.getWhoClicked(), inventoryClickEvent.getClickedInventory(), inventoryClickEvent.getSlot());
                            } else if (inventoryClickEvent.getView().getTitle().equalsIgnoreCase("Challenges " + ConfigShorts.getChallengesConfig().getString("Difficulty.Hard"))) {
                                cH.checkChallenge(Challenges.challengesHard.get(challenge), (Player) inventoryClickEvent.getWhoClicked(), inventoryClickEvent.getClickedInventory(), inventoryClickEvent.getSlot());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the next site number.
     * @param inventoryView
     * @return Integer site number
     */
    private int getNextSite(InventoryView inventoryView) {
        if (inventoryView.getTopInventory().getItem(22) != null) {
            return inventoryView.getTopInventory().getItem(22).getAmount() + 1;
        } else {
            return 1;
        }
    }

    /**
     * Returns the previous site number.
     * @param inventoryView
     * @return Integer site number
     */
    private int getPreviousSite(InventoryView inventoryView) {
        if (inventoryView.getTopInventory().getItem(22) != null) {
            return inventoryView.getTopInventory().getItem(22).getAmount() - 1;
        } else {
            return 1;
        }
    }

    /**
     * Deletes color codes from item names.
     *
     * @param challengewithColors
     * @return String
     */
    private String getChallengeName(String challengewithColors) {
        String challenge = challengewithColors;
        while (challenge.contains("§")) {
            challenge = challenge.substring(2);
        }
        return challenge;
    }

    @EventHandler
    public void cinvHandler2(InventoryDragEvent inventoryDragEvent) {
        if (inventoryDragEvent.getView().getTitle().equalsIgnoreCase("Challenges " + ConfigShorts.getChallengesConfig().getString("Difficulty.Easy")) ||
                inventoryDragEvent.getView().getTitle().equalsIgnoreCase("Challenges " + ConfigShorts.getChallengesConfig().getString("Difficulty.Medium")) ||
                inventoryDragEvent.getView().getTitle().equalsIgnoreCase("Challenges " + ConfigShorts.getChallengesConfig().getString("Difficulty.Hard"))) {
            inventoryDragEvent.setCancelled(true);
        }
    }

    /**
     * Creates the next site of the challenges inventory.
     * @param currentInv
     * @param player
     */
    private void getnextChallengeinv(InventoryView currentInv, Player player, int site, boolean switchDifficulty) {
        if (currentInv.getTitle().equals("Challenges " + ConfigShorts.getChallengesConfig().getString("Difficulty.Easy"))) {
            if (switchDifficulty) {
                cc.createChallenges(player, Challenge.Difficulty.MEDIUM, site);
            } else {
                cc.createChallenges(player, Challenge.Difficulty.EASY, site);
            }
        } else if (currentInv.getTitle().equals("Challenges " + ConfigShorts.getChallengesConfig().getString("Difficulty.Medium"))) {
            if (switchDifficulty) {
                cc.createChallenges(player, Challenge.Difficulty.HARD, site);
            } else {
                cc.createChallenges(player, Challenge.Difficulty.MEDIUM, site);
            }
        } else {
            cc.createChallenges(player, Challenge.Difficulty.HARD, site);
        }
    }

    /**
     * Creates the previous site of the challenges inventory.
     * @param currentInv
     * @param player
     */
    private void getpreviousChallengeinv(InventoryView currentInv, Player player, int site, boolean switchDifficulty) {
        if (currentInv.getTitle().equals("Challenges " + ConfigShorts.getChallengesConfig().getString("Difficulty.Hard"))) {
            if (switchDifficulty) {
                cc.createChallenges(player, Challenge.Difficulty.MEDIUM, site);
            } else {
                cc.createChallenges(player, Challenge.Difficulty.MEDIUM, site);
            }
        } else if (currentInv.getTitle().equals("Challenges " + ConfigShorts.getChallengesConfig().getString("Difficulty.Medium"))) {
            if (switchDifficulty) {
                cc.createChallenges(player, Challenge.Difficulty.EASY, site);
            } else {
                cc.createChallenges(player, Challenge.Difficulty.MEDIUM, site);
            }
        } else {
            cc.createChallenges(player, Challenge.Difficulty.EASY, site);
        }
    }
}
