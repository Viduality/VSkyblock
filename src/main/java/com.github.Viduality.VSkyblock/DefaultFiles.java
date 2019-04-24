package com.github.Viduality.VSkyblock;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DefaultFiles {


    private static VSkyblock plugin = VSkyblock.getInstance();

    private static File configFile;
    private static FileConfiguration config;


    private static File engFile;
    private static FileConfiguration eng;


    private static File gerFile;
    private static FileConfiguration ger;


    private static File gerChallenges;
    private static FileConfiguration gerC;

    private static File engChallenges;
    private static FileConfiguration engC;

    private static File gerTeleporter;
    private static FileConfiguration gerT;

    private static File engTeleporter;
    private static FileConfiguration engT;

    private static File gerHelp;
    private static FileConfiguration gerH;

    private static File engHelp;
    private static FileConfiguration engH;

    private static File blockValues;
    private static FileConfiguration blockValuesConfig;

    public static HashMap<Enum, Double> blockvalues = new HashMap<>();

    /**
     * Check default Files
     * Checks resources for an existing file and creates them if they do not exist.
     */
    public static void init() {
        {
            configFile = new File(plugin.getDataFolder(), "config.yml");
            try {
                firstRun();
            } catch (Exception e) {
                e.printStackTrace();
            }

            config = new YamlConfiguration();

        }


        {
            engFile = new File(plugin.getDataFolder() + "/Languages", "eng.yml");
            try {
                engfirstRun();
            } catch (Exception e) {
                e.printStackTrace();
            }

            eng = new YamlConfiguration();

        }




        {
            gerFile = new File(plugin.getDataFolder() + "/Languages", "ger.yml");
            try {
                gerfirstRun();
            } catch (Exception e) {
                e.printStackTrace();
            }

            ger = new YamlConfiguration();
        }



        {
            gerChallenges = new File(plugin.getDataFolder() + "/Challenges", "ChallengesGer.yml");
            try {
                gerChallengesfirstRun();
            } catch (Exception e) {
                e.printStackTrace();
            }

            gerC = new YamlConfiguration();
        }


        {
            engChallenges = new File(plugin.getDataFolder() + "/Challenges", "ChallengesEng.yml");
            try {
                engChallengesfirstRun();
            } catch (Exception e) {
                e.printStackTrace();
            }

            engC = new YamlConfiguration();
        }


        {
            gerHelp = new File(plugin.getDataFolder() + "/Help", "HelpGer.yml");
            try {
                gerHelpfirstRun();
            } catch (Exception e) {
                e.printStackTrace();
            }

            gerH = new YamlConfiguration();
        }


        {
            engHelp = new File(plugin.getDataFolder() + "/Help", "HelpEng.yml");
            try {
                engHelpfirstRun();
            } catch (Exception e) {
                e.printStackTrace();
            }

            engH = new YamlConfiguration();
        }


        {
            gerTeleporter = new File(plugin.getDataFolder() + "/Teleporter", "TeleporterGer.yml");
            try {
                gerTeleporterFirstRun();
            } catch (Exception e) {
                e.printStackTrace();
            }

            gerT = new YamlConfiguration();
        }


        {
            engTeleporter = new File(plugin.getDataFolder() + "/Teleporter", "TeleporterEng.yml");
            try {
                engTeleporterFirstRun();
            } catch (Exception e) {
                e.printStackTrace();
            }

            engT = new YamlConfiguration();
        }


        {
            blockValues = new File(plugin.getDataFolder(), "BlockValues.yml");
            try {
                blockValuesfirstRun();
            } catch (Exception e) {
                e.printStackTrace();
            }

            blockValuesConfig = new YamlConfiguration();
        }



        loadYamls();

        loadValues();





    }

    /**
     * Checks if config.yml exists and creates it if not.
     *
     * @throws Exception
     */
    private static void firstRun() throws Exception {
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            copy(plugin.getResource("config.yml"), configFile);
        }
    }

    /**
     * Checks if eng.yml exists and creates it if not.
     *
     * @throws Exception
     */
    private static void engfirstRun() throws Exception {
        if (!engFile.exists()) {
            engFile.getParentFile().mkdirs();
            copy(plugin.getResource("eng.yml"), engFile);
        }
    }

    /**
     * Checks if ger.yml exists and creates it if not.
     *
     * @throws Exception
     */
    private static void gerfirstRun() throws Exception {
        if (!gerFile.exists()) {
            gerFile.getParentFile().mkdirs();
            copy(plugin.getResource("ger.yml"), gerFile);
        }
    }

    /**
     * Checks if ChallengesGer.yml exists and creates it if not.
     *
     * @throws Exception
     */
    private static void gerChallengesfirstRun() throws Exception {
        if (!gerChallenges.exists()) {
            gerChallenges.getParentFile().mkdirs();
            copy(plugin.getResource("ChallengesGer.yml"), gerChallenges);
        }
    }

    /**
     * Checks if ChallengesEng.yml exists and creates it if not.
     *
     * @throws Exception
     */
    private static void engChallengesfirstRun() throws Exception {
        if (!engChallenges.exists()) {
            engChallenges.getParentFile().mkdirs();
            copy(plugin.getResource("ChallengesEng.yml"), engChallenges);
        }
    }

    /**
     * Checks if HelpGer.yml exists and creates it if not.
     *
     * @throws Exception
     */
    private static void gerHelpfirstRun() throws Exception {
        if (!gerHelp.exists()) {
            gerHelp.getParentFile().mkdirs();
            copy(plugin.getResource("HelpGer.yml"), gerHelp);
        }
    }

    /**
     * Checks if HelpEng.yml exists and creates it if not.
     *
     * @throws Exception
     */
    private static void engHelpfirstRun() throws Exception {
        if (!engHelp.exists()) {
            engHelp.getParentFile().mkdirs();
            copy(plugin.getResource("HelpEng.yml"), engHelp);
        }
    }

    /**
     * Checks if TeleporterGer.yml exists and creates it if not.
     *
     * @throws Exception
     */
    private static void gerTeleporterFirstRun() throws Exception {
        if (!gerTeleporter.exists()) {
            gerTeleporter.getParentFile().mkdirs();
            copy(plugin.getResource("TeleporterGer.yml"), gerTeleporter);
        }
    }

    /**
     * Checks if TeleporterEng.yml exists and creates it if not.
     *
     * @throws Exception
     */
    private static void engTeleporterFirstRun() throws Exception {
        if (!engTeleporter.exists()) {
            engTeleporter.getParentFile().mkdirs();
            copy(plugin.getResource("TeleporterEng.yml"), engTeleporter);
        }
    }

    /**
     * Checks if BlockValues.yml exists and creates it if not.
     *
     * @throws Exception
     */
    private static void blockValuesfirstRun() throws Exception {
        if (!blockValues.exists()) {
            blockValues.getParentFile().mkdirs();
            copy(plugin.getResource("BlockValues.yml"), blockValues);
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
     * Tries to load all YAMLs once.
     *
     * @throws Exception
     */
    private static void loadYamls() {
        try {
            config.load(configFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            eng.load(engFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ger.load(gerFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            gerC.load(gerChallenges);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            engC.load(engChallenges);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            blockValuesConfig.load(blockValues);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the BlockValues.yml file
     */
    private static void loadValues() {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/BlockValues.yml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }


        try {
            BufferedReader file = new BufferedReader(new FileReader(plugin.getDataFolder() + "/BlockValues.yml"));
            String line;
            Integer linenumber = 0;


            while ((line = file.readLine()) != null) {
                linenumber++;
                if (line.contains(":")) {
                    List<String> pseudomaterial = Arrays.asList(line.split(":"));
                    String configString = pseudomaterial.get(0);
                    String material = pseudomaterial.get(0).toUpperCase();
                    if (Material.getMaterial(material) != null) {
                        if (isDouble(plugin.getConfig().getString(configString))) {
                            Double value = plugin.getConfig().getDouble(configString);
                            blockvalues.put(Material.getMaterial(material), value);
                        }
                        else {
                            System.out.println("Line: " + linenumber + ", Material: " + material + " has an invalid value!");
                        }
                    }
                    else {
                        System.out.println("Line: " + linenumber + ", Material: " + material + " is not a valid material!");
                    }
                }
            }

            file.close();


        } catch (Exception e) {
            System.out.println("Problem reading file.");
            e.printStackTrace();
        }
    }

    /**
     * Checks if a String is from type Double
     *
     * @param String
     * @return boolean
     */
    private static boolean isDouble(String String) {
        try {
            Double.parseDouble(String);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
