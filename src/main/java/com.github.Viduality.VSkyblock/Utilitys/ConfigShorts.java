package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ConfigShorts {

    private static VSkyblock plugin = VSkyblock.getInstance();

    /**
     * Sends a message from the config.yml file to a player.
     *
     * @param String
     * @param player
     */
    public static void messagefromString(String String, Player player) {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        String actualLanguage = plugin.getConfig().getString("Language");
        String prefix;
        String message;
        if (actualLanguage.equalsIgnoreCase("ger")) {
            loadGERConfig();
            prefix = plugin.getConfig().getString("Prefix");
            message = prefix + " " + plugin.getConfig().getString(String);
        }
        else {
            loadENGConfig();
            prefix = plugin.getConfig().getString("Prefix");
            message = prefix + " " + plugin.getConfig().getString(String);
        }

        player.sendMessage(message);
        loaddefConfig();
    }

    /**
     * Broadcasts a custom message from the config.yml file to the server.
     *
     * @param String
     */
    public static void broadcastfromString(String String) {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        String actualLanguage = plugin.getConfig().getString("Language");
        String prefix;
        String message;
        if (actualLanguage.equalsIgnoreCase("ger")) {
            loadGERConfig();
            prefix = plugin.getConfig().getString("Prefix");
            message = prefix + " " + plugin.getConfig().getString(String);
        }
        else {
            loadENGConfig();
            prefix = plugin.getConfig().getString("Prefix");
            message = prefix + " " + plugin.getConfig().getString(String);
        }

        plugin.getServer().broadcastMessage(message);
        loaddefConfig();
    }

    /**
     * Broadcasts that a specific player has completed a specific challenge.
     *
     * @param String
     * @param playername
     * @param challenge
     */
    public static void broadcastChallengeCompleted(String String, String playername, String challenge) {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        String actualLanguage = plugin.getConfig().getString("Language");
        String prefix;
        String message;
        if (actualLanguage.equalsIgnoreCase("ger")) {
            loadGERConfig();
            prefix = plugin.getConfig().getString("Prefix");
            message = prefix + " " + plugin.getConfig().getString(String);
        }
        else {
            loadENGConfig();
            prefix = plugin.getConfig().getString("Prefix");
            message = prefix + " " + plugin.getConfig().getString(String);
        }

        if (message.contains("%Player")) {
            message = message.replace("%Player%", playername);
            if (message.contains("%Challenge%")) {
                message = message.replace("%Challenge%", challenge);
            }
        }
        if (message.contains("%Challenge%")) {
            message = message.replace("%Challenge%", challenge);

        }

        plugin.getServer().broadcastMessage(message);
        loaddefConfig();
    }

    /**
     * Sends a custom message from the config.yml file to a player.
     * Playername and targetname will be shown in the message when included as "%Player%" or "%SecondPlayer%" in the config.yml file.
     * @param String
     * @param player
     * @param playername
     * @param targetname
     */
    public static void custommessagefromString(String String, Player player, String playername, String targetname) {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        String actualLanguage = plugin.getConfig().getString("Language");
        String prefix;
        String message;
        if (actualLanguage.equalsIgnoreCase("ger")) {
            loadGERConfig();
            prefix = plugin.getConfig().getString("Prefix");
            message = prefix + " " + plugin.getConfig().getString(String);
        }
        else {
            loadENGConfig();
            prefix = plugin.getConfig().getString("Prefix");
            message = prefix + " " + plugin.getConfig().getString(String);
        }

        if (message.contains("%Player")) {
            message = message.replace("%Player%", playername);
            if (message.contains("%SecondPlayer%")) {
                message = message.replace("%SecondPlayer%", targetname);
            }
        }
        if (message.contains("%SecondPlayer%")) {
            message = message.replace("%SecondPlayer%", targetname);

        }
        player.sendMessage(message);
        loaddefConfig();
    }

    /**
     * Sends a custom message from the config.yml file to a player.
     * The replacement will be shown in the message when included as "%replacement%" in the config.yml file.
     * @param String
     * @param player
     * @param replacement
     */
    public static void custommessagefromString(String String, Player player, String replacement) {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        String actualLanguage = plugin.getConfig().getString("Language");
        String prefix;
        String message;
        if (actualLanguage.equalsIgnoreCase("ger")) {
            loadGERConfig();
            prefix = plugin.getConfig().getString("Prefix");
            message = prefix + " " + plugin.getConfig().getString(String);
        }
        else {
            loadENGConfig();
            prefix = plugin.getConfig().getString("Prefix");
            message = prefix + " " + plugin.getConfig().getString(String);
        }

        if (message.contains("%replacement%")) {
            message = message.replace("%replacement%", replacement);
        }

        player.sendMessage(message);
        loaddefConfig();
    }

    public static String getCustomString(String String, String replacement, String replacement2) {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        String actualLanguage = plugin.getConfig().getString("Language");
        String prefix;
        String message;
        if (actualLanguage.equalsIgnoreCase("ger")) {
            loadGERConfig();
            prefix = plugin.getConfig().getString("Prefix");
            message = prefix + " " + plugin.getConfig().getString(String);
        }
        else {
            loadENGConfig();
            prefix = plugin.getConfig().getString("Prefix");
            message = prefix + " " + plugin.getConfig().getString(String);
        }

        if (message.contains("%replacement%")) {
            message = message.replace("%replacement%", replacement);
        }

        if (message.contains("%replacement2%")) {
            message = message.replace("%replacement2%", replacement2);
        }
        loaddefConfig();
        return message;
    }

    public static String getCustomString(String String) {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        String actualLanguage = plugin.getConfig().getString("Language");
        String prefix;
        String message;
        if (actualLanguage.equalsIgnoreCase("ger")) {
            loadGERConfig();
            prefix = plugin.getConfig().getString("Prefix");
            message = prefix + " " + plugin.getConfig().getString(String);
        }
        else {
            loadENGConfig();
            prefix = plugin.getConfig().getString("Prefix");
            message = prefix + " " + plugin.getConfig().getString(String);
        }
        loaddefConfig();
        return message;
    }

    public static String getPrefix() {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        String actualLanguage = plugin.getConfig().getString("Language");
        String prefix;
        if (actualLanguage.equalsIgnoreCase("ger")) {
            loadGERConfig();
            prefix = plugin.getConfig().getString("Prefix") + " ";
        }
        else {
            loadENGConfig();
            prefix = plugin.getConfig().getString("Prefix") + " ";

        }
        return prefix;
    }

    /**
     * Loads the help config.
     * Language depends on the given language in the config.yml file.
     */
    public static void loadHelpConfig() {
        loaddefConfig();
        String actualLanguage = plugin.getConfig().getString("Language");
        if (actualLanguage.equalsIgnoreCase("ger")) {
            loadGERHelp();
        } else {
            loadENGHelp();
        }
    }

    /**
     * Loads the german config file.
     */
    private static void loadGERConfig() {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/Languages/ger.yml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the english config file.
     */
    private static void loadENGConfig() {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/Languages/eng.yml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the german help file.
     */
    private static void loadGERHelp() {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/Help/HelpGer.yml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the english help file.
     */
    private static void loadENGHelp() {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/Help/HelpEng.yml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the default config file. (config.yml)
     */
    public static void loaddefConfig() {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the challenges config.
     * Language depends on the given language in the config.yml file.
     */
    public static void loadChallengesConfig() {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        String actualLanguage = plugin.getConfig().getString("Language");
        if (actualLanguage.equalsIgnoreCase("ger")) {
            try {
                plugin.getConfig().load(plugin.getDataFolder() + "/Challenges/ChallengesGer.yml");
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                plugin.getConfig().load(plugin.getDataFolder() + "/Challenges/ChallengesEng.yml");
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads the teleporter config.
     * Language depends on the given language in the config.yml file.
     */
    public static void loadTeleporterConfig() {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        String actualLanguage = plugin.getConfig().getString("Language");
        if (actualLanguage.equalsIgnoreCase("ger")) {
            try {
                plugin.getConfig().load(plugin.getDataFolder() + "/Teleporter/TeleporterGer.yml");
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                plugin.getConfig().load(plugin.getDataFolder() + "/Teleporter/TeleporterEng.yml");
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadWorldConfig() {
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/Worlds.yml");
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

}
