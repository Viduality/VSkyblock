package com.github.Viduality.VSkyblock.Utilitys;

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

import com.github.Viduality.VSkyblock.Challenges.Challenge;
import com.github.Viduality.VSkyblock.Challenges.ChallengesInventoryCreator;
import com.github.Viduality.VSkyblock.VSkyblock;
import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.chat.*;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public static void broadcastChallengeCompleted(String string, String playername, Challenge challenge) {

        String prefix = messagesConfig.getString("Prefix");
        String message = prefix + " " + messagesConfig.getString(string);
        String hover = createHover(challenge);
        String hoverableChallenge = "[&6" + challenge.getChallengeName() + "]" + "(hover=" + hover + ")";

        BaseComponent[] message1 = MineDown.parse(message, "Player", playername, "Challenge", hoverableChallenge);

        plugin.getServer().broadcast(new MineDown(MineDown.stringify(message1)).toComponent());
    }

    private static String createHover(Challenge challenge) {
        List<String> lore = new ArrayList<>();
        lore.add(ChallengesInventoryCreator.loreString);
        lore.addAll(splitString(challenge.getDescription(), ChallengesInventoryCreator.descriptioncolor));
        switch (challenge.getChallengeType()) {
            case onPlayer:
                lore.add(ChallengesInventoryCreator.neededonPlayer);
                break;
            case onIsland:
                lore.add(ChallengesInventoryCreator.neededonIsland);
                break;
            case islandLevel:
                lore.add(ChallengesInventoryCreator.neededislandlevel);
                break;
        }
        lore.addAll(splitString(challenge.getNeededText(), ChallengesInventoryCreator.descriptioncolor));

        StringBuilder hover = new StringBuilder();
        for (String loreLine : lore) {
            hover.append(loreLine);
            if (!loreLine.equals(lore.get(lore.size() - 1))) {
                hover.append("\n");
            }
        }
        return String.valueOf(hover);
    }

    /**
     * Splits an string.
     *
     * @param string
     * @param colorCode
     * @return List of String
     */
    private static List<String> splitString(String string, String colorCode) {
        List<String> wordbyword = new ArrayList<>();
        if (string.length() < 30) {
            wordbyword.add(string);
        } else {
            wordbyword = Arrays.asList(string.split(" "));
        }

        List<String> splittedString = new ArrayList<>();
        int i = 0;
        String currentLine = null;
        for (String word : wordbyword) {
            i = i + word.length();
            if (i > 30) {
                if (word.length() >= 30) {
                    splittedString.add(currentLine);
                    splittedString.add(word);
                    i = 0;
                    currentLine = null;
                } else {
                    splittedString.add(colorCode + currentLine);
                    currentLine = word;
                    i = word.length();
                }
            } else {
                if (currentLine == null) {
                    currentLine = word;
                } else {
                    currentLine = currentLine + " " + word;
                }
            }
            if (wordbyword.get(wordbyword.size() - 1).equals(word)) {
                splittedString.add(colorCode + currentLine);
            }
        }
        return splittedString;
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
