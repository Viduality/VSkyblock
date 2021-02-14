package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.Commands.WorldCommands.AdminSubCommand;
import com.github.Viduality.VSkyblock.DefaultFiles;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;

import java.io.File;

public class RecreateChallengeFiles implements AdminSubCommand {

    private final VSkyblock plugin;

    public RecreateChallengeFiles(VSkyblock plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender.hasPermission("VSkyblock.RecreateChallengeFiles")) {
            File path = new File(plugin.getDataFolder() + "/Challenges");
            if (path.exists()) {
                File[] files = path.listFiles();
                if (files != null) {
                    for (File currentFile : files) {
                        currentFile.delete();
                    }
                }
                DefaultFiles.init();
                ConfigShorts.messagefromString("RecreatedChallengeFiles", sender);
            }
        } else {
            ConfigShorts.messagefromString("PermissionLack", sender);
        }
    }
}
