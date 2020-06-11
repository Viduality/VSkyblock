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

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.github.Viduality.VSpecialItems.VSpecialItems;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;


public class InteractBlocker implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();


    @EventHandler
    public void interactEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() != World.Environment.NETHER
                && !player.getWorld().getName().equals(ConfigShorts.getDefConfig().getString("SpawnWorld"))
                && !player.getWorld().getName().equals(Island.playerislands.get(player.getUniqueId()))
                && !player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (event.getClickedBlock() != null
                    && event.getClickedBlock().getType().isInteractable()) {
                event.setCancelled(true);
                ConfigShorts.messagefromString("InteractBlocker", player);
            } else if (event.getAction() == Action.PHYSICAL
                    || event.getItem() == null
                    || !event.getItem().getType().isEdible()) {
                event.setCancelled(true);
            }
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getHand() == EquipmentSlot.HAND) {
                if (event.hasItem()) {
                    if (plugin.getServer().getPluginManager().getPlugin("VSpecialItems") != null) {
                        if (player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer()
                                .has(VSpecialItems.KEY, PersistentDataType.STRING))     {
                            if ((Objects.equals(VSpecialItems.specialItems.get("ChangeBiomeItem").getItemMeta().getPersistentDataContainer().get(VSpecialItems.KEY, PersistentDataType.STRING), player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer()
                                    .get(VSpecialItems.KEY, PersistentDataType.STRING)))) {
                                if (!player.getWorld().getName().equals(Island.playerislands.get(player.getUniqueId()))) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void interactEntityEvent(PlayerInteractEntityEvent playerInteractEntityEvent) {
        handle(playerInteractEntityEvent);
    }

    @EventHandler
    public void interactatEntityEvent(PlayerInteractAtEntityEvent playerInteractAtEntityEvent) {
        handle(playerInteractAtEntityEvent);
    }

    private void handle(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() != World.Environment.NETHER
                && !player.getWorld().getName().equals(Island.playerislands.get(player.getUniqueId()))
                && !player.hasPermission("VSkyblock.IgnoreProtected")) {
            event.setCancelled(true);
        }
    }
}