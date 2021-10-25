package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Listener.NetherPortalListener;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;

public class IslandSetNetherhome implements SubCommand {

    private final VSkyblock plugin;

    public IslandSetNetherhome(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(ExecutionInfo execution) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerInfo playerInfo = execution.getPlayerInfo();
            if (NetherPortalListener.setNetherHome.asMap().containsKey(playerInfo.getUuid())) {
                plugin.getDb().getWriter().saveNetherHome(playerInfo.getIslandId(), NetherPortalListener.setNetherHome.asMap().get(playerInfo.getUuid()));
                NetherPortalListener.setNetherHome.asMap().remove(playerInfo.getUuid());
                ConfigShorts.messagefromString("SetNetherhomeSuccess", playerInfo.getPlayer());
            } else {
                ConfigShorts.messagefromString("NoPendingNetherSethome", playerInfo.getPlayer());
            }
        });
    }
}
