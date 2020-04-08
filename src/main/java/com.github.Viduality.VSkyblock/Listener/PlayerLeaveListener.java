package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.Utilitys.Scoreboardmanager;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Objective;

public class PlayerLeaveListener implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();
    private WorldManager wm = new WorldManager();
    private DatabaseWriter databaseWriter = new DatabaseWriter();


    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();
        Location loc = player.getLocation();
        if (Island.playerislands.containsKey(player.getUniqueId())) {
            String island = Island.playerislands.get(player.getUniqueId());
            Island.playerislands.remove(player.getUniqueId());
            if (!Island.playerislands.containsValue(island)) {
                if (!wm.getAutoLoad(island)) {
                    Island.emptyloadedislands.put(island, island);
                    Island.emptyloadedislands.cleanUp();
                }
            }
        }
        if (plugin.scoreboardmanager.doesobjectiveexist("deaths")) {
            if (plugin.scoreboardmanager.hasPlayerScore(player.getName(), "deaths")) {
                int currentcount = plugin.scoreboardmanager.getPlayerScore(player.getName(), "deaths");
                databaseWriter.updateDeathCount(player.getUniqueId(), currentcount);
            }
        }
        saveLocation(player, loc);
    }


    private void saveLocation(Player player, Location loc) {
        databaseWriter.savelastLocation(player.getUniqueId(), loc);
    }
}
