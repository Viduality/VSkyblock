package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Listener.NetherPortalListener;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;

public class IslandSetNetherhome implements SubCommand {

    private final VSkyblock plugin = VSkyblock.getInstance();
    private final DatabaseWriter databaseWriter = new DatabaseWriter();


    @Override
    public void execute(DatabaseCache databaseCache) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (NetherPortalListener.setNetherHome.asMap().containsKey(databaseCache.getUuid())) {
                databaseWriter.saveNetherHome(databaseCache.getIslandId(), NetherPortalListener.setNetherHome.asMap().get(databaseCache.getUuid()));
                NetherPortalListener.setNetherHome.asMap().remove(databaseCache.getUuid());
                ConfigShorts.messagefromString("SetNetherhomeSuccess", databaseCache.getPlayer());
            } else {
                ConfigShorts.messagefromString("NoPendingNetherSethome", databaseCache.getPlayer());
            }
        });
    }
}
