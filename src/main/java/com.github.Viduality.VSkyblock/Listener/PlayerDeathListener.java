package com.github.Viduality.VSkyblock.Listener;

/*
 * VSkyblock
 * Copyright (C) 2020  Viduality
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.TeleportHandler;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.UUID;

public class PlayerDeathListener implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();
    private WorldManager wm = new WorldManager();


    @EventHandler
    public void playerRespawnListener(PlayerRespawnEvent playerRespawnEvent) {
        Player player = playerRespawnEvent.getPlayer();
        UUID uuid = player.getUniqueId();
        if (Island.playerislands.containsKey(uuid)) {
            if (player.getBedSpawnLocation() == null) {
                playerRespawnEvent.setRespawnLocation(Island.islandhomes.get(Island.playerislands.get(uuid)));
                player.setCanCollide(true);
            }
        }
    }

    @EventHandler
    public void playerDeathListener(PlayerDeathEvent playerDeathEvent) {
        Player player = playerDeathEvent.getEntity();
        if (TeleportHandler.teleportQueue.containsKey(player)) {
            TeleportHandler.teleportQueue.remove(player);
            ConfigShorts.messagefromString("StoppedTeleportation", player);
        }
    }
}
