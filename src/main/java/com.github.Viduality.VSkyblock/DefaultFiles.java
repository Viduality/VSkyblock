package com.github.Viduality.VSkyblock;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;

public class DefaultFiles {


    private static VSkyblock plugin = VSkyblock.getInstance();

    public static Map<Material, Double> blockvalues = new EnumMap<>(Material.class);

    /**
     * Check default Files
     * Checks resources for an existing file and creates them if they do not exist.
     */
    public static void init() {
        saveDefaultConfig("Worlds.yml");
        saveDefaultConfig("BlockValues.yml");

        reloadBlockValues();


    }

    private static void saveDefaultConfig(String configName) {
        File configFile = new File(plugin.getDataFolder(), configName);
        try {
            if (!configFile.exists()) {
                plugin.getDataFolder().mkdirs();
                copy(plugin.getResource(configName), configFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Copies a resource file needed into an OutputStream.
     *
     * @param in
     * @param file
     */
    private static void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len=in.read(buf))>0) {
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the BlockValues.yml file
     */
    public static void reloadBlockValues() {
        File configFile = new File(plugin.getDataFolder(), "BlockValues.yml");
        if (!configFile.exists()) {
            System.out.println("BlockValues.yml does not exist!");
            return;
        }
        if (!blockvalues.isEmpty()) {
            blockvalues.clear();
        }
        FileConfiguration blockValuesConfig = new YamlConfiguration();
        try {
            blockValuesConfig.load(configFile);

            for (String configString : blockValuesConfig.getKeys(false)) {
                String material = configString.toUpperCase();
                if (Material.getMaterial(material) != null) {
                    double value = blockValuesConfig.getDouble(configString, -1);
                    if (value > -1) {
                        blockvalues.put(Material.getMaterial(material), value);
                    }
                    else {
                        System.out.println("Material: " + material + " has an invalid block value (" + blockValuesConfig.get(configString) + ")");
                    }
                }
                else {
                    System.out.println("Material string " + configString + " is not a valid material!");
                }
            }

        } catch (Exception e) {
            System.out.println("Problem reading block values file.");
            e.printStackTrace();
        }
    }
}
