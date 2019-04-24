package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();


    @EventHandler
    public void playerRespawnListener(PlayerRespawnEvent playerRespawnEvent) {
        Player player = playerRespawnEvent.getPlayer();
        String uiud = player.getUniqueId().toString();
        if (Island.playerislands.containsKey(uiud)) {
            if (player.getBedSpawnLocation() == null) {
                playerRespawnEvent.setRespawnLocation(plugin.getMV().getCore().getMVWorldManager().getMVWorld(Island.playerislands.get(uiud)).getSpawnLocation());
            }
        }
    }
}
