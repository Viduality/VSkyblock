package com.github.Viduality.VSkyblock.Commands;


import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/*
 * Lists all members of your island.
 */
public class IslandMembers extends PlayerSubCommand {

    public IslandMembers(VSkyblock plugin) {
        super(plugin, "members");
    }

    @Override
    public void execute(CommandSender sender, PlayerInfo playerInfo, String[] args) {
        if (playerInfo.getIslandId() != 0) {
            plugin.getDb().getReader().hasIslandMembers(playerInfo.getIslandId(), hasislandmembers -> {
                if (hasislandmembers) {

                    plugin.getDb().getReader().getIslandMembers(playerInfo.getIslandId(), result -> {

                        StringBuilder memberList = null;
                        for (String member : result) {
                            if (memberList == null) {
                                memberList = new StringBuilder(member);
                            } else {
                                memberList.append(", ").append(member);
                            }
                        }
                        sender.sendMessage(ChatColor.AQUA + "Players: " + ChatColor.RESET + memberList);
                    });
                } else {
                    ConfigShorts.messagefromString("OnlyMember", sender);
                }
            });
        } else {
            ConfigShorts.messagefromString("NoIsland", sender);
        }
    }
}
