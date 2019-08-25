package com.github.Viduality.VSkyblock.Commands.Admin;

import com.github.Viduality.VSkyblock.SQLConnector;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.VSkyblock;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;


public class Testcommand implements CommandExecutor {

    private VSkyblock plugin = VSkyblock.getInstance();
    private SQLConnector getDatabase = new SQLConnector();
    private DatabaseReader databaseReader = new DatabaseReader();
    private VSkyblock testcommand;
    public Testcommand(VSkyblock testcommand) {
        this.testcommand = testcommand;
    }
    private HashMap<Player, String> test = new HashMap<>();
    private WorldManager wm = new WorldManager();





    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("Testcommand")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("VSkyblock.Testcommand")) {
                    // wm.setOption(player.getWorld().getName(), "autoLoad", "true");
                    // wm.addWorld(args[0]);
                    // wm.setSpawnLocation(player.getLocation());
                    // player.sendMessage(String.valueOf(player.getWorld().getGenerator()));
                    wm.deleteWorld("VSkyblockMasterIsland");

                }
            }
        }
        return true;
    }
}
