package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.WorldGenerator.Islandmethods;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;


public class IslandCommand implements SubCommand {

    private Islandmethods im = new Islandmethods();
    private WorldManager wm = new WorldManager();

    @Override
    public void execute(DatabaseCache databaseCache) {
        Player player = databaseCache.getPlayer();
        if (databaseCache.getIslandId() != 0) {
            boolean teleport = true;
            if (!ConfigShorts.getDefConfig().getBoolean("SaveWithIslandCommand")) {
                if (player.getFallDistance() > 2) {
                    teleport = false;
                    ConfigShorts.messagefromString("PlayerFalling", player);
                }
            }
            if (!ConfigShorts.getDefConfig().getBoolean("SaveWithIslandCommandLava")) {
                if (player.getLocation().getBlock().getType().equals(Material.LAVA)) {
                    teleport = false;
                    ConfigShorts.messagefromString("PlayerInLava", player);
                }
            }
            if (teleport) {
                if (!wm.getLoadedWorlds().contains(databaseCache.getIslandname())) {
                    if (!wm.loadWorld(databaseCache.getIslandname())) {
                        ConfigShorts.custommessagefromString("WorldFailedToLoad", player, databaseCache.getIslandname());
                        return;
                    }
                }
                Location islandHome = Island.islandhomes.get(databaseCache.getIslandname());
                if (islandHome != null) {
                    islandHome.getWorld().getChunkAtAsync(islandHome).whenComplete((c, e) -> {
                       if (e != null) {
                           e.printStackTrace();
                       }
                       if (c != null) {
                           Block below = islandHome.getBlock().getRelative(BlockFace.DOWN);
                           if (below.getType() == Material.AIR) {
                               below.setType(Material.INFESTED_COBBLESTONE);
                           }
                           player.teleport(islandHome);
                       } else {
                           ConfigShorts.custommessagefromString("WorldFailedToLoad", player, databaseCache.getIslandname());
                       }
                    });
                }
            }
        } else {
            if (!Island.isgencooldown.asMap().containsValue(player.getUniqueId())) {
                ConfigShorts.messagefromString("GenerateNewIsland", player);
                player.getEnderChest().clear();
                player.getInventory().clear();
                player.setTotalExperience(0);
                player.setExp(0);
                player.setFoodLevel(20);
                im.createNewIsland(databaseCache.getUuid(), null);
                Island.isgencooldown.put(player.getUniqueId(), player.getUniqueId());
            } else {
                ConfigShorts.custommessagefromString("GenerateCooldown", player, String.valueOf(Island.getisgencooldown()));
            }
        }
    }
}