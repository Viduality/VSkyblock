package com.github.Viduality.VSkyblock.Utilitys;


import com.github.Viduality.VSkyblock.VSkyblock;

import java.util.Calendar;

public class DeleteOldIslands implements Runnable {

    private final VSkyblock plugin;

    public DeleteOldIslands(VSkyblock plugin) {
        this.plugin = plugin;
    }


    @Override
    public void run() {
        int rltime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (rltime == getCleanUpTime()) {
            startCleanUp();
        }
    }

    /**
     * Starts the whole cleanup.
     */
    private void startCleanUp() {
        ConfigShorts.broadcastfromString("CleaningUpOldIslands");
        plugin.getDb().getReader().getEmptyIslands(result -> {
            for (String currentIsland : result) {
                plugin.getWorldManager().deleteWorld(currentIsland).thenAccept(deleted -> {
                    if (deleted) {
                        plugin.getDb().getWriter().deleteIsland(currentIsland);
                    }
                });
            }
            ConfigShorts.broadcastfromString("CleaningUpOldIslandsDone");
        });
    }

    /**
     * Gets the hour of the day when the cleanup happens.
     * @return Integer hour.
     */
    private Integer getCleanUpTime() {
        String cleanUpTimeString = ConfigShorts.getDefConfig().getString("CleanUpTime");
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
