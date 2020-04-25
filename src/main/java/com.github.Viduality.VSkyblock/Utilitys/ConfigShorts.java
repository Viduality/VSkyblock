package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.VSkyblock;
import org.apache.commons.lang.WordUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigShorts {

    private static VSkyblock plugin = VSkyblock.getInstance();

    private static StoredConfiguration messagesConfig;

    private static StoredConfiguration challengesConfig;

    private static StoredConfiguration helpConfig;

    private static StoredConfiguration worldsConfig;

    private static StoredConfiguration optionsConfig;


    /**
     * Sends a message from the config.yml file to a player.
     *
     * @param string
     * @param sender
     */
    public static void messagefromString(String string, CommandSender sender) {
        if (sender == null) {
            return;
        }

        String prefix = messagesConfig.getString("Prefix");
        String message = prefix + " " + messagesConfig.getString(string);

        sender.sendMessage(message);
    }

    /**
     * Broadcasts a custom message from the config.yml file to the server.
     *
     * @param string
     */
    public static void broadcastfromString(String string) {

        String prefix = messagesConfig.getString("Prefix");
        String message = prefix + " " + messagesConfig.getString(string);

        plugin.getServer().broadcastMessage(message);
    }

    /**
     * Broadcasts that a specific player has completed a specific challenge.
     *
     * @param string
     * @param playername
     * @param challenge
     */
    public static void broadcastChallengeCompleted(String string, String playername, String challenge) {

        String prefix = messagesConfig.getString("Prefix");
        String message = prefix + " " + messagesConfig.getString(string);

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
    }

    /**
     * Sends a custom message from the config.yml file to a player.
     * Playername and targetname will be shown in the message when included as "%Player%" or "%SecondPlayer%" in the config.yml file.
     * @param string
     * @param player
     * @param playername
     * @param targetname
     */
    public static void custommessagefromString(String string, Player player, String playername, String targetname) {

        String prefix = messagesConfig.getString("Prefix");
        String message = prefix + " " + messagesConfig.getString(string);

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
    }

    /**
     * Sends a custom message from the config.yml file to a player.
     * The replacement will be shown in the message when included as "%replacement%" in the config.yml file.
     * @param string
     * @param sender
     * @param replacement
     */
    public static void custommessagefromString(String string, CommandSender sender, String replacement) {

        String prefix = messagesConfig.getString("Prefix");
        String message = prefix + " " + messagesConfig.getString(string);

        if (message.contains("%replacement%")) {
            message = message.replace("%replacement%", replacement);
        }

        sender.sendMessage(message);
    }

    /**
     * Returns a custom String from the config and replaces "%replacement%" and "%replacement2%"
     * @param string
     * @param replacement
     * @param replacement2
     * @return custom String
     */
    public static String getCustomString(String string, String replacement, String replacement2) {

        String prefix = messagesConfig.getString("Prefix");
        String message = prefix + " " + messagesConfig.getString(string);

        if (message.contains("%replacement%")) {
            message = message.replace("%replacement%", replacement);
        }

        if (message.contains("%replacement2%")) {
            message = message.replace("%replacement2%", replacement2);
        }
        return message;
    }

    /**
     * Returns a custom String from the config
     * @param string
     * @return String
     */
    public static String getCustomString(String string) {

        String prefix = messagesConfig.getString("Prefix");
        String message = prefix + " " + messagesConfig.getString(string);

        return message;
    }

    private static String getLanguage() {
        return plugin.getConfig().getString("Language", "eng").toLowerCase();
    }

    private static void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
    }

    static void reloadWorldConfig() throws IOException, InvalidConfigurationException {
        File worldsFile = new File(plugin.getDataFolder(), "Worlds.yml");
        worldsConfig = new StoredConfiguration(worldsFile);
        worldsConfig.load(worldsFile);
    }

    private static StoredConfiguration loadConfig(String name) {
        return loadConfig(name, name);
    }

    private static StoredConfiguration loadConfig(String folderName, String configName) {
        File folder = new File(plugin.getDataFolder(), folderName);
        File file = new File(folder, (configName.isEmpty() ? getLanguage() : configName + WordUtils.capitalize(getLanguage())) + ".yml");

        FileConfiguration defaultConfig = new YamlConfiguration();

        try (InputStream stream = plugin.getResource(file.getName())) {
            if (stream != null) {
                defaultConfig.load(new InputStreamReader(stream));
                if (!file.exists()) {
                    defaultConfig.save(file);
                }
            } else {
                System.out.println("Default config " + file.getName() + " does not exist in the plugin");
            }
        } catch (IOException | InvalidConfigurationException e) {
            System.out.println("Error while ssving default config " + file.getName());
            e.printStackTrace();
        }

        if (!file.exists()) {
            file = new File(folder, (configName.isEmpty() ? "eng" : configName + "Eng.yml"));
        }

        StoredConfiguration config = new StoredConfiguration(file);
        if (file.exists()) {
            try {
                config.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                System.out.println("Encountered an error while loading " + folderName + " config from " + file.getPath());
                e.printStackTrace();
            }
        } else {
            System.out.println("No possible file for the " + folderName + " config exists? Checked for " + getLanguage() + " and eng versions.");
        }
        config.setDefaults(defaultConfig);
        return config;
    }

    public static void reloadAllConfigs() {
        try {
            loadConfig();
            messagesConfig = loadConfig("Languages", "");
            challengesConfig = loadConfig("Challenges");
            helpConfig = loadConfig("Help");
            optionsConfig = loadConfig("Options");
            reloadWorldConfig();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static StoredConfiguration getWorldConfig() {
        return worldsConfig;
    }

    public static FileConfiguration getDefConfig() {
        return plugin.getConfig();
    }

    public static FileConfiguration getMessageConfig() {
        return messagesConfig;
    }

    public static FileConfiguration getOptionsConfig() {
        return optionsConfig;
    }

    public static FileConfiguration getHelpConfig() {
        return helpConfig;
    }

    public static FileConfiguration getChallengesConfig() {
        return challengesConfig;
    }

}
