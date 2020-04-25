package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.VSkyblock;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WorldManager {

    private VSkyblock plugin = VSkyblock.getInstance();

    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001b[0m";


    /**
     * Creates a new island. Also writes it into the Worlds.yml config file
     * @param island
     */
    public boolean createIsland(String island) {

        //Check if the world doesn't already exists
        if (!getAllWorlds().contains(island)) {
            File dir = new File(plugin.getServer().getWorldContainer().getAbsolutePath(), island);
            if (!dir.exists()) {
                dir.mkdirs();

                File source = new File(plugin.getServer().getWorldContainer().getAbsolutePath(), "VSkyblockMasterIsland");

                try {
                    FileUtils.copyDirectory(source, dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                File files[] = dir.listFiles();
                for (File file : files) {
                    if (file.getName().equalsIgnoreCase("uid.dat") || file.getName().equalsIgnoreCase("session.lock")) {
                        file.delete();
                    }
                }

                WorldCreator wc = new WorldCreator(island);
                wc.generator("VSkyblock");
                wc.environment(World.Environment.NORMAL);
                wc.type(WorldType.FLAT);
                wc.generateStructures(false);
                World newIsland = wc.createWorld();
                plugin.getServer().getWorlds().add(newIsland);
                plugin.getServer().getWorld(island).setSpawnLocation(0, 67, 0);
                newIsland.setDifficulty(getDifficulty(newIsland.getName()));
                newIsland.setKeepSpawnInMemory(false);
                if (addWorld(island, "VSkyblock", "NORMAL")) {
                    return true;
                } else {
                    System.out.println(ANSI_RED + "Could not add world to config!" + ANSI_RESET);
                    return false;
                }
            } else {
                System.out.println(ANSI_RED + "Folder already exists!" + ANSI_RESET);
            }
        } else {
            System.out.println(ANSI_RED + "Tried to create a world but VSkyblock already knows about it!" + ANSI_RESET);
        }
        return false;
    }

    /**
     * Unloads a world.
     * Teleports all players in the world into the SpawnWorld from the config.
     * @param world
     * @return boolean
     */
    public boolean unloadWorld(String world) {
        if (plugin.getServer().getWorlds().contains(plugin.getServer().getWorld(world))) {
            if (!ConfigShorts.getDefConfig().getString("SpawnWorld").equals(world)) {
                if (plugin.getServer().getWorld(world) != null) {
                    World spawnWorld = plugin.getServer().getWorld(ConfigShorts.getDefConfig().getString("SpawnWorld"));
                    if (spawnWorld == null) {
                        spawnWorld = plugin.getServer().getWorlds().get(0);
                    }
                    for (Player player : plugin.getServer().getWorld(world).getPlayers()) {
                        player.teleportAsync(spawnWorld.getSpawnLocation());
                    }
                }
                return plugin.getServer().unloadWorld(world, true);
            } else {
                return false;
            }
        } else {
            System.out.println(ANSI_RED + "Tried to unload a world VSkyblock does not know about or which is already unloaded." + ANSI_RESET);
            return false;
        }
    }

    /**
     * Loads a world.
     * @param world
     * @return boolean
     */
    public boolean loadWorld(String world) {
        if (getAllWorlds().contains(world)) {
            if (getUnloadedWorlds().contains(world)) {
                WorldCreator wc = new WorldCreator(world);
                wc.generator(getGenerator(world));
                wc.environment(getEnvironment(world));
                wc.type(WorldType.FLAT);
                wc.generateStructures(generateStructures(world));
                World loadedworld = wc.createWorld();
                if (loadedworld != null) {
                    loadedworld.setDifficulty(getDifficulty(loadedworld.getName()));
                    loadedworld.setKeepSpawnInMemory(keepSpawnInMemory(world));
                    return true;
                } else {
                    System.out.println(ANSI_RED + "VSkyblock failed to load world " + world + ANSI_RESET);
                    return false;
                }
            } else {
                return getLoadedWorlds().contains(world);
            }
        } else {
            System.out.println(ANSI_RED + "VSkyblock does not know about the world " + world + ANSI_RESET);
            return false;
        }
    }

    /**
     * Gets the world generator from the config.
     *
     * Returns null if the configs value has been manipulated.
     * @param world
     * @return String
     */
    private String getGenerator(String world) {
        String option = ConfigShorts.getWorldConfig().getString("Worlds." + world + ".generator");
        if (option != null && option.equals("default")) {
            return null;
        }
        return option;
    }

    /**
     * Gets the worlds environment from the config.
     *
     * Returns the NORMAL environment if the configs value has been manipulated.
     *
     * @param world
     * @return World.Environment
     */
    private World.Environment getEnvironment(String world) {
        try {
            return World.Environment.valueOf(ConfigShorts.getWorldConfig().getString("Worlds." + world + ".environment", "NORMAL").toUpperCase());
        } catch (IllegalArgumentException e) {
            return World.Environment.NORMAL;
        }
    }

    /**
     * Deletes an world.
     * @param world
     * @return boolean
     */
    public boolean deleteWorld(String world) {
        if (getAllWorlds().contains(world)) {
            if (loadWorld(world)) {
                World delete = plugin.getServer().getWorld(world);
                File deleteFolder = delete.getWorldFolder();
                if (unloadWorld(world)) {

                    if(deleteFolder.exists()) {
                        File files[] = deleteFolder.listFiles();

                        for (File file : files) {
                            if (file.isDirectory()) {
                                for (File file2 : file.listFiles()) {
                                    file2.delete();
                                }
                            }
                            file.delete();
                        }
                    }
                    deleteWorldfromConfig(world);
                    return(deleteFolder.delete());
                } else {
                    System.out.println(ANSI_RED + "Could not delete world " + world + ANSI_RESET);
                    return false;
                }
            } else {
                System.out.println(ANSI_RED + "Could not delete world " + world + ANSI_RESET);
                return false;
            }
        } else {
            System.out.println(ANSI_RED + "VSkyblock does not know about this world: " + world + ANSI_RESET);
            return false;
        }
    }

    /**
     * Returns the spawn location from a world. (from the config)
     * Loads the world if unloaded.
     * @param world
     * @return Location
     */
    public Location getSpawnLocation(String world) {
        return getSpawnLocation(world, true);
    }

    /**
     * Returns the spawn location from a world. (from the config)
     * @param world
     * @param load Whether or not to load the world if it's unloaded
     * @return Location
     */
    public Location getSpawnLocation(String world, boolean load) {
        Set<String> worlds = getAllWorlds();
        if (worlds.contains(world)) {
            if (load && getUnloadedWorlds().contains(world)) {
                loadWorld(world);
            }
            World world1 = plugin.getServer().getWorld(world);
            double x = ConfigShorts.getWorldConfig().getDouble("Worlds." + world + ".spawnLocation.x");
            double y = ConfigShorts.getWorldConfig().getDouble("Worlds." + world + ".spawnLocation.y");
            double z = ConfigShorts.getWorldConfig().getDouble("Worlds." + world + ".spawnLocation.z");
            float yaw = (float) ConfigShorts.getWorldConfig().getDouble("Worlds." + world + ".spawnLocation.yaw");
            float pitch = (float) ConfigShorts.getWorldConfig().getDouble("Worlds." + world + ".spawnLocation.pitch");
            return new Location(world1, x, y, z, yaw, pitch);
        } else {
            System.out.println(ANSI_RED + "Could not find a spawn location for world " + world + "!" + ANSI_RESET);
            return null;
        }
    }

    /**
     * Returns a list of all unloaded worlds.
     * @return List
     */
    public Set<String> getUnloadedWorlds() {
        List<World> loadedWorlds = plugin.getServer().getWorlds();
        List<String> worlds = new ArrayList<>();
        for (World world : loadedWorlds) {
            worlds.add(world.getName());
        }
        Set<String> allWorlds = getAllWorlds();
        Set<String> unloadedworlds = new HashSet<>();
        for (String currentworld : allWorlds) {
            if (!worlds.contains(currentworld)) {
                unloadedworlds.add(currentworld);
            }
        }
        return unloadedworlds;
    }

    /**
     * Returns a list of all loaded worlds.
     * @return List
     */
    public List<String> getLoadedWorlds() {

        List<World> loadedWorlds = plugin.getServer().getWorlds();
        List<String> worlds = new ArrayList<>();
        for (World world : loadedWorlds) {
            worlds.add(world.getName());
        }
        return worlds;
    }

    /**
     * Returns a list of all worlds.
     * @return List
     */
    public Set<String> getAllWorlds() {
        if (ConfigShorts.getWorldConfig().isConfigurationSection("Worlds")) {
            return ConfigShorts.getWorldConfig().getConfigurationSection("Worlds").getKeys(false);
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Adds a world to the config with the default settings.
     * @param world
     */
    public boolean addWorld(String world, String generator, String environment) {
        ConfigurationSection worldConfig = ConfigShorts.getWorldConfig().createSection("Worlds." + world);
        worldConfig.set("generator", generator);
        worldConfig.set("environment", environment);
        try {
            ConfigShorts.getWorldConfig().save();
            return true;
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Problem storing world " + world + ANSI_RESET);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a world from the config.
     * @param world
     */
    public void deleteWorldfromConfig(String world) {
        ConfigShorts.getWorldConfig().set("Worlds." + world, null);
        try {
            ConfigShorts.getWorldConfig().save();
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Problem deleting world " + world + ANSI_RESET);
            e.printStackTrace();
        }
    }

    /**
     * Sets an option in the "Worlds.yml" file.
     * @param world
     * @param option
     * @param value
     */
    public void setOption(String world, String option, String value) {
        ConfigurationSection worldConfig = ConfigShorts.getWorldConfig().getConfigurationSection("Worlds." + world);
        if (worldConfig == null) {
            System.out.println(ANSI_RED + "World " + world + " is not known?" + ANSI_RESET);
            return;
        }
        worldConfig.set(option, value);
        try {
            ConfigShorts.getWorldConfig().save();
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Problem storing option " + option + ": " + value + " for world " + world + ANSI_RESET);
            e.printStackTrace();
        }
    }

    /**
     * Sets a new spawn location for a world (into the config).
     * @param loc
     */
    public boolean setSpawnLocation(Location loc) {
        ConfigurationSection worldConfig = ConfigShorts.getWorldConfig().getConfigurationSection("Worlds." + loc.getWorld().getName());
        if (worldConfig == null) {
            System.out.println(ANSI_RED + "World " + loc.getWorld().getName() + " is not known?" + ANSI_RESET);
            return false;
        }
        ConfigurationSection spawnSection = worldConfig.createSection("spawnLocation");
        spawnSection.set("x", loc.getX());
        spawnSection.set("y", loc.getY());
        spawnSection.set("z", loc.getZ());
        spawnSection.set("yaw", loc.getYaw());
        spawnSection.set("pitch", loc.getPitch());
        try {
            ConfigShorts.getWorldConfig().save();
            return true;
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Problem storing spawn location of world " + loc.getWorld().getName() + ANSI_RESET);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if an island is autoLoaded.
     *
     * @param world
     * @return boolean
     */
    public boolean getAutoLoad(String world) {
        return ConfigShorts.getWorldConfig().getBoolean("Worlds." + world + ".autoLoad", false);
    }

    /**
     * Gets the difficulty for the world
     *
     * @param world
     * @return String
     */
    public Difficulty getDifficulty(String world) {
        try {
            return Difficulty.valueOf(ConfigShorts.getWorldConfig().getString("Worlds." + world + ".difficulty", "NORMAL"));
        } catch (IllegalArgumentException e) {
            return Difficulty.NORMAL;
        }
    }

    /**
     * Returns the keepSpawnInMemory boolean from the config.
     *
     * @param world
     * @return boolean
     */
    public boolean keepSpawnInMemory(String world) {
        return ConfigShorts.getWorldConfig().getBoolean("Worlds." + world + ".keepSpawnInMemory", false);
    }

    /**
     * Returns the generateStructures boolean from the config.
     *
     * @param world
     * @return boolean
     */
    public boolean generateStructures(String world) {
        return ConfigShorts.getWorldConfig().getBoolean("Worlds." + world + ".generateStructures", false);
    }

    /**
     * Returns a String with all Infos for a given world.
     *
     * @param world
     * @return String
     */
    public String getWorldInformation(String world) {
        if (getAllWorlds().contains(world)) {
            String difficulty = getDifficulty(world).name();
            String generator = getGenerator(world);
            boolean autoLoad = getAutoLoad(world);
            String environment = getEnvironment(world).name();
            boolean keepSpawnInMemory = keepSpawnInMemory(world);
            boolean generateStructures = generateStructures(world);

            Location spawnloc = getSpawnLocation(world, false);
            double spawnlocX = spawnloc.getX();
            double spawnlocY = spawnloc.getY();
            double spawnlocZ = spawnloc.getZ();
            double spawnlocPitch = spawnloc.getPitch();
            double spawnlocYaw = spawnloc.getYaw();

            String worldInfo = ChatColor.AQUA + "----- " + world + " -----" + "\n" + "\n" +
                    ChatColor.GOLD + "Difficulty: " + ChatColor.RESET + difficulty + "\n" +
                    ChatColor.GOLD + "Generator: " + ChatColor.RESET + generator + "\n" +
                    ChatColor.GOLD + "AutoLoad: " + ChatColor.RESET + autoLoad + "\n" +
                    ChatColor.GOLD + "Environment: " + ChatColor.RESET + environment + "\n" +
                    ChatColor.GOLD + "KeepSpawnInMemory: " + ChatColor.RESET + keepSpawnInMemory + "\n" +
                    ChatColor.GOLD + "GenerateStructures: " + ChatColor.RESET + generateStructures + "\n" + "\n" +

                    ChatColor.AQUA + "----- Spawn -----" + "\n" +
                    ChatColor.GOLD + "X: " + ChatColor.RESET + spawnlocX + "\n" +
                    ChatColor.GOLD + "Y: " + ChatColor.RESET + spawnlocY + "\n" +
                    ChatColor.GOLD + "Z: " + ChatColor.RESET + spawnlocZ + "\n" +
                    ChatColor.GOLD + "Pitch: " + ChatColor.RESET + spawnlocPitch + "\n" +
                    ChatColor.GOLD + "Yaw: " + ChatColor.RESET + spawnlocYaw + "\n";
            return worldInfo;
        } else {
            return world;
        }
    }
}