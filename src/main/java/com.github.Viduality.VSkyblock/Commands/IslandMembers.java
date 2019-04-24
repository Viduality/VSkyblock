package com.github.Viduality.VSkyblock.Commands;


import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.VSkyblock;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;


public class IslandMembers implements SubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();



    @Override
    public void execute(DatabaseCache databaseCache) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Player player = databaseCache.getPlayer();
                if (databaseCache.getIslandId() != 0) {
                    databaseReader.hasislandmembers(databaseCache.getIslandId(), new DatabaseReader.CallbackBoolean() {
                        @Override
                        public void onQueryDone(boolean hasislandmembers) {
                            if (hasislandmembers) {

                                databaseReader.getIslandMembers(databaseCache.getIslandId(), new DatabaseReader.CallbackList() {
                                    @Override
                                    public void onQueryDone(List<String> result) {

                                        StringBuilder memberList = null;
                                        for (String member : result) {
                                            if (memberList == null) {
                                                memberList = new StringBuilder(member);
                                            } else {
                                                memberList.append(", ").append(member);
                                            }
                                        }
                                        player.sendMessage(ChatColor.AQUA + "Players: " + ChatColor.RESET + memberList);
                                    }
                                });
                            } else {
                                ConfigShorts.messagefromString("OnlyMember", player);
                            }
                        }
                    });
                } else {
                    ConfigShorts.messagefromString("NoIsland", player);
                }
            }
        });
    }
}
