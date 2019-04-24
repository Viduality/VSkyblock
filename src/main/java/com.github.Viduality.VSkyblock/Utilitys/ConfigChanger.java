package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.VSkyblock;

import java.io.*;

public class ConfigChanger {

    private VSkyblock plugin = VSkyblock.getInstance();

    /**
     * Changes parameter in the config.yml file.
     *
     * @param string
     * @param newString
     */
    public void setConfig(String string, String newString) {

        ConfigShorts.loaddefConfig();
        String inputStr = null;


        try {
            // input the file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(plugin.getDataFolder() + "/config.yml"));
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
                    String OldString = plugin.getConfig().getString(string);
                    String replace1 = string + ":";
                    String with1 = string + ": " + newString;
                    String OldLine = string + ": " + OldString;
                    String NewLine = string + ": " + newString;
                    if (OldString == null) {
                        inputStr = inputStr.replace(replace1, with1);
                    } else {
                        inputStr = inputStr.replace(OldLine, NewLine);
                    }
            } else System.out.println("Keine Zeile in der Config gefunden!");

                // write the new String with the replaced line OVER the same file
                FileOutputStream fileOut = new FileOutputStream(plugin.getDataFolder() + "/config.yml");
                fileOut.write(inputStr.getBytes());
                fileOut.close();

        } catch (Exception e) {
                System.out.println("Problem reading file.");
                e.printStackTrace();
        }
    }
}
