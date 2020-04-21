package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class ConfigShorts {

    private static VSkyblock plugin = VSkyblock.getInstance();

    private static File configFile;
    private static FileConfiguration config;

    private static File messagesFile;
    private static FileConfiguration messagesConfig;

    private static File challengesFile;
    private static FileConfiguration challengesConfig;

    private static File helpFile;
    private static FileConfiguration helpConfig;

    private static File worldsFile;
    private static FileConfiguration worldsConfig;

    private static File optionsFile;
    private static FileConfiguration optionsConfig;


    /**
     * Sends a message from the config.yml file to a player.
     *
     * @param string
     * @param sender
     */
    public static void messagefromString(String string, CommandSender sender) {

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
        try {
            plugin.getConfig().load(plugin.getDataFolder() + "/config.yml");
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        String actualLanguage = plugin.getConfig().getString("Language");
        if (actualLanguage != null) {
            if (actualLanguage.equalsIgnoreCase("ger")) {
                return "ger";
            }
        }
        return "eng";
    }

    public static void loadMessagesConfig() throws IOException, InvalidConfigurationException {

        if (getLanguage().equals("ger")) {
            messagesFile = new File(plugin.getDataFolder() + "/Languages", "ger.yml");
        } else {
            messagesFile = new File(plugin.getDataFolder() + "/Languages", "eng.yml");
        }
        messagesConfig = new YamlConfiguration();
        messagesConfig.load(messagesFile);
    }

    public static void loadChallengesConfig1() throws IOException, InvalidConfigurationException {

        if (getLanguage().equals("ger")) {
            challengesFile = new File(plugin.getDataFolder() + "/Challenges", "ChallengesGer.yml");
        } else {
            challengesFile = new File(plugin.getDataFolder() + "/Challenges", "ChallengesEng.yml");
        }
        challengesConfig = new YamlConfiguration();
        challengesConfig.load(challengesFile);
    }

    public static void loadHelpConfig1() throws IOException, InvalidConfigurationException {

        if (getLanguage().equals("ger")) {
            helpFile = new File(plugin.getDataFolder() + "/Help", "HelpGer.yml");
        } else {
            helpFile = new File(plugin.getDataFolder() + "/Help", "HelpEng.yml");
        }
        helpConfig = new YamlConfiguration();
        helpConfig.load(helpFile);
    }

    public static void loadOptionsConfig1() throws IOException, InvalidConfigurationException {

        if (getLanguage().equals("ger")) {
            optionsFile = new File(plugin.getDataFolder() + "/Options", "OptionsGer.yml");
        } else {
            optionsFile = new File(plugin.getDataFolder() + "/Options", "OptionsEng.yml");
        }
        optionsConfig = new YamlConfiguration();
        optionsConfig.load(optionsFile);
    }

    public static void loadConfig() throws IOException, InvalidConfigurationException {

        configFile = new File(plugin.getDataFolder() + "/config.yml");
        config = new YamlConfiguration();
        config.load(configFile);
    }

    public static void reloadWorldConfig1() throws IOException, InvalidConfigurationException {

        worldsFile = new File(plugin.getDataFolder() + "/Worlds.yml");
        worldsConfig = new YamlConfiguration();
        worldsConfig.load(worldsFile);
    }

    public static void reloadAllConfigs() {
        try {
            loadMessagesConfig();
            loadChallengesConfig1();
            loadHelpConfig1();
            loadOptionsConfig1();
            loadConfig();
            reloadWorldConfig1();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static FileConfiguration getWorldConfig() {
        return worldsConfig;
    }

    public static FileConfiguration getDefConfig() {
        return config;
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
