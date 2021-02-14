package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.VSkyblock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.logging.Level;

public class ConfigChanger {

    private final VSkyblock plugin ;

    public ConfigChanger(VSkyblock plugin) {
        this.plugin = plugin;
    }

    /**
     * Changes parameter in the config.yml file.
     *
     * @param string
     * @param newString
     */
    public void setConfig(String string, String newString) {

        String inputStr = null;


        try {
            // input the file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(new File(plugin.getDataFolder(), "config.yml")));
            String line;
            StringBuffer inputBuffer = new StringBuffer();

            while ((line = file.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
                inputStr = inputBuffer.toString();

                file.close();

                /// Whoever reads the following lines... Please, don't blame me for it, i know this is absolut botched xD

            if (inputStr.contains(string)) {
                    String OldString = ConfigShorts.getDefConfig().getString(string);
                    String replace1 = string + ":";
                    String with1 = string + ": " + newString;
                    String OldLine = string + ": " + OldString;
                    String NewLine = string + ": " + newString;
                    if (OldString == null) {
                        inputStr = inputStr.replace(replace1, with1);
                    } else {
                        inputStr = inputStr.replace(OldLine, NewLine);
                    }
            } else plugin.getLogger().warning("Keine Zeile in der Config gefunden!");

                // write the new String with the replaced line OVER the same file
                FileOutputStream fileOut = new FileOutputStream(new File(plugin.getDataFolder(), "config.yml"));
                fileOut.write(inputStr.getBytes());
                fileOut.close();

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Problem reading file.", e);
        }
    }
}
