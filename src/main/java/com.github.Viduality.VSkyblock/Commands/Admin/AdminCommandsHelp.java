package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.Commands.WorldCommands.AdminSubCommand;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AdminCommandsHelp implements AdminSubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();

    @Override
    public void execute(Player player, String args, String option1, String option2) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                ConfigShorts.loadHelpConfig();
                String intro = plugin.getConfig().getString("IntroTextAdmin");
                String deletePlayer = plugin.getConfig().getString("VSkyblockDeletePlayer");
                String resetChallenges = plugin.getConfig().getString("VSkyblockResetChallenges");
                String setNether = plugin.getConfig().getString("VSkyblockSetNether");
                String setSpawnWorld = plugin.getConfig().getString("VSkyblockSetSpawnWorld");
                String setSpawnPoint = plugin.getConfig().getString("VSkyblockSetSpawnPoint");
                String setAutoLoad = plugin.getConfig().getString("VSkyblockSetAutoLoad");
                String teleport = plugin.getConfig().getString("VSkyblockTeleport");
                String load = plugin.getConfig().getString("VSkyblockLoad");
                String unload = plugin.getConfig().getString("VSkyblockUnload");
                String deleteWorld = plugin.getConfig().getString("VSkyblockDeleteWorld");
                String createWorld = plugin.getConfig().getString("VSkyblockCreateWorld");
                String list = plugin.getConfig().getString("VSkyblockList");
                String importWorld = plugin.getConfig().getString("VSkyblockImport");
                String recreateLanguageFiles = plugin.getConfig().getString("VSkyblockRecreateLanguageFiles");
                String recreateHelpFiles = plugin.getConfig().getString("VSkyblockRecreateHelpFiles");
                String recreateChallengeFiles = plugin.getConfig().getString("VSkyblockRecreateChallengeFiles");
                ConfigShorts.loaddefConfig();
                String message = ChatColor.AQUA + intro + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock delete player <Player>" + "\n" + ChatColor.RESET + " - " + deletePlayer + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock reset challenges <Player>" + "\n" + ChatColor.RESET + " - " + resetChallenges + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock set nether" + "\n" + ChatColor.RESET + " - " + setNether + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock set spawnworld" + "\n" + ChatColor.RESET + " - " + setSpawnWorld + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock set spawnpoint" + "\n" + ChatColor.RESET + " - " + setSpawnPoint + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock set autoload <true|false>" + "\n" + ChatColor.RESET + " - " + setAutoLoad + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock teleport <World>" + "\n" + ChatColor.RESET + " - " + teleport + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock load <World>" + "\n" + ChatColor.RESET + " - " + load + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock unload <World>" + "\n" + ChatColor.RESET + " - " + unload + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock delete world <World>" + "\n" + ChatColor.RESET + " - " + deleteWorld + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock create world <World> <Generator> <Environment>" + "\n" + ChatColor.RESET + " - " + createWorld + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock list" + "\n" + ChatColor.RESET + " - " + list + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock import <world>" + "\n" + ChatColor.RESET + " - " + importWorld + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock recreate languages" + "\n" + ChatColor.RESET + " - " + recreateLanguageFiles + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock recreate help" + "\n" + ChatColor.RESET + " - " + recreateHelpFiles + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock recreate challenges" + "\n" + ChatColor.RESET + " - " + recreateChallengeFiles + "\n";
                player.sendMessage(message);
            }
        });
    }
}
