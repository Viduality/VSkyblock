package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;

public class IslandTop implements SubCommand {

    private final VSkyblock plugin;

    public IslandTop(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(DatabaseCache databaseCache) {
        plugin.getDb().getReader().getHighestIslands(result -> {
            String message = "§9§l-----Top Islands-----" + "\n";
            for (int i = 0; i < result.size(); i++) {
                int rank = i + 1;
                message = message + ChatColor.GOLD + rank + ".: " + ChatColor.RESET + result.get(i) + "\n";
            }
            databaseCache.getPlayer().sendMessage(message);
        });
    }
}
