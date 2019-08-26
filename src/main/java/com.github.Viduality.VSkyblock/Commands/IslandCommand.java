package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.WorldGenerator.Islandmethods;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;


public class IslandCommand implements SubCommand {

    private Islandmethods im = new Islandmethods();
    private WorldManager wm = new WorldManager();

    @Override
    public void execute(DatabaseCache databaseCache) {
        Player player = databaseCache.getPlayer();
        if (databaseCache.getIslandId() != 0) {
            if (!wm.getLoadedWorlds().contains(databaseCache.getIslandname())) {
                wm.loadWorld(databaseCache.getIslandname());
                if (wm.getSpawnLocation(databaseCache.getIslandname()).getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
                    wm.getSpawnLocation(databaseCache.getIslandname()).getBlock().getRelative(BlockFace.DOWN).setType(Material.INFESTED_COBBLESTONE);
                }
            }
            player.teleport(wm.getSpawnLocation(databaseCache.getIslandname()));
        } else {
            if (!Island.isgencooldown.asMap().containsValue(player.getUniqueId())) {
                ConfigShorts.messagefromString("GenerateNewIsland", player);
                player.getEnderChest().clear();
                player.getInventory().clear();
                player.setFoodLevel(20);
                im.createNewIsland(databaseCache.getuuid(), null);
                Island.isgencooldown.put(player.getUniqueId(), player.getUniqueId());
            } else {
                ConfigShorts.custommessagefromString("GenerateCooldown", player, String.valueOf(Island.getisgencooldown()));
            }
        }
    }
}







/*
Player player = databaseCache.getPlayer();
        if (plugin.getMV().getCore().getMVWorldManager().getUnloadedWorlds().contains(databaseCache.getIslandname())) {
            plugin.getMV().getCore().getMVWorldManager().loadWorld(databaseCache.getIslandname());
        }
        if (databaseCache.getIslandId() != 0) {
            if (plugin.getMV().getCore().getMVWorldManager().getMVWorld(databaseCache.getIslandname()).getSpawnLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
                plugin.getMV().getCore().getMVWorldManager().getMVWorld(databaseCache.getIslandname()).getSpawnLocation().getBlock().getRelative(BlockFace.DOWN).setType(Material.INFESTED_COBBLESTONE);
            }
            plugin.getMV().getCore().getMVWorldManager().loadWorld(databaseCache.getIslandname());
            databaseCache.getPlayer().teleport(plugin.getMV().getCore().getMVWorldManager().getMVWorld(databaseCache.getIslandname()).getSpawnLocation());
        } else {
            if (!Island.isgencooldown.asMap().containsValue(player.getUniqueId())) {
                Island.isgencooldown.put(player.getUniqueId(), player.getUniqueId());
                ConfigShorts.messagefromString("GenerateNewIsland", databaseCache.getPlayer());
                databaseCache.getPlayer().getEnderChest().clear();
                databaseCache.getPlayer().getInventory().clear();
                databaseCache.getPlayer().setFoodLevel(20);
                im.createNewIsland(databaseCache.getuuid(), null);
            } else {
                ConfigShorts.custommessagefromString("GenerateCooldown", databaseCache.getPlayer(), String.valueOf(Island.getisgencooldown()));
            }
        }
 */