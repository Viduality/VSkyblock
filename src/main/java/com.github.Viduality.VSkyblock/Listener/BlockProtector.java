package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import java.util.EnumSet;
import java.util.Set;

public class BlockProtector implements Listener {


    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (!(player.getWorld().getEnvironment().equals(World.Environment.NETHER) || player.getWorld().getName().equals(Island.playerislands.get(uuid)))) {
                blockBreakEvent.setCancelled(true);
                ConfigShorts.messagefromString("BlockBreak", player);
            } else {
                if (blockBreakEvent.getBlock().getType().equals(Material.END_PORTAL_FRAME)) {
                    blockBreakEvent.setDropItems(true);
                }
            }
        }
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        Player player = blockPlaceEvent.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (!(player.getWorld().getEnvironment().equals(World.Environment.NETHER) || player.getWorld().getName().equals(Island.playerislands.get(uuid)))) {
                blockPlaceEvent.setCancelled(true);
                ConfigShorts.messagefromString("BlockPlace", player);
            }
        }
    }



    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent playerBucketEmptyEvent) {
        Player player = playerBucketEmptyEvent.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (!(player.getWorld().getEnvironment().equals(World.Environment.NETHER) || player.getWorld().getName().equals(Island.playerislands.get(uuid)))) {
                playerBucketEmptyEvent.setCancelled(true);
                ConfigShorts.messagefromString("BlockPlace", player);
            }
        }
    }



    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent playerBucketFillEvent) {
        Player player = playerBucketFillEvent.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (!(player.getWorld().getEnvironment().equals(World.Environment.NETHER) || player.getWorld().getName().equals(Island.playerislands.get(uuid)))) {
                playerBucketFillEvent.setCancelled(true);
                ConfigShorts.messagefromString("BlockBreak", player);
            }
        }
    }
}
