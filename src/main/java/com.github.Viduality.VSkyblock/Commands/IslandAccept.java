package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.*;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IslandAccept implements SubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private DatabaseWriter databaseWriter = new DatabaseWriter();
    private WorldManager wm = new WorldManager();



    @Override
    public void execute(DatabaseCache databaseCache) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                Player player = databaseCache.getPlayer();
                if (Island.invitemap.asMap().containsKey(UUID.fromString(databaseCache.getuuid()))) {
                    String newmemberuuid = databaseCache.getuuid();
                    String islandowneruuid = String.valueOf(Island.invitemap.asMap().get(UUID.fromString(databaseCache.getuuid())));
                    databaseReader.getislandidfromplayer(islandowneruuid, new DatabaseReader.CallbackINT() {
                        @Override
                        public void onQueryDone(int result) {
                            int islandid = result;
                            String newisland = "VSkyblockIsland_" + islandid;
                            if (databaseCache.isIslandowner()) {
                                databaseReader.hasislandmembers(databaseCache.getIslandId(), new DatabaseReader.CallbackBoolean() {
                                    @Override
                                    public void onQueryDone(boolean result) {
                                        if (!result) {

                                            player.getInventory().clear();
                                            player.getEnderChest().clear();
                                            player.teleport(wm.getSpawnLocation(newisland));
                                            databaseWriter.updatePlayersIsland(newmemberuuid, islandid, false);
                                            databaseWriter.resetChallenges(newmemberuuid);
                                            Island.invitemap.asMap().remove(player.getUniqueId());
                                            Island.playerislands.put(player.getUniqueId().toString(), newisland);
                                            Island.isjoincooldown.put(player.getUniqueId(), player.getUniqueId());
                                            wm.unloadWorld(databaseCache.getIslandname());
                                            databaseWriter.updateDeathCount(newmemberuuid, 0);
                                            plugin.scoreboardmanager.updatePlayerScore(player.getName(), "deaths", 0);

                                        } else {
                                            ConfigShorts.messagefromString("HasIslandMembers", player);
                                        }
                                    }
                                });
                            } else {
                                player.getInventory().clear();
                                player.getEnderChest().clear();
                                player.teleport(wm.getSpawnLocation(newisland));
                                databaseWriter.updatePlayersIsland(newmemberuuid, islandid, false);
                                databaseWriter.resetChallenges(newmemberuuid);
                                Island.invitemap.asMap().remove(player.getUniqueId());
                                Island.playerislands.put(player.getUniqueId().toString(), newisland);
                                Island.isjoincooldown.put(player.getUniqueId(), player.getUniqueId());
                                if (databaseCache.getIslandname() != null) {
                                    if (!Island.playerislands.containsValue(databaseCache.getIslandname())) {
                                        wm.unloadWorld(databaseCache.getIslandname());
                                    }
                                }
                            }
                        }
                    });
                } else {
                    ConfigShorts.messagefromString("NoPendingInvite", player);
                }
            }
        });
    }
}
