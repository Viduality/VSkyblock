package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Listener.NetherPortalListener;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

/*
 * Sets the nether portal home point.
 */
public class IslandSetNetherhome extends PlayerSubCommand {

    public IslandSetNetherhome(VSkyblock plugin) {
        super(plugin, "setnetherhome", "setnether");
    }

    @Override
    public void execute(CommandSender sender, PlayerInfo playerInfo, String[] args) {
        Location pendingNetherHome = NetherPortalListener.setNetherHome.getIfPresent(playerInfo.getUuid());
        if (pendingNetherHome != null) {
            plugin.getDb().getWriter().saveNetherHome(playerInfo.getIslandId(), pendingNetherHome);
            NetherPortalListener.setNetherHome.invalidate(playerInfo.getUuid());
            ConfigShorts.messagefromString("SetNetherhomeSuccess", playerInfo.getPlayer());
        } else {
            ConfigShorts.messagefromString("NoPendingNetherSethome", playerInfo.getPlayer());
        }
    }
}
