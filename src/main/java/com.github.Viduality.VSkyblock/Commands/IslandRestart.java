package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class IslandRestart implements SubCommand {

    private final VSkyblock plugin;

    public IslandRestart(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ExecutionInfo execution) {
        PlayerInfo playerInfo = execution.getPlayerInfo();
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Player player = plugin.getServer().getPlayer(playerInfo.getPlayer().getUniqueId());
            if (player != null) {
                if (playerInfo.isIslandOwner()) {
                    plugin.getDb().getReader().hasIslandMembers(playerInfo.getIslandId(), hasislandmembers -> {
                        if (!hasislandmembers) {
                            Island.restartmap.put(player.getUniqueId(), 1);
                            ConfigShorts.messagefromString("ConfirmRestart", player);
                        } else {
                            ConfigShorts.messagefromString("HasIslandMembers", player);
                        }
                    });
                } else {
                    ConfigShorts.messagefromString("NotIslandOwner", player);
                }
            }
        });
    }
}
