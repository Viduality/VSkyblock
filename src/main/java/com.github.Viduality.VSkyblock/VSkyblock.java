package com.github.Viduality.VSkyblock;


import com.github.Viduality.VSkyblock.Commands.Admin.*;
import com.github.Viduality.VSkyblock.Commands.Challenges.Challenges;
import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Commands.Admin.AdminCommands;
import com.github.Viduality.VSkyblock.Listener.*;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.DeleteOldIslands;
import com.github.Viduality.VSkyblock.WorldGenerator.VoidGenerator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Viduality
 * @version 1.0
 */
public class VSkyblock extends JavaPlugin implements Listener {


    private static VSkyblock instance;
    private SQLConnector sqlConnector;

    private Island islandExecutor;
    private Testcommand testcommandExecutor;
    private Challenges challengesExecutor;
    private AdminCommands adminCommandsExecutor;




    public void onEnable() {
        instance = this;

        DatabaseReader databaseReader = new DatabaseReader();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        DefaultFiles.init();


        {
            islandExecutor = new Island(this);
            getCommand("Island").setExecutor(islandExecutor);
        }

        {
            challengesExecutor = new Challenges(this);
            getCommand("Challenges").setExecutor(challengesExecutor);
        }

        {
            testcommandExecutor = new Testcommand(this);
            getCommand("Testcommand").setExecutor(testcommandExecutor);
        }

        {
            adminCommandsExecutor = new AdminCommands(this);
            getCommand("VSkyblock").setExecutor(adminCommandsExecutor);
        }


        /*
         * Register all Events
         */

        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinListener(), this);
        pm.registerEvents(new PlayerLeaveListener(), this);
        pm.registerEvents(new PlayerRespawnListener(), this);
        pm.registerEvents(new BlockBreakListener(), this);
        pm.registerEvents(new NetherPortalListener(), this);
        pm.registerEvents(new BlockProtector(), this);
        pm.registerEvents(new EntityProtector(), this);
        pm.registerEvents(new InteractBlocker(), this);
        pm.registerEvents(new ItemDropBlocker(), this);
        pm.registerEvents(new ItemPickupBlocker(), this);
        pm.registerEvents(new ChallengesInventoryHandler(), this);
        pm.registerEvents(new PortalAccessor(), this);
        pm.registerEvents(new TeleporterInventoryHandler(), this);

        new DeleteOldIslands().run();

        {   //Teleporter Recipe
            ItemStack portalFrame = new ItemStack(Material.END_PORTAL_FRAME);
            ItemMeta portalFramemeta = portalFrame.getItemMeta();
            portalFramemeta.setDisplayName(ChatColor.DARK_PURPLE + "Teleporter");
            portalFramemeta.addEnchant(Enchantment.DURABILITY, 1, false);
            portalFramemeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            portalFrame.setItemMeta(portalFramemeta);

            ShapedRecipe portal = new ShapedRecipe(portalFrame);
            portal.shape("ded", "oeo", "sss");
            portal.setIngredient('e', Material.ENDER_EYE);
            portal.setIngredient('d', Material.DIAMOND);
            portal.setIngredient('o', Material.OBSIDIAN);
            portal.setIngredient('s', Material.END_STONE);
            getServer().addRecipe(portal);
        }

        {
            ItemStack gravel = new ItemStack(Material.GRAVEL, 4);
            ShapelessRecipe gravel1 = new ShapelessRecipe(gravel);
            gravel1.addIngredient(3, Material.COBBLESTONE);
            gravel1.addIngredient(1, Material.DIRT);
            getServer().addRecipe(gravel1);
        }

        sqlConnector = new SQLConnector();
        sqlConnector.initConnection();

        if (getOnlinePlayers().size() != 0) {
            databaseReader.refreshIslands(getOnlinePlayers());
        }
    }




    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        sqlConnector.close();
        getServer().resetRecipes();
    }

    /**
     * Provides an instance of the plugin
     * @return plugin instance
     */
    public static VSkyblock getInstance() {
        return instance;
    }


    /**
     * Returns all online players.
     * @return Player list
     */
    public List<Player> getOnlinePlayers() {
        return new ArrayList<>(getServer().getOnlinePlayers());
    }



    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new VoidGenerator();
    }
}
