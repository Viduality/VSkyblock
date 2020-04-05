package com.github.Viduality.VSkyblock;


import com.github.Viduality.VSkyblock.Commands.Admin.*;
import com.github.Viduality.VSkyblock.Commands.Challenges.Challenges;
import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Commands.Admin.AdminCommands;
import com.github.Viduality.VSkyblock.Listener.*;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseReader;
import com.github.Viduality.VSkyblock.Utilitys.DeleteOldIslands;
import com.github.Viduality.VSkyblock.Utilitys.Scoreboardmanager;
import com.github.Viduality.VSkyblock.Utilitys.WorldLoader;
import com.github.Viduality.VSkyblock.WorldGenerator.VoidGenerator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Viduality
 * @version 1.0
 */
public class VSkyblock extends JavaPlugin implements Listener {


    private static VSkyblock instance;
    private SQLConnector sqlConnector;
    public Scoreboardmanager scoreboardmanager;

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
        pm.registerEvents(new IslandOptionsInventoryHandler(), this);
        pm.registerEvents(new CobblestoneGenerator(), this);
        pm.registerEvents(new CobblestoneGeneratorInventoryHandler(), this);
        pm.registerEvents(new PhantomSpawn(), this);

        new DeleteOldIslands().run();

        /*  NOT IMPLEMENTED YET
         {   //Teleporter Recipe
            ItemStack portalFrame = new ItemStack(Material.END_PORTAL_FRAME);
            ItemMeta portalFramemeta = portalFrame.getItemMeta();
            portalFramemeta.setDisplayName(ChatColor.DARK_PURPLE + "Teleporter");
            portalFramemeta.addEnchant(Enchantment.DURABILITY, 1, false);
            portalFramemeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            portalFrame.setItemMeta(portalFramemeta);

            NamespacedKey portalFrameKey = NamespacedKey.minecraft("vskyblockportalFrame");
            ShapedRecipe portal = new ShapedRecipe(portalFrameKey ,portalFrame);
            portal.shape("ded", "oeo", "sss");
            portal.setIngredient('e', Material.ENDER_EYE);
            portal.setIngredient('d', Material.DIAMOND);
            portal.setIngredient('o', Material.OBSIDIAN);
            portal.setIngredient('s', Material.END_STONE);
            getServer().addRecipe(portal);
        }
         */


        sqlConnector = new SQLConnector();
        sqlConnector.initConnection();

        new WorldLoader().run();

        setGeneratorChances();

        scoreboardmanager = new Scoreboardmanager();

        ScoreboardManager sm = getServer().getScoreboardManager();
        Scoreboard scoreboard = sm.getMainScoreboard();
        if (!scoreboardmanager.doesobjectiveexist("deaths")) {
            Objective deathCount = scoreboard.registerNewObjective("deaths", "deathCount", "Deaths");
            deathCount.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }

        if (getOnlinePlayers().size() != 0) {
            databaseReader.refreshIslands(getOnlinePlayers());
            databaseReader.refreshDeathCounts(getOnlinePlayers());
        }
    }




    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        sqlConnector.close();
        getServer().resetRecipes();
        getServer().getScoreboardManager().getMainScoreboard().getObjective("deaths").unregister();
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

    private void setGeneratorChances() {
        double coalLevel = 20;
        double coalChance = 3;
        double ironLevel = 35;
        double ironChance = 1.5;
        double redstoneLevel = 50;
        double redstoneChance = 1.5;
        double lapisLevel = 60;
        double lapisChance = 2;
        double goldLevel = 80;
        double goldChance = 1;
        double emeraldLevel = 100;
        double emeraldChance = 0.05;
        double diamondLevel = 100;
        double diamondChance = 0.2;
        int cobblestoneLevelIntervall = 10;
        double cobblestoneChance = 20;
        if (getConfig().getString("CobblestoneGenerator.CoalLevel") != null) {
            if (isDouble(getConfig().getString("CobblestoneGenerator.CoalLevel"))) {
                coalLevel = getConfig().getDouble("CobblestoneGenerator.CoalLevel");
            }
        }
        if (getConfig().getString("CobblestoneGenerator.CoalChance") != null) {
            if (isDouble(getConfig().getString("CobblestoneGenerator.CoalChance"))) {
                coalChance = getConfig().getDouble("CobblestoneGenerator.CoalChance");
            }
        }
        if (getConfig().getString("CobblestoneGenerator.IronLevel") != null) {
            if (isDouble(getConfig().getString("CobblestoneGenerator.IronLevel"))) {
                ironLevel = getConfig().getDouble("CobblestoneGenerator.IronLevel");
            }
        }
        if (getConfig().getString("CobblestoneGenerator.IronChance") != null) {
            if (isDouble(getConfig().getString("CobblestoneGenerator.IronChance"))) {
                ironChance = getConfig().getDouble("CobblestoneGenerator.IronChance");
            }
        }
        if (getConfig().getString("CobblestoneGenerator.RedstoneLevel") != null) {
            if (isDouble(getConfig().getString("CobblestoneGenerator.RedstoneLevel"))) {
                redstoneLevel = getConfig().getDouble("CobblestoneGenerator.RedstoneLevel");
            }
        }
        if (getConfig().getString("CobblestoneGenerator.RedstoneChance") != null) {
            if (isDouble(getConfig().getString("CobblestoneGenerator.RedstoneChance"))) {
                redstoneChance = getConfig().getDouble("CobblestoneGenerator.RedstoneChance");
            }
        }
        if (getConfig().getString("CobblestoneGenerator.LapisLevel") != null) {
            if (isDouble(getConfig().getString("CobblestoneGenerator.LapisLevel"))) {
                lapisLevel = getConfig().getDouble("CobblestoneGenerator.LapisLevel");
            }
        }
        if (getConfig().getString("CobblestoneGenerator.LapisChance") != null) {
            if (isDouble(getConfig().getString("CobblestoneGenerator.LapisChance"))) {
                lapisChance = getConfig().getDouble("CobblestoneGenerator.LapisChance");
            }
        }
        if (getConfig().getString("CobblestoneGenerator.GoldLevel") != null) {
            if (isDouble(getConfig().getString("CobblestoneGenerator.GoldLevel"))) {
                goldLevel = getConfig().getDouble("CobblestoneGenerator.GoldLevel");
            }
        }
        if (getConfig().getString("CobblestoneGenerator.GoldChance") != null) {
            if (isDouble(getConfig().getString("CobblestoneGenerator.GoldChance"))) {
                goldChance = getConfig().getDouble("CobblestoneGenerator.GoldChance");
            }
        }
        if (getConfig().getString("CobblestoneGenerator.EmeraldLevel") != null) {
            if (isDouble(getConfig().getString("CobblestoneGenerator.EmeraldLevel"))) {
                emeraldLevel = getConfig().getDouble("CobblestoneGenerator.EmeraldLevel");
            }
        }
        if (getConfig().getString("CobblestoneGenerator.EmeraldChance") != null) {
            if (isDouble(getConfig().getString("CobblestoneGenerator.EmeraldChance"))) {
                emeraldChance = getConfig().getDouble("CobblestoneGenerator.EmeraldChance");
            }
        }
        if (getConfig().getString("CobblestoneGenerator.DiamondLevel") != null) {
            if (isDouble(getConfig().getString("CobblestoneGenerator.DiamondLevel"))) {
                diamondLevel = getConfig().getDouble("CobblestoneGenerator.DiamondLevel");
            }
        }
        if (getConfig().getString("CobblestoneGenerator.DiamondChance") != null) {
            if (isDouble(getConfig().getString("CobblestoneGenerator.DiamondChance"))) {
                diamondChance = getConfig().getDouble("CobblestoneGenerator.DiamondChance");
            }
        }
        CobblestoneGenerator.generatorValues.put("CoalLevel", coalLevel);
        CobblestoneGenerator.generatorValues.put("CoalChance", coalChance);
        CobblestoneGenerator.generatorValues.put("IronLevel", ironLevel);
        CobblestoneGenerator.generatorValues.put("IronChance", ironChance);
        CobblestoneGenerator.generatorValues.put("RedstoneLevel", redstoneLevel);
        CobblestoneGenerator.generatorValues.put("RedstoneChance", redstoneChance);
        CobblestoneGenerator.generatorValues.put("LapisLevel", lapisLevel);
        CobblestoneGenerator.generatorValues.put("LapisChance", lapisChance);
        CobblestoneGenerator.generatorValues.put("GoldLevel", goldLevel);
        CobblestoneGenerator.generatorValues.put("GoldChance", goldChance);
        CobblestoneGenerator.generatorValues.put("EmeraldLevel", emeraldLevel);
        CobblestoneGenerator.generatorValues.put("EmeraldChance", emeraldChance);
        CobblestoneGenerator.generatorValues.put("DiamondLevel", diamondLevel);
        CobblestoneGenerator.generatorValues.put("DiamondChance", diamondChance);


        if (getConfig().getString("CobblestoneGenerator.Cobblestone.MultipleDrops") != null) {
            if (getConfig().getString("CobblestoneGenerator.Cobblestone.MultipleDrops").equalsIgnoreCase("true")) {
                if (getConfig().getString("CobblestoneGenerator.Cobblestone.CobblestoneLevelIntervall") != null) {
                    if (isInt(getConfig().getString("CobblestoneGenerator.Cobblestone.CobblestoneLevelIntervall"))) {
                        cobblestoneLevelIntervall = getConfig().getInt("CobblestoneGenerator.Cobblestone.CobblestoneLevelIntervall");
                    }
                }
                if (getConfig().getString("CobblestoneGenerator.Cobblestone.CobblestoneChance") != null) {
                    if (isInt(getConfig().getString("CobblestoneGenerator.Cobblestone.CobblestoneChance"))) {
                        cobblestoneChance = getConfig().getInt("CobblestoneGenerator.Cobblestone.CobblestoneChance");
                    }
                }
            }
        }

        CobblestoneGenerator.generatorValues.put("CobblestoneLevelIntervall", (double) cobblestoneLevelIntervall);
        CobblestoneGenerator.generatorValues.put("CobblestoneChance", cobblestoneChance);
    }

    private boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
