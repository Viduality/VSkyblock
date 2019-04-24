package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.SQLConnector;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;


public class Testcommand implements CommandExecutor {

    private VSkyblock plugin = VSkyblock.getInstance();
    private SQLConnector getDatabase = new SQLConnector();
    private DatabaseReader databaseReader = new DatabaseReader();
    private VSkyblock testcommand;
    public Testcommand(VSkyblock testcommand) {
        this.testcommand = testcommand;
    }
    private HashMap<Player, String> test = new HashMap<>();





    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("Testcommand")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("VSkyblock.Testcommand")) {
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                        @Override
                        public void run() {
                            plugin.getMV().getCore().getMVWorldManager().getMVWorld("VSkyblockMasterIsland").setAlias("VSkyblockMasterIsland");
                        }
                    });
                }
            }
        }
        return false;
    }
}
