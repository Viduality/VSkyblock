package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseWriter;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.github.Viduality.VSkyblock.WorldGenerator.Islandmethods;
import org.bukkit.entity.Player;

public class IslandRestartConfirm implements SubCommand{

    private VSkyblock plugin = VSkyblock.getInstance();
    private Islandmethods islandmethods = new Islandmethods();
    private DatabaseWriter databaseWriter = new DatabaseWriter();


    @Override
    public void execute(DatabaseCache databaseCache) {
        Player player = databaseCache.getPlayer();
        if (Island.restartmap.asMap().containsKey(player.getUniqueId())) {
            if (!Island.isgencooldown.asMap().containsValue(player.getUniqueId())) {
                Island.isgencooldown.put(player.getUniqueId(), player.getUniqueId());
                ConfigShorts.messagefromString("GenerateNewIsland", player);
                islandmethods.createNewIsland(databaseCache.getuuid(), databaseCache.getIslandname());
                databaseWriter.resetChallenges(databaseCache.getuuid());
                player.getInventory().clear();
                player.getEnderChest().clear();
            } else {
                ConfigShorts.custommessagefromString("GenerateCooldown", databaseCache.getPlayer(), String.valueOf(Island.getisgencooldown()));
            }
        } else {
            ConfigShorts.messagefromString("RestartFirst", player);
        }
    }
}
