package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;

public class Scoreboardmanager {

    private VSkyblock plugin = VSkyblock.getInstance();
    private ScoreboardManager sm = plugin.getServer().getScoreboardManager();

    /**
     * Checks if given objective exists.
     *
     * @param objective
     * @return boolean
     */
    public boolean doesobjectiveexist(String objective) {
        return plugin.getServer().getScoreboardManager().getMainScoreboard().getObjective(objective) != null;
    }

    /**
     * Updates players objective score.
     *
     * @param playername
     * @param objective
     * @param score
     * @return true if success
     */
    public boolean updatePlayerScore(String playername, String objective, int score) {
        if (doesobjectiveexist(objective)) {
            Objective obj = sm.getMainScoreboard().getObjective(objective);
            obj.getScore(playername).setScore(score);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds player to objective.
     *
     * @param player
     * @param objective
     * @return true if success
     */
    public boolean addPlayerToObjective(Player player, String objective) {
        if (doesobjectiveexist(objective)) {
            Objective obj = plugin.getServer().getScoreboardManager().getMainScoreboard().getObjective("deaths");
            player.setScoreboard(obj.getScoreboard());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if a player has an objective score.
     *
     * @param playername
     * @param objective
     * @return true if player has score.
     */
    public boolean hasPlayerScore(String playername, String objective) {
        if (doesobjectiveexist(objective)) {
            Objective obj = sm.getMainScoreboard().getObjective(objective);
            if (obj.getScore(playername) != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Gets the players score of an specific objective.
     *
     * @param playername
     * @param objective
     * @return players objective score
     */
    public int getPlayerScore(String playername, String objective) {
        if (doesobjectiveexist(objective)) {
            Objective obj = sm.getMainScoreboard().getObjective(objective);
            if (obj != null) {
                return obj.getScore(playername).getScore();
            }
        } return 0;
    }
}
