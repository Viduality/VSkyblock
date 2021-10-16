package com.github.Viduality.VSkyblock;


/*
 * VSkyblock
 * Copyright (C) 2020  Viduality
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import com.github.Viduality.VSkyblock.Challenges.ChallengesManager;
import com.github.Viduality.VSkyblock.Commands.Challenges;
import com.github.Viduality.VSkyblock.Challenges.ChallengesCreator;
import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Commands.Admin.AdminCommands;
import com.github.Viduality.VSkyblock.Listener.*;
import com.github.Viduality.VSkyblock.Utilitys.*;
import com.github.Viduality.VSkyblock.WorldGenerator.VoidGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Viduality
 * @version 1.0
 */
public class VSkyblock extends JavaPlugin implements Listener {

    private static VSkyblock instance;

    private TeleportHandler teleportHandler;

    private SQLConnector sqlConnector;

    public Scoreboardmanager scoreboardmanager;
    private WorldManager worldManager;
    private ChallengesManager challengesManager;
    private ConfigChanger configChanger;

    public void onEnable() {
        instance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        DefaultFiles.init();
        ConfigShorts.reloadAllConfigs();

        configChanger = new ConfigChanger(this);
        sqlConnector = new SQLConnector(this);
        worldManager = new WorldManager(this);
        challengesManager = new ChallengesManager(this);
        teleportHandler = new TeleportHandler(this);


        getCommand("Island").setExecutor(new Island(this));

        getCommand("Challenges").setExecutor(new Challenges(this));

        //getCommand("Testcommand").setExecutor(new Testcommand());

        getCommand("VSkyblock").setExecutor( new AdminCommands(this));


        /*
         * Register all Events
         */

        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new PlayerLeaveListener(this), this);
        pm.registerEvents(new PlayerDeathListener(), this);
        pm.registerEvents(new BlockBreakListener(), this);
        pm.registerEvents(new NetherPortalListener(this), this);
        pm.registerEvents(new BlockProtector(), this);
        pm.registerEvents(new EntityProtector(this), this);
        pm.registerEvents(new InteractBlocker(this), this);
        pm.registerEvents(new ItemDropBlocker(), this);
        pm.registerEvents(new ItemPickupBlocker(), this);
        pm.registerEvents(new ChallengesInventoryHandler(this), this);
        pm.registerEvents(new PortalAccessor(this), this);
        pm.registerEvents(new TeleporterInventoryHandler(), this);
        pm.registerEvents(new IslandOptionsInventoryHandler(this), this);
        pm.registerEvents(new CobblestoneGenerator(this), this);
        pm.registerEvents(new CobblestoneGeneratorInventoryHandler(this), this);
        pm.registerEvents(new PhantomSpawn(), this);


        getServer().getScheduler().runTaskTimer(this, new DeleteOldIslands(this), 10, 72000);

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

        sqlConnector.initTables();

        new WorldLoader(this).run();

        setGeneratorChances();

        scoreboardmanager = new Scoreboardmanager(this);

        ScoreboardManager sm = getServer().getScoreboardManager();
        Scoreboard scoreboard = sm.getMainScoreboard();
        if (!scoreboardmanager.doesobjectiveexist("deaths")) {
            Objective deathCount = scoreboard.registerNewObjective("deaths", "deathCount", "Deaths");
            deathCount.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }

        if (getOnlinePlayers().size() != 0) {
            getDb().getReader().refreshIslands(getOnlinePlayers());
            getDb().getReader().refreshDeathCounts(getOnlinePlayers());
        }
        updateNewTables();
        ChallengesCreator cc = new ChallengesCreator(this);
        if (cc.createAllChallenges()) {
            if (!useNewTables()) {
                new ChallengesConverter(this).convertAllChallenges();
            }
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

    public ConfigChanger getConfigChanger() {
        return configChanger;
    }

    public SQLConnector getDb() {
        return sqlConnector;
    }

    public Scoreboardmanager getScoreboardManager() {
        return scoreboardmanager;
    }


    public WorldManager getWorldManager() {
        return worldManager;
    }

    public ChallengesManager getChallengesManager() {
        return challengesManager;
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
        addGeneratorLevel("Coal", Material.COAL_ORE, 1, 20, 3);
        addGeneratorLevel("Iron", Material.IRON_ORE, 2, 35, 1.5);
        addGeneratorLevel("Redstone", Material.REDSTONE_ORE, 3, 50, 1.5);
        addGeneratorLevel("Lapis", Material.LAPIS_ORE, 4, 60, 2);
        addGeneratorLevel("Gold", Material.GOLD_ORE, 5, 80, 1);
        addGeneratorLevel("Emerald", Material.EMERALD_ORE, 6, 100, 0.05);
        addGeneratorLevel("Diamond", Material.DIAMOND_ORE, 7, 100, 0.2);
        addGeneratorLevel("AncientDebris", Material.ANCIENT_DEBRIS, 8, 150, 0.4);

        CobblestoneGenerator.cobbleStoneMultiDrop = ConfigShorts.getDefConfig().getBoolean("CobblestoneGenerator.Cobblestone.MultipleDrops", false);
        CobblestoneGenerator.cobblestoneLevelInterval = ConfigShorts.getDefConfig().getInt("CobblestoneGenerator.Cobblestone.CobblestoneLevelIntervall", 10);
        CobblestoneGenerator.cobblestoneChance = ConfigShorts.getDefConfig().getDouble("CobblestoneGenerator.Cobblestone.CobblestoneChance", 20);
    }

    private void addGeneratorLevel(String name, Material type, int level, int islandLevel, double chance) {
        CobblestoneGenerator.LEVELS.add(new CobblestoneGenerator.Level(
                level,
                ConfigShorts.getDefConfig().getInt("CobblestoneGenerator." + name + "Level", islandLevel),
                type,
                ConfigShorts.getDefConfig().getDouble("CobblestoneGenerator." + name + "Chance", chance))
        );
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

    private void updateNewTables() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try (Connection connection = getDb().getConnection()) {
                PreparedStatement preparedStatement;
                preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Island");
                ResultSet resultSet = preparedStatement.executeQuery();
                List<Integer> islandids = new ArrayList<>();
                List<String> islandnames = new ArrayList<>();
                while (resultSet.next()) {
                    islandids.add(resultSet.getInt("islandid"));
                    islandnames.add(resultSet.getString("island"));
                }
                PreparedStatement prep = null;
                PreparedStatement insert = null;
                for (int i = 0; i < islandids.size(); i++) {
                    insert = connection.prepareStatement("INSERT IGNORE INTO VSkyblock_IslandLocations(islandid) VALUES (?)");
                    insert.setInt(1, islandids.get(i));
                    if (insert.executeUpdate() != 0) {
                        String currentis = islandnames.get(i);
                        double x = ConfigShorts.getWorldConfig().getDouble("Worlds." + currentis + ".spawnLocation.x");
                        double y = ConfigShorts.getWorldConfig().getDouble("Worlds." + currentis + ".spawnLocation.y");
                        double z = ConfigShorts.getWorldConfig().getDouble("Worlds." + currentis + ".spawnLocation.z");
                        float yaw = (float) ConfigShorts.getWorldConfig().getDouble("Worlds." + currentis + ".spawnLocation.yaw");
                        float pitch = (float) ConfigShorts.getWorldConfig().getDouble("Worlds." + currentis + ".spawnLocation.pitch");
                        prep = connection.prepareStatement("UPDATE IGNORE VSkyblock_IslandLocations SET spawnX = ?, spawnY = ?, spawnZ = ?, spawnYaw = ?, spawnPitch = ? WHERE islandid = ?");
                        prep.setDouble(1, x);
                        prep.setDouble(2, y);
                        prep.setDouble(3, z);
                        prep.setDouble(4, yaw);
                        prep.setDouble(5, pitch);
                        prep.setInt(6, islandids.get(i));
                        prep.executeUpdate();
                    }
                }
                if (prep != null) {
                    prep.close();
                    insert.close();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    private boolean useNewTables() {
        Connection con = getDb().getConnection();
        try {
            ResultSet r = con.getMetaData().getTables(null, null, "VSkyblock_Challenges_Easy", null);
            return !r.next();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public boolean teleportToIsland(Player player, Location location, boolean visit, List<String> islandMembers) {
        return teleportHandler.teleportToIsland(player, location, visit, islandMembers);
    }
    public boolean teleportToIsland(Player player, Location location) {
        return teleportHandler.teleportToIsland(player, location, false, null);
    }
}
