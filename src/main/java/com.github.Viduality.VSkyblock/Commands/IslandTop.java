package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;

import java.util.List;

public class IslandTop implements SubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();



    @Override
    public void execute(DatabaseCache databaseCache) {
        databaseReader.getHighestIslands(new DatabaseReader.CallbackList() {
            @Override
            public void onQueryDone(List<String> result) {
                String message = ChatColor.AQUA + "Top Islands:" + "\n";
                for (int i = 0; i < result.size(); i++) {
                    int rank = i + 1;
                    message = message + ChatColor.YELLOW + rank + ".: " + ChatColor.RESET + result.get(i) + "\n";
                }
                databaseCache.getPlayer().sendMessage(message);
            }
        });
    }
}
