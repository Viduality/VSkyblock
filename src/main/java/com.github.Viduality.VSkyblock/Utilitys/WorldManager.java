package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.*;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.omg.CORBA.Environment;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class WorldManager {

    private VSkyblock plugin = VSkyblock.getInstance();


    /**
     * Creates a new island. Also writes it into the Worlds.yml config file
     * @param island
     */
    public boolean createIsland(String island) {

        //Check if the world doesn't already exists
        if (!getAllWorlds().contains(island)) {
            File dir = new File(plugin.getServer().getWorldContainer().getAbsolutePath() + "/" + island);
            if (!dir.exists()) {
                dir.mkdirs();

                File source = new File(plugin.getServer().getWorldContainer().getAbsolutePath() + "/VSkyblockMasterIsland");

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
                if (addWorld(island, "VSkyblock", "NORMAL")) {
                    return true;
                } else {
                    System.out.println("Could not add world to config!");
                    return false;
                }
            } else {
                System.out.println("Folder already exists!");
            }
        } else {
            System.out.println(ChatColor.RED + "Tried to create save a world but VSkyblock already knows about it!");
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
            if (plugin.getServer().getWorld(world).getPlayers() != null) {
                for (Player player : plugin.getServer().getWorld(world).getPlayers()) {
                    player.teleport(plugin.getServer().getWorld(plugin.getConfig().getString("SpawnWorld")).getSpawnLocation());
                }
            }
            plugin.getServer().unloadWorld(world, true);
            return true;
        } else {
            System.out.println(ChatColor.RED + "Tried to unload a world VSkyblock does not know about. :(");
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
                wc.generateStructures(false);
                World loadedworld = wc.createWorld();
                plugin.getServer().getWorlds().add(loadedworld);
                return true;
            } else {
                if (getLoadedWorlds().contains(world)) {
                    return true;
                }
            }
        } else {
            System.out.println(ChatColor.RED + "VSkyblock does not know about this world!");
            return false;
        }
        return false;
    }

    /**
     * Gets the world generator from the config.
     *
     * Returns null if the configs value has been manipulated.
     * @param world
     * @return String
     */
    private String getGenerator(String world) {
        ConfigShorts.loadWorldConfig();
        String option = plugin.getConfig().getString("Worlds." + world + "generator");
        ConfigShorts.loaddefConfig();
        if (option != null) {
            if (option.equals("default")) {
                return null;
            } else {
                return option;
            }
        } else {
            return null;
        }
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
        ConfigShorts.loadWorldConfig();
        String option = plugin.getConfig().getString("Worlds." + world + "environment");
        ConfigShorts.loaddefConfig();
        if (option != null) {
            String env = option.toUpperCase();
            switch (env) {
                case "NETHER":
                    return World.Environment.NETHER;
                case "THE_END":
                    return World.Environment.THE_END;
                default:
                    return World.Environment.NORMAL;
            }
        } else {
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
                if (unloadWorld(world)) {
                    File deleteFolder = delete.getWorldFolder();

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
                    System.out.println("§cCould not delete world " + world);
                    return false;
                }
            } else {
                System.out.println("§cCould not delete world " + world);
                return false;
            }
        } else {
            System.out.println("§cVSkyblock does not know about this world!");
            return false;
        }
    }

    /**
     * Returns the spawn location from a world (from the config)
     * @param world
     * @return Location
     */
    public Location getSpawnLocation(String world) {
        ConfigShorts.loadWorldConfig();
        List<String> worlds = getAllWorlds();
        if (worlds.contains(world)) {
            if (getUnloadedWorlds().contains(world)) {
                loadWorld(world);
            }
            World world1 = plugin.getServer().getWorld(world);
            double x = plugin.getConfig().getDouble("Worlds." + world + ".spawnLocation.x");
            double y = plugin.getConfig().getDouble("Worlds." + world + ".spawnLocation.y");
            double z = plugin.getConfig().getDouble("Worlds." + world + ".spawnLocation.z");
            float yaw = (float) plugin.getConfig().getDouble("Worlds." + world + ".spawnLocation.yaw");
            float pitch = (float) plugin.getConfig().getDouble("Worlds." + world + ".spawnLocation.pitch");
            ConfigShorts.loaddefConfig();
            return new Location(world1, x, y, z, yaw, pitch);
        } else {
            System.out.println(ChatColor.RED + "Could not find a spawn location for world " + world + "!");
            return null;
        }
    }

    /**
     * Returns a list of all unloaded worlds.
     * @return List
     */
    public List<String> getUnloadedWorlds() {
        List<World> loadedWorlds = plugin.getServer().getWorlds();
        List<String> worlds = new ArrayList<>();
        for (World world : loadedWorlds) {
            worlds.add(world.getName());
        }
        List<String> allWorlds = getAllWorlds();
        List<String> unloadedworlds = new ArrayList<>();
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
    public List<String> getAllWorlds() {
        ConfigShorts.loadWorldConfig();
        Set<String> allworlds = plugin.getConfig().getConfigurationSection("Worlds").getKeys(false);
        ConfigShorts.loaddefConfig();
        return new ArrayList<>(allworlds);
    }

    /**
     * Adds a world to the config with the default settings.
     * @param world
     */
    public boolean addWorld(String world, String generator, String environment) {
        try {
            InputStream templateStream = plugin.getResource("WorldTemplate.yml");
            StringBuilder out = new StringBuilder();
            final char[] buffer = new char[0x10000];

            Reader in = new InputStreamReader(templateStream);
            int read;
            do {
                read = in.read(buffer, 0, buffer.length);
                if (read>0) {
                    out.append(buffer, 0, read);
                }
            } while (read>=0);
            String template = out.toString();
            in.close();
            template = template.replace("WorldName", world);
            template = template.replace("default", generator);
            template = template.replace("NORMAL", environment);


            // input the file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(plugin.getDataFolder() + "/Worlds.yml"));
            String line;
            StringBuffer inputBuffer = new StringBuffer();

            while ((line = file.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            inputBuffer.append(template);
            file.close();

            String worldsFile = inputBuffer.toString();

            FileOutputStream fileOut = new FileOutputStream(plugin.getDataFolder() + "/Worlds.yml");
            fileOut.write(worldsFile.getBytes());
            fileOut.close();
            return true;

        } catch (Exception e) {
            System.out.println("Problem reading file.");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a world from the config.
     * @param world
     */
    public void deleteWorldfromConfig(String world) {

        ConfigShorts.loadWorldConfig();

        String inputStr = null;
        String worldinfoString = null;

        try {
            // input the file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(plugin.getDataFolder() + "/Worlds.yml"));
            String line;
            StringBuffer inputBuffer = new StringBuffer();
            StringBuffer worldinfo = new StringBuffer();
            int i = 0;

            Object[] test = plugin.getConfig().getConfigurationSection("Worlds." + world).getKeys(true).toArray();
            String lastPart = null;
            for (Object out : test) {
                lastPart = out.toString();
            }

            while ((line = file.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append('\n');
                if (line.contains(world)) {
                    i = 1;
                }
                if (i == 1) {
                    worldinfo.append(line);
                    worldinfo.append('\n');
                    if (line.contains(lastPart)) {
                        i = 0;
                    }
                }
            }
            inputStr = inputBuffer.toString();
            worldinfoString = worldinfo.toString();
            file.close();

            inputStr = inputStr.replace(worldinfoString, "");


            FileOutputStream fileOut = new FileOutputStream(plugin.getDataFolder() + "/Worlds.yml");
            fileOut.write(inputStr.getBytes());
            fileOut.close();

            ConfigShorts.loaddefConfig();

        } catch (Exception e) {
            System.out.println("Problem reading file.");
            e.printStackTrace();
        }
    }

    /**
     * Sets an option in the "Worlds.yml" file.
     * @param world
     * @param string
     * @param option
     */
    public void setOption(String world, String string, String option) {

        ConfigShorts.loadWorldConfig();

        String inputStr = null;
        String worldinfoString = null;

        try {
            // input the file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(plugin.getDataFolder() + "/Worlds.yml"));
            String line;
            StringBuffer inputBuffer = new StringBuffer();
            StringBuffer worldinfo = new StringBuffer();
            int i = 0;

            while ((line = file.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append('\n');
                if (line.contains(world)) {
                    i = 1;
                }
                if (i == 1) {
                    worldinfo.append(line);
                    worldinfo.append('\n');
                    if (line.contains(string)) {
                        i = 0;
                    }
                }
            }
            inputStr = inputBuffer.toString();
            worldinfoString = worldinfo.toString();
            String newWorldInfo = worldinfoString;
            file.close();

            if (newWorldInfo.contains(string)) {
                String oldString = plugin.getConfig().getString("Worlds." + world + "." + string);
                String replace1 = string + ":";
                String with1 = string + ": " + option;
                String OldLine = string + ": " + oldString;
                String NewLine = string + ": " + option;
                if (oldString == null) {
                    newWorldInfo = newWorldInfo.replace(replace1, with1);
                } else {
                    newWorldInfo = newWorldInfo.replace(OldLine, NewLine);
                }
            } else {
                System.out.println("Keine Zeile in der Config gefunden!");
            }


            inputStr = inputStr.replace(worldinfoString, newWorldInfo);


            FileOutputStream fileOut = new FileOutputStream(plugin.getDataFolder() + "/Worlds.yml");
            fileOut.write(inputStr.getBytes());
            fileOut.close();

            ConfigShorts.loaddefConfig();

        } catch (Exception e) {
            System.out.println("Problem reading file.");
            e.printStackTrace();
        }
    }

    /**
     * Sets a new spawn location for a world (into the config).
     * @param loc
     */
    public boolean setSpawnLocation(Location loc) {

        ConfigShorts.loadWorldConfig();
        List<String> worlds = getAllWorlds();

        if (worlds.contains(loc.getWorld().getName())) {
            String world = loc.getWorld().getName();
            double x = loc.getX();
            double y = loc.getY();
            double z = loc.getZ();
            float yaw = loc.getYaw();
            float pitch = loc.getPitch();



            List<String> locInfo = Arrays.asList("x", "y", "z", "yaw", "pitch");
            List<String> locValues = Arrays.asList(String.valueOf(x), String.valueOf(y), String.valueOf(z), String.valueOf(yaw), String.valueOf(pitch));


            String inputStr = null;
            String worldinfoString = null;

            try {
                // input the file content to the StringBuffer "input"
                BufferedReader file = new BufferedReader(new FileReader(plugin.getDataFolder() + "/Worlds.yml"));
                String line;
                StringBuffer inputBuffer = new StringBuffer();
                StringBuffer worldinfo = new StringBuffer();
                int i = 0;

                while ((line = file.readLine()) != null) {
                    inputBuffer.append(line);
                    inputBuffer.append('\n');
                    if (line.contains(world)) {
                        i = 1;
                    }
                    if (i == 1) {
                        worldinfo.append(line);
                        worldinfo.append('\n');
                        if (line.contains("yaw")) {
                            i = 0;
                        }
                    }
                }
                inputStr = inputBuffer.toString();
                worldinfoString = worldinfo.toString();
                String newWorldInfo = worldinfoString;
                file.close();

                for (int a = 0; a < locInfo.size(); a++) {
                    String currentData = locInfo.get(a);
                    if (newWorldInfo.contains(currentData)) {
                        String oldString = plugin.getConfig().getString("Worlds." + world + ".spawnLocation" + "." + currentData);
                        String replace1 = currentData + ":";
                        String with1 = currentData + ": " + locValues.get(a);
                        String OldLine = currentData + ": " + oldString;
                        String NewLine = currentData + ": " + locValues.get(a);
                        if (oldString == null) {
                            newWorldInfo = newWorldInfo.replace(replace1, with1);
                        } else {
                            newWorldInfo = newWorldInfo.replace(OldLine, NewLine);
                        }
                    } else {
                        System.out.println("Keine Zeile in der Config gefunden!");
                    }
                }


                inputStr = inputStr.replace(worldinfoString, newWorldInfo);


                FileOutputStream fileOut = new FileOutputStream(plugin.getDataFolder() + "/Worlds.yml");
                fileOut.write(inputStr.getBytes());
                fileOut.close();

                ConfigShorts.loaddefConfig();
            } catch (Exception e) {
                System.out.println("Problem reading file.");
                e.printStackTrace();
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Checks if an island is autoLoaded.
     *
     * @param world
     * @return boolean
     */
    public boolean getAutoLoad(String world) {
        ConfigShorts.loadWorldConfig();
        boolean autoLoad = false;
        if (plugin.getConfig().getString("Worlds." + world + ".autoLoad").equals("true")) {
            autoLoad = plugin.getConfig().getBoolean("Worlds." + world + ".autoLoad");
        }
        ConfigShorts.loaddefConfig();
        return autoLoad;
    }
}