package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.Commands.WorldCommands.AdminSubCommand;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AdminCommandsHelp implements AdminSubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();

    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                String intro = ConfigShorts.getHelpConfig().getString("IntroTextAdmin");
                String deletePlayer = ConfigShorts.getHelpConfig().getString("VSkyblockDeletePlayer");
                String resetChallenges = ConfigShorts.getHelpConfig().getString("VSkyblockResetChallenges");
                String setNether = ConfigShorts.getHelpConfig().getString("VSkyblockSetNether");
                String setSpawnWorld = ConfigShorts.getHelpConfig().getString("VSkyblockSetSpawnWorld");
                String setSpawnPoint = ConfigShorts.getHelpConfig().getString("VSkyblockSetSpawnPoint");
                String setAutoLoad = ConfigShorts.getHelpConfig().getString("VSkyblockSetAutoLoad");
                String teleport = ConfigShorts.getHelpConfig().getString("VSkyblockTeleport");
                String load = ConfigShorts.getHelpConfig().getString("VSkyblockLoad");
                String unload = ConfigShorts.getHelpConfig().getString("VSkyblockUnload");
                String deleteWorld = ConfigShorts.getHelpConfig().getString("VSkyblockDeleteWorld");
                String createWorld = ConfigShorts.getHelpConfig().getString("VSkyblockCreateWorld");
                String list = ConfigShorts.getHelpConfig().getString("VSkyblockList");
                String importWorld = ConfigShorts.getHelpConfig().getString("VSkyblockImport");
                String recreateLanguageFiles = ConfigShorts.getHelpConfig().getString("VSkyblockRecreateLanguageFiles");
                String recreateHelpFiles = ConfigShorts.getHelpConfig().getString("VSkyblockRecreateHelpFiles");
                String recreateChallengeFiles = ConfigShorts.getHelpConfig().getString("VSkyblockRecreateChallengeFiles");
                String reloadBlockValues = ConfigShorts.getHelpConfig().getString("VSkyblockReloadBlockValues");
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
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock recreate challenges" + "\n" + ChatColor.RESET + " - " + recreateChallengeFiles + "\n" +
                        ChatColor.GOLD + ChatColor.BOLD + "/VSkyblock reload blockvalues" + "\n" + ChatColor.RESET + " - " + reloadBlockValues;
                sender.sendMessage(message);
            }
        });
    }
}
