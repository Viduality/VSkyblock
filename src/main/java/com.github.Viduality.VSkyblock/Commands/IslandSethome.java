package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.IslandCacheHandler;
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*
 * Sets the Islands home if the player is the owner of the island and on the island
 */
public class IslandSethome extends PlayerSubCommand {

    public IslandSethome(VSkyblock plugin) {
        super(plugin, "sethome");
    }

    @Override
    public void execute(CommandSender sender, PlayerInfo playerInfo, String[] args) {
        if (playerInfo.isIslandOwner()) {
            String island = playerInfo.getIslandName();
            Player player = playerInfo.getPlayer();
            if (island.equals(player.getWorld().getName())) {
                if (player.getFallDistance() == 0) {
                    Location loc = player.getLocation();
                    loc.setY(Math.ceil(loc.getY()));
                    plugin.getWorldManager().setSpawnLocation(loc);
                    plugin.getDb().getWriter().setIslandSpawn(playerInfo.getIslandId(), loc);
                    IslandCacheHandler.islandhomes.put(playerInfo.getIslandName(), loc);
                    ConfigShorts.messagefromString("SethomeSuccess", player);
                } else {
                    ConfigShorts.messagefromString("PlayerFalling", player);
                }
            } else {
                ConfigShorts.messagefromString("NotAtPlayersIsland", player);
            }
        } else {
            ConfigShorts.messagefromString("NotIslandOwner", sender);
        }
    }
}
