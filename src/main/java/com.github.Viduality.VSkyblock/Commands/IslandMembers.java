package com.github.Viduality.VSkyblock.Commands;


import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.VSkyblock;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class IslandMembers implements SubCommand {

    private final VSkyblock plugin;

    public IslandMembers(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(DatabaseCache databaseCache) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            Player player = databaseCache.getPlayer();
            if (databaseCache.getIslandId() != 0) {
                plugin.getDb().getReader().hasislandmembers(databaseCache.getIslandId(), hasislandmembers -> {
                    if (hasislandmembers) {

                        plugin.getDb().getReader().getIslandMembers(databaseCache.getIslandId(), result -> {

                            StringBuilder memberList = null;
                            for (String member : result) {
                                if (memberList == null) {
                                    memberList = new StringBuilder(member);
                                } else {
                                    memberList.append(", ").append(member);
                                }
                            }
                            player.sendMessage(ChatColor.AQUA + "Players: " + ChatColor.RESET + memberList);
                        });
                    } else {
                        ConfigShorts.messagefromString("OnlyMember", player);
                    }
                });
            } else {
                ConfigShorts.messagefromString("NoIsland", player);
            }
        });
    }
}
