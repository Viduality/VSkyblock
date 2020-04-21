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
                String intro = ConfigShorts.getHelpConfig().getString("IntroText");
                String island = ConfigShorts.getHelpConfig().getString("Island");
                String sethome = ConfigShorts.getHelpConfig().getString("IslandSethome");
                String restart = ConfigShorts.getHelpConfig().getString("IslandRestart");
                String restartconfirm = ConfigShorts.getHelpConfig().getString("IslandRestartConfirm");
                String invite = ConfigShorts.getHelpConfig().getString("IslandInvite");
                String accept = ConfigShorts.getHelpConfig().getString("IslandAccept");
                String kick = ConfigShorts.getHelpConfig().getString("IslandKick");
                String setowner = ConfigShorts.getHelpConfig().getString("IslandSetowner");
                String leave = ConfigShorts.getHelpConfig().getString("IslandLeave");
                String leaveconfirm = ConfigShorts.getHelpConfig().getString("IslandLeaveConfirm");
                String level = ConfigShorts.getHelpConfig().getString("IslandLevel");
                String members = ConfigShorts.getHelpConfig().getString("IslandMembers");
                String options = ConfigShorts.getHelpConfig().getString("IslandOptions");
                String top = ConfigShorts.getHelpConfig().getString("IslandTop");
                String visit = ConfigShorts.getHelpConfig().getString("IslandVisit");
                String setnetherhome = ConfigShorts.getHelpConfig().getString("IslandSetnetherhome");
                String nether = ConfigShorts.getHelpConfig().getString("IslandNether");
                String challenges = ConfigShorts.getHelpConfig().getString("Challenges");
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
                        ChatColor.GOLD + ChatColor.BOLD + "/island options" + "\n" + ChatColor.RESET + " - " + options + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island top" + "\n" + ChatColor.RESET + " - " + top + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island visit" + "\n" + ChatColor.RESET + " - " + visit + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island setnetherhome" + "\n" + ChatColor.RESET + " - " + setnetherhome + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/island nether" + "\n" + ChatColor.RESET + " - " + nether + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/challenges" + "\n" + ChatColor.RESET + " - " + challenges;
                databaseCache.getPlayer().sendMessage(message);
            }
        });
    }
}
