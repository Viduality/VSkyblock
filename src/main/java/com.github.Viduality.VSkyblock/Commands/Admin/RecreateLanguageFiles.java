package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.Commands.WorldCommands.AdminSubCommand;
import com.github.Viduality.VSkyblock.DefaultFiles;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class RecreateLanguageFiles implements AdminSubCommand {

    private VSkyblock plugin = VSkyblock.getInstance();

    @Override
    public void execute(CommandSender sender, String args, String option1, String option2) {
        if (sender.hasPermission("VSkyblock.RecreateLanguageFiles")) {
            File path = new File(plugin.getDataFolder() + "/Languages");
            if (path.exists()) {
                File[] files = path.listFiles();
                if (files != null) {
                    for (File currentFile : files) {
                        currentFile.delete();
                    }
                }
                DefaultFiles.init();
                ConfigShorts.messagefromString("RecreatedLanguageFiles", sender);
            }
        }
    }
}
