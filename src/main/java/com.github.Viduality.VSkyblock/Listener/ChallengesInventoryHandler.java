package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.ChallengesHandler;
import com.github.Viduality.VSkyblock.Commands.Challenges.CreateChallengesInventory;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;


public class ChallengesInventoryHandler implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();
    private CreateChallengesInventory cc = new CreateChallengesInventory();
    private ChallengesHandler cH = new ChallengesHandler();


    @EventHandler
    public void cinvHandler(InventoryClickEvent inventoryClickEvent) {
        ConfigShorts.loadChallengesConfig();
        if (inventoryClickEvent.getView().getTitle().equalsIgnoreCase("Challenges " + plugin.getConfig().getString("Difficulty.Easy")) ||
                inventoryClickEvent.getView().getTitle().equalsIgnoreCase("Challenges " + plugin.getConfig().getString("Difficulty.Medium")) ||
                inventoryClickEvent.getView().getTitle().equalsIgnoreCase("Challenges " + plugin.getConfig().getString("Difficulty.Hard"))) {
            inventoryClickEvent.setCancelled(true);
            if (inventoryClickEvent.getCurrentItem() != null) {
                if (inventoryClickEvent.getRawSlot() < 27) {
                    if (inventoryClickEvent.getSlot() == 18 && !inventoryClickEvent.getCurrentItem().getType().equals(Material.AIR) && !inventoryClickEvent.getCurrentItem().getType().equals(Material.BARRIER)) {
                        getpreviousChallengeinv(inventoryClickEvent.getView().getTitle(), (Player) inventoryClickEvent.getWhoClicked());
                    }
                    if (inventoryClickEvent.getSlot() == 26 && !inventoryClickEvent.getCurrentItem().getType().equals(Material.AIR) && !inventoryClickEvent.getCurrentItem().getType().equals(Material.BARRIER)) {
                        getnextChallengeinv(inventoryClickEvent.getView().getTitle(), (Player) inventoryClickEvent.getWhoClicked());
                    }
                    if (inventoryClickEvent.getSlot() >= 0 && inventoryClickEvent.getSlot() <= 17 ) {
                        if (!inventoryClickEvent.getCurrentItem().getType().equals(Material.AIR)) {
                            int challenge = inventoryClickEvent.getSlot() + 1;
                            String challengewithColors = inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName();
                            if (inventoryClickEvent.getView().getTitle().equals("Challenges " + plugin.getConfig().getString("Difficulty.Easy"))) {
                                cH.checkChallenge(challenge, getChallenge(challengewithColors), "Easy", (Player) inventoryClickEvent.getWhoClicked());
                            } else if (inventoryClickEvent.getView().getTitle().equalsIgnoreCase("Challenges " + plugin.getConfig().getString("Difficulty.Medium"))) {
                                cH.checkChallenge(challenge, getChallenge(challengewithColors), "Medium", (Player) inventoryClickEvent.getWhoClicked());
                            } else if (inventoryClickEvent.getView().getTitle().equalsIgnoreCase("Challenges " + plugin.getConfig().getString("Difficulty.Hard"))) {
                                cH.checkChallenge(challenge, getChallenge(challengewithColors), "Hard", (Player) inventoryClickEvent.getWhoClicked());
                            }
                        }
                    }
                }
            }
        }
        ConfigShorts.loaddefConfig();
    }

    /**
     * Deletes color codes from item names.
     *
     * @param challengewithColors
     * @return String
     */
    private String getChallenge(String challengewithColors) {
        String challenge = challengewithColors;
        for (int i = 1; i > 0; i++) {
            if (challenge.contains("§")) {
                challenge = challenge.substring(2);
            } else {
                i = -1;
            }
        }
        return challenge;
    }

    @EventHandler
    public void cinvHandler2(InventoryDragEvent inventoryDragEvent) {
        ConfigShorts.loadChallengesConfig();
        if (inventoryDragEvent.getView().getTitle().equalsIgnoreCase("Challenges " + plugin.getConfig().getString("Difficulty.Easy")) ||
                inventoryDragEvent.getView().getTitle().equalsIgnoreCase("Challenges " + plugin.getConfig().getString("Difficulty.Medium")) ||
                inventoryDragEvent.getView().getTitle().equalsIgnoreCase("Challenges " + plugin.getConfig().getString("Difficulty.Hard"))) {
            inventoryDragEvent.setCancelled(true);
        }
        ConfigShorts.loaddefConfig();
    }

    /**
     * Creates the next site of the challenges inventory.
     * @param currentInv
     * @param player
     */
    private void getnextChallengeinv(String currentInv, Player player) {
        if (currentInv.equals("Challenges " + plugin.getConfig().getString("Difficulty.Easy"))) {
            cc.createChallenges(player, "Medium");
        } else {
            cc.createChallenges(player, "Hard");
        }
    }

    /**
     * Creates the previous site of the challenges inventory.
     * @param currentInv
     * @param player
     */
    private void getpreviousChallengeinv(String currentInv, Player player) {
        if (currentInv.equals("Challenges " + plugin.getConfig().getString("Difficulty.Hard"))) {
            cc.createChallenges(player, "Medium");
        } else {
            cc.createChallenges(player, "Easy");
        }
    }
}
