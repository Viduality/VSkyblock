package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class IslandSethome implements SubCommand {

    private final VSkyblock plugin;

    public IslandSethome(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ExecutionInfo execution) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerInfo playerInfo = execution.getPlayerInfo();
            Player player = playerInfo.getPlayer();
            if (playerInfo.isIslandOwner()) {
                String island = playerInfo.getIslandName();
                if (island.equals(player.getWorld().getName())) {
                    if (player.getFallDistance() == 0) {
                        Location loc = player.getLocation();
                        loc.setY(Math.ceil(loc.getY()));
                        plugin.getWorldManager().setSpawnLocation(loc);
                        plugin.getDb().getWriter().setIslandSpawn(playerInfo.getIslandId(), loc);
                        Island.islandhomes.put(playerInfo.getIslandName(), loc);
                        ConfigShorts.messagefromString("SethomeSuccess", player);
                    } else {
                        ConfigShorts.messagefromString("PlayerFalling", player);
                    }
                } else {
                    ConfigShorts.messagefromString("NotAtPlayersIsland", player);
                }
            } else {
                ConfigShorts.messagefromString("NotIslandOwner", player);
            }
        });
    }
}
