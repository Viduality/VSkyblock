package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {

    private WorldManager wm = new WorldManager();


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
            }
        }
    }
}
