package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();
    private WorldManager wm = new WorldManager();


    @EventHandler
    public void playerRespawnListener(PlayerRespawnEvent playerRespawnEvent) {
        Player player = playerRespawnEvent.getPlayer();
        String uuid = player.getUniqueId().toString();
        if (Island.playerislands.containsKey(uuid)) {
            if (player.getBedSpawnLocation() == null) {
                playerRespawnEvent.setRespawnLocation(wm.getSpawnLocation(plugin.getServer().getWorld(Island.playerislands.get(uuid)).getName()));
            }
        }
    }
}
