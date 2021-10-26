package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.IslandCacheHandler;
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;

/*
 * Command to restart the Island if the player is owner and there are no island members left
 * on the island. Player has to confirm the restart with "island restart confirm".
 */
public class IslandRestart extends PlayerSubCommand {

    public IslandRestart(VSkyblock plugin) {
        super(plugin, "restart");
        registerSubCommand(new IslandRestartConfirm(plugin));
    }

    @Override
    public void execute(CommandSender sender, PlayerInfo playerInfo, String[] args) {
        if (playerInfo.isIslandOwner()) {
            plugin.getDb().getReader().hasIslandMembers(playerInfo.getIslandId(), hasislandmembers -> {
                if (!hasislandmembers) {
                    IslandCacheHandler.restartmap.put(playerInfo.getUuid(), 1);
                    ConfigShorts.messagefromString("ConfirmRestart", sender);
                } else {
                    ConfigShorts.messagefromString("HasIslandMembers", sender);
                }
            });
        } else {
            ConfigShorts.messagefromString("NotIslandOwner", sender);
        }
    }
}
