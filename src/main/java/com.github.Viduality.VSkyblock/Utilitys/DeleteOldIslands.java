package com.github.Viduality.VSkyblock.Utilitys;


import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;

import java.util.Calendar;
import java.util.List;

public class DeleteOldIslands implements Runnable {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private DatabaseWriter databaseWriter = new DatabaseWriter();


    @Override
    public void run() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                int rltime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                if (rltime == getCleanUpTime()) {
                    startCleanUp();
                }
            }
        }, 10, 72000);
    }

    /**
     * Starts the whole cleanup.
     */
    private void startCleanUp() {
        plugin.getServer().broadcastMessage(ChatColor.RED + "Cleaning up Worlds, could lag for a short time!");
        databaseReader.getemptyIslands(new DatabaseReader.CallbackList() {
            @Override
            public void onQueryDone(List<String> result) {
                for (String currentIsland : result) {
                    plugin.getMV().getCore().getMVWorldManager().loadWorld(currentIsland);
                    plugin.getMV().getCore().getMVWorldManager().deleteWorld(currentIsland, true, true);
                    databaseWriter.deleteIsland(currentIsland);
                }
                plugin.getServer().broadcastMessage(ChatColor.GREEN + "Cleaning up done! :)");
            }
        });
    }

    /**
     * Gets the hour of the day when the cleanup happens.
     * @return Integer hour.
     */
    private Integer getCleanUpTime() {
        String cleanUpTimeString = plugin.getConfig().getString("CleanUpTime");
        Integer cleanUpTime = 3;
        if (!(cleanUpTimeString == null)) {
            if (isInt(cleanUpTimeString)) {
                Integer cleanUpTime1 = Integer.parseInt(cleanUpTimeString);
                if (cleanUpTime1 > -1 && cleanUpTime1 < 24) {
                    cleanUpTime = cleanUpTime1;
                }
            }
        }
        return cleanUpTime;
    }

    /**
     * Checks if a string is from type Integer
     * @param s
     * @return boolean
     */
    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
