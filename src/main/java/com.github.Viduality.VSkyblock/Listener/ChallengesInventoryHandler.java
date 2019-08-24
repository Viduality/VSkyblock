package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.ChallengesHandler;
import com.github.Viduality.VSkyblock.Commands.Challenges.CreateChallengesInventory;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.IOException;

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
                        if (inventoryClickEvent.getView().getTitle().equals("Challenges " + plugin.getConfig().getString("Difficulty.Easy"))) {
                            cH.checkChallenge(challenge, inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName(), "Easy", (Player) inventoryClickEvent.getWhoClicked());
                        } else if (inventoryClickEvent.getView().getTitle().equalsIgnoreCase("Challenges " + plugin.getConfig().getString("Difficulty.Medium"))) {
                            cH.checkChallenge(challenge, inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName(), "Medium", (Player) inventoryClickEvent.getWhoClicked());
                        } else if (inventoryClickEvent.getView().getTitle().equalsIgnoreCase("Challenges " + plugin.getConfig().getString("Difficulty.Hard"))) {
                            cH.checkChallenge(challenge, inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName(), "Hard", (Player) inventoryClickEvent.getWhoClicked());
                        }
                    }
                }
            }
        }
        ConfigShorts.loaddefConfig();
    }

    private void getnextChallengeinv(String currentInv, Player player) {
        if (currentInv.equals("Challenges " + plugin.getConfig().getString("Difficulty.Easy"))) {
            cc.createChallenges(player, "Medium");
        } else {
            cc.createChallenges(player, "Hard");
        }
    }

    private void getpreviousChallengeinv(String currentInv, Player player) {
        if (currentInv.equals("Challenges " + plugin.getConfig().getString("Difficulty.Hard"))) {
            cc.createChallenges(player, "Medium");
        } else {
            cc.createChallenges(player, "Easy");
        }
    }
}
