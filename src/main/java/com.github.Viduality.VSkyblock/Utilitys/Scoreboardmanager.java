package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.Challenges.Challenge;
import com.github.Viduality.VSkyblock.Challenges.ChallengesHandler;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public void updateTracked(int islandId, ChallengesCache challenges) {
        plugin.getDatabaseReader().getIslandMembers(islandId, members -> {
            for (String member : members) {
                Player player = plugin.getServer().getPlayer(member);
                if (player != null) {
                    updateTracked(player, challenges);
                }
            }
        });
    }

    public void updateTracked(Player player, ChallengesCache challenges) {
        if (challenges.getTrackedChallenges().isEmpty()) {
            player.setScoreboard(sm.getMainScoreboard());
        } else {
            if (player.getScoreboard() == sm.getMainScoreboard()) {
                player.setScoreboard(sm.getNewScoreboard());
            }

            Objective sidebar = player.getScoreboard().getObjective("tracked");
            if (sidebar == null) {
                sidebar = player.getScoreboard().registerNewObjective("tracked", "dummy", ConfigShorts.getMessageConfig().getString("SideBarTrackedChallenges"));
            }
            if (sidebar.getDisplaySlot() != DisplaySlot.SIDEBAR) {
                sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
            }

            Map<Challenge, Integer> challengeCounts = new LinkedHashMap<>();
            for (String challengeId : challenges.getTrackedChallenges()) {
                Challenge challenge = ChallengesHandler.challenges.get(challengeId);
                if (challenge != null) {
                    challengeCounts.put(challenge, challenges.getChallengeCount(challengeId));
                }
            }

            List<Map.Entry<Challenge, Integer>> lines = new ArrayList<>(challengeCounts.entrySet());
            for (int i = 0; i < lines.size(); i++) {
                sidebar.getScore(ChatColor.values()[i].toString()).setScore(lines.get(i).getValue());

                Team team = player.getScoreboard().getTeam("vskyblock_ct" + i);
                if (team == null) {
                    team = player.getScoreboard().registerNewTeam("vskyblock_ct" + i);
                    team.addEntry(ChatColor.values()[i].toString());
                }

                String line = lines.get(i).getKey().getNeededText();
                if (line.length() <= 32) {
                    team.setPrefix(ChatColor.YELLOW + line);
                } else {
                    team.setPrefix(ChatColor.YELLOW + line.substring(0, 32) + "...");
                }
            }
            for (int i = lines.size(); i < 10; i++) {
                player.getScoreboard().resetScores(ChatColor.values()[i].toString());
            }
        }
    }
}
