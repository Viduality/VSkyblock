package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.IslandCacheHandler;
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;

/*
 * Lets the player leave his current island. Deletes it if he is the owner and alone on it.
 */
public class IslandLeave extends PlayerSubCommand {

    public IslandLeave(VSkyblock plugin) {
        super(plugin, "leave");
        registerSubCommand(new IslandLeaveConfirm(plugin));
    }


    @Override
    public void execute(CommandSender sender, PlayerInfo playerInfo, String[] args) {
        if (playerInfo.getIslandId() != 0) {
            if (playerInfo.isIslandOwner()) {
                plugin.getDb().getReader().hasIslandMembers(playerInfo.getIslandId(), result -> {
                    if (!result) {
                        IslandCacheHandler.leavemap.put(playerInfo.getUuid(), 1);
                        ConfigShorts.messagefromString("AcceptLeave", sender);
                    } else {
                        ConfigShorts.messagefromString("HasIslandMembers", sender);
                    }
                });
            } else {
                IslandCacheHandler.leavemap.put(playerInfo.getUuid(), 1);
                ConfigShorts.messagefromString("AcceptLeave", sender);
            }
        } else {
            ConfigShorts.messagefromString("NoIsland", sender);
        }
    }
}
