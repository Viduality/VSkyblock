package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Listener.NetherPortalListener;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;

public class IslandSetNetherhome implements SubCommand {

    private final VSkyblock plugin;

    public IslandSetNetherhome(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(DatabaseCache databaseCache) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (NetherPortalListener.setNetherHome.asMap().containsKey(databaseCache.getUuid())) {
                plugin.getDb().getWriter().saveNetherHome(databaseCache.getIslandId(), NetherPortalListener.setNetherHome.asMap().get(databaseCache.getUuid()));
                NetherPortalListener.setNetherHome.asMap().remove(databaseCache.getUuid());
                ConfigShorts.messagefromString("SetNetherhomeSuccess", databaseCache.getPlayer());
            } else {
                ConfigShorts.messagefromString("NoPendingNetherSethome", databaseCache.getPlayer());
            }
        });
    }
}
