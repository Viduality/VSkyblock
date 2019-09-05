package com.github.Viduality.VSkyblock.Utilitys;


import com.github.Viduality.VSkyblock.VSkyblock;

import java.util.Calendar;
import java.util.List;

public class DeleteOldIslands implements Runnable {

    private VSkyblock plugin = VSkyblock.getInstance();
    private DatabaseReader databaseReader = new DatabaseReader();
    private DatabaseWriter databaseWriter = new DatabaseWriter();
    private WorldManager wm = new WorldManager();


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
        ConfigShorts.broadcastfromString("CleaningUpOldIslands");
        databaseReader.getemptyIslands(new DatabaseReader.CallbackList() {
            @Override
            public void onQueryDone(List<String> result) {
                for (String currentIsland : result) {
                    boolean deleted = wm.deleteWorld(currentIsland);
                    if (deleted) {
                        databaseWriter.deleteIsland(currentIsland);
                    }
                }
                ConfigShorts.broadcastfromString("CleaningUpOldIslandsDone");
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
