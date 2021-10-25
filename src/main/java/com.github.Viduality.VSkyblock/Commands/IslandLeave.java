package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;

public class IslandLeave implements SubCommand{

    private final VSkyblock plugin;

    public IslandLeave(VSkyblock plugin) {
        this.plugin = plugin;
    }


    @Override
    public void execute(ExecutionInfo execution) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerInfo playerInfo = execution.getPlayerInfo();
            Player player = playerInfo.getPlayer();
            if (playerInfo.getIslandId() != 0) {
                if (playerInfo.isIslandOwner()) {
                    plugin.getDb().getReader().hasIslandMembers(playerInfo.getIslandId(), result -> {
                        if (!result) {
                            Island.leavemap.put(playerInfo.getUuid(), 1);
                            ConfigShorts.messagefromString("AcceptLeave", player);
                        } else {
                            ConfigShorts.messagefromString("HasIslandMembers", player);
                        }
                    });
                } else {
                    Island.leavemap.put(playerInfo.getUuid(), 1);
                    ConfigShorts.messagefromString("AcceptLeave", player);
                }
            } else {
                ConfigShorts.messagefromString("NoIsland", playerInfo.getPlayer());
            }
        });
    }
}
