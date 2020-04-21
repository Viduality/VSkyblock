package com.github.Viduality.VSkyblock.Listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockBreakListener implements Listener {



    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        if (blockBreakEvent.getBlock().getType().equals(Material.INFESTED_COBBLESTONE)) {
            blockBreakEvent.setDropItems(false);
        }
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.getClickedBlock() != null) {
            if (playerInteractEvent.getClickedBlock().getType().equals(Material.INFESTED_COBBLESTONE)) {
                playerInteractEvent.getClickedBlock().setType(Material.COBBLESTONE);
                CobblestoneGenerator.cobblegen.put(playerInteractEvent.getClickedBlock().getLocation(), System.currentTimeMillis());
            }
        }
    }
}
