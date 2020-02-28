package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.EnumSet;
import java.util.Set;

public class PhantomSpawn implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();

    @EventHandler
    public void onBedPlace(BlockPlaceEvent blockPlaceEvent) {
        Player player = blockPlaceEvent.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (beds.contains(blockPlaceEvent.getBlockPlaced().getType())) {
            if (player.getWorld().getName().equals(Island.playerislands.get(uuid))) {
                if (!plugin.getServer().getWorld(Island.playerislands.get(uuid)).getGameRuleValue(GameRule.DO_INSOMNIA)) {
                    plugin.getServer().getWorld(Island.playerislands.get(uuid)).setGameRule(GameRule.DO_INSOMNIA, true);
                }
            }
        }
    }

    private final Set<Material> beds = EnumSet.of(
            Material.BLACK_BED,
            Material.BLUE_BED,
            Material.BROWN_BED,
            Material.CYAN_BED,
            Material.GREEN_BED,
            Material.LIGHT_BLUE_BED,
            Material.LIGHT_GRAY_BED,
            Material.LIME_BED,
            Material.MAGENTA_BED,
            Material.ORANGE_BED,
            Material.PINK_BED,
            Material.PURPLE_BED,
            Material.RED_BED,
            Material.WHITE_BED,
            Material.YELLOW_BED
    );
}
