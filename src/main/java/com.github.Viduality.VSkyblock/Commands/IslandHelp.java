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
                String level = plugin.getConfig().getString("IslandLevel");
                String members = plugin.getConfig().getString("IslandMembers");
                String options = plugin.getConfig().getString("IslandOptions");
                String top = plugin.getConfig().getString("IslandTop");
                String visit = plugin.getConfig().getString("IslandVisit");
                String challenges = plugin.getConfig().getString("Challenges");
                ConfigShorts.loaddefConfig();
                String message = ChatColor.AQUA + intro + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island" + "\n" + ChatColor.RESET + " - " + island + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island sethome" + "\n" + ChatColor.RESET + " - " + sethome + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island restart" + "\n" + ChatColor.RESET + " - " + restart + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island restart confirm" + "\n" + ChatColor.RESET + " - " + restartconfirm + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island invite" + "\n" + ChatColor.RESET + " - " + invite + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island accept" + "\n" + ChatColor.RESET + " - " + accept + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island kick" + "\n" + ChatColor.RESET + " - " + kick + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island setowner" + "\n" + ChatColor.RESET + " - " + setowner + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island leave" + "\n" + ChatColor.RESET + " - " + leave + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island leave confirm" + "\n" + ChatColor.RESET + " - " + leaveconfirm + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island level" + "\n" + ChatColor.RESET + " - " + level + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island members" + "\n" + ChatColor.RESET + " - " + members + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island top" + "\n" + ChatColor.RESET + " - " + top + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island visit" + "\n" + ChatColor.RESET + " - " + visit + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/challenges" + "\n" + ChatColor.RESET + " - " + challenges;
                databaseCache.getPlayer().sendMessage(message);
            }
        });
    }
}
