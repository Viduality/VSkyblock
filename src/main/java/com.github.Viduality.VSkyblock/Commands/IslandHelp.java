package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;


public class IslandHelp implements SubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();


    @Override
    public void execute(DatabaseCache databaseCache) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                ConfigShorts.loadHelpConfig();
                String intro = plugin.getConfig().getString("IntroText");
                String island = plugin.getConfig().getString("Island");
                String sethome = plugin.getConfig().getString("IslandSethome");
                String restart = plugin.getConfig().getString("IslandRestart");
                String restartconfirm = plugin.getConfig().getString("IslandRestartConfirm");
                String invite = plugin.getConfig().getString("IslandInvite");
                String accept = plugin.getConfig().getString("IslandAccept");
                String kick = plugin.getConfig().getString("IslandKick");
                String setowner = plugin.getConfig().getString("IslandSetowner");
                String leave = plugin.getConfig().getString("IslandLeave");
                String leaveconfirm = plugin.getConfig().getString("IslandLeaveConfirm");
                String members = plugin.getConfig().getString("IslandMembers");
                String top = plugin.getConfig().getString("IslandTop");
                String challenges = plugin.getConfig().getString("Challenges");
                String message = intro + "\n" +
                        ChatColor.YELLOW +  "/island" + "\n" + ChatColor.RESET + " - " + island + "\n" +
                        ChatColor.YELLOW +  "/island sethome" + "\n" + ChatColor.RESET + " - " + sethome + "\n" +
                        ChatColor.YELLOW +  "/island restart" + "\n" + ChatColor.RESET + " - " + restart + "\n" +
                        ChatColor.YELLOW +  "/island restart confirm" + "\n" + ChatColor.RESET + " - " + restartconfirm + "\n" +
                        ChatColor.YELLOW +  "/island invite" + "\n" + ChatColor.RESET + " - " + invite + "\n" +
                        ChatColor.YELLOW +  "/island accept" + "\n" + ChatColor.RESET + " - " + accept + "\n" +
                        ChatColor.YELLOW +  "/island kick" + "\n" + ChatColor.RESET + " - " + kick + "\n" +
                        ChatColor.YELLOW +  "/island setowner" + "\n" + ChatColor.RESET + " - " + setowner + "\n" +
                        ChatColor.YELLOW +  "/island leave" + "\n" + ChatColor.RESET + " - " + leave + "\n" +
                        ChatColor.YELLOW +  "/island leave confirm" + "\n" + ChatColor.RESET + " - " + leaveconfirm + "\n" +
                        ChatColor.YELLOW +  "/island members" + "\n" + ChatColor.RESET + " - " + members + "\n" +
                        ChatColor.YELLOW +  "/island top" + "\n" + ChatColor.RESET + " - " + top + "\n" +
                        ChatColor.YELLOW +  "/challenges" + "\n" + ChatColor.RESET + " - " + challenges;
                databaseCache.getPlayer().sendMessage(message);
            }
        });
    }
}
