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

import com.github.Viduality.VSkyblock.Commands.Challenges.Challenge;
import com.github.Viduality.VSkyblock.Commands.Challenges.Challenges;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChallengesConverter {

    private final VSkyblock plugin = VSkyblock.getInstance();

    private final DatabaseWriter databaseWriter = new DatabaseWriter();

    public void convertAllChallenges() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String ANSI_RED = "\u001B[31m";
            String ANSI_RESET = "\u001B[0m";
            System.out.println(ANSI_RED + "Converting Challenges to new Database..." + ANSI_RESET);
            Connection connection = plugin.getdb().getConnection();
            List<Integer> islands = new ArrayList<>();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM VSkyblock_Island");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    islands.add(resultSet.getInt("islandid"));
                }
                preparedStatement.close();
                if (!islands.isEmpty()) {
                    for (int currentisland : islands) {
                        System.out.println("Converting Challenges of Island " + currentisland + "...");
                        List<String> uuids = new ArrayList<>();
                        PreparedStatement getMembers = connection.prepareStatement("SELECT * FROM VSkyblock_Player WHERE islandid = ?");
                        getMembers.setInt(1, currentisland);
                        ResultSet membersResult = getMembers.executeQuery();
                        while (membersResult.next()) {
                            uuids.add(membersResult.getString("uuid"));
                        }

                        if (!uuids.isEmpty()) {

                            ChallengesCache currentIslandChallengesEasy = new ChallengesCache();
                            ChallengesCache currentIslandChallengesMedium = new ChallengesCache();
                            ChallengesCache currentIslandChallengesHard = new ChallengesCache();
                            for (String currentUUID : uuids) {
                                try {
                                    PreparedStatement getChallengesEasy;
                                    getChallengesEasy = connection.prepareStatement("SELECT * FROM VSkyblock_Challenges_Easy WHERE uuid = ?");
                                    getChallengesEasy.setString(1, currentUUID);
                                    ResultSet rEasy = getChallengesEasy.executeQuery();
                                    while (rEasy.next()) {
                                        currentIslandChallengesEasy.setc1(currentIslandChallengesEasy.getc1() + rEasy.getInt("c1"));
                                        currentIslandChallengesEasy.setc2(currentIslandChallengesEasy.getc2() + rEasy.getInt("c2"));
                                        currentIslandChallengesEasy.setc3(currentIslandChallengesEasy.getc3() + rEasy.getInt("c3"));
                                        currentIslandChallengesEasy.setc4(currentIslandChallengesEasy.getc4() + rEasy.getInt("c4"));
                                        currentIslandChallengesEasy.setc5(currentIslandChallengesEasy.getc5() + rEasy.getInt("c5"));
                                        currentIslandChallengesEasy.setc6(currentIslandChallengesEasy.getc6() + rEasy.getInt("c6"));
                                        currentIslandChallengesEasy.setc7(currentIslandChallengesEasy.getc7() + rEasy.getInt("c7"));
                                        currentIslandChallengesEasy.setc8(currentIslandChallengesEasy.getc8() + rEasy.getInt("c8"));
                                        currentIslandChallengesEasy.setc9(currentIslandChallengesEasy.getc9() + rEasy.getInt("c9"));
                                        currentIslandChallengesEasy.setc10(currentIslandChallengesEasy.getc10() + rEasy.getInt("c10"));
                                        currentIslandChallengesEasy.setc11(currentIslandChallengesEasy.getc11() + rEasy.getInt("c11"));
                                        currentIslandChallengesEasy.setc12(currentIslandChallengesEasy.getc12() + rEasy.getInt("c12"));
                                        currentIslandChallengesEasy.setc13(currentIslandChallengesEasy.getc13() + rEasy.getInt("c13"));
                                        currentIslandChallengesEasy.setc14(currentIslandChallengesEasy.getc14() + rEasy.getInt("c14"));
                                        currentIslandChallengesEasy.setc15(currentIslandChallengesEasy.getc15() + rEasy.getInt("c15"));
                                        currentIslandChallengesEasy.setc16(currentIslandChallengesEasy.getc16() + rEasy.getInt("c16"));
                                        currentIslandChallengesEasy.setc17(currentIslandChallengesEasy.getc17() + rEasy.getInt("c17"));
                                        currentIslandChallengesEasy.setc18(currentIslandChallengesEasy.getc18() + rEasy.getInt("c18"));
                                    }

                                    PreparedStatement getChallengesMedium;
                                    getChallengesMedium = connection.prepareStatement("SELECT * FROM VSkyblock_Challenges_Medium WHERE uuid = ?");
                                    getChallengesMedium.setString(1, currentUUID);
                                    ResultSet rMedium = getChallengesMedium.executeQuery();
                                    while (rMedium.next()) {
                                        currentIslandChallengesMedium.setc1(currentIslandChallengesMedium.getc1() + rMedium.getInt("c1"));
                                        currentIslandChallengesMedium.setc2(currentIslandChallengesMedium.getc2() + rMedium.getInt("c2"));
                                        currentIslandChallengesMedium.setc3(currentIslandChallengesMedium.getc3() + rMedium.getInt("c3"));
                                        currentIslandChallengesMedium.setc4(currentIslandChallengesMedium.getc4() + rMedium.getInt("c4"));
                                        currentIslandChallengesMedium.setc5(currentIslandChallengesMedium.getc5() + rMedium.getInt("c5"));
                                        currentIslandChallengesMedium.setc6(currentIslandChallengesMedium.getc6() + rMedium.getInt("c6"));
                                        currentIslandChallengesMedium.setc7(currentIslandChallengesMedium.getc7() + rMedium.getInt("c7"));
                                        currentIslandChallengesMedium.setc8(currentIslandChallengesMedium.getc8() + rMedium.getInt("c8"));
                                        currentIslandChallengesMedium.setc9(currentIslandChallengesMedium.getc9() + rMedium.getInt("c9"));
                                        currentIslandChallengesMedium.setc10(currentIslandChallengesMedium.getc10() + rMedium.getInt("c10"));
                                        currentIslandChallengesMedium.setc11(currentIslandChallengesMedium.getc11() + rMedium.getInt("c11"));
                                        currentIslandChallengesMedium.setc12(currentIslandChallengesMedium.getc12() + rMedium.getInt("c12"));
                                        currentIslandChallengesMedium.setc13(currentIslandChallengesMedium.getc13() + rMedium.getInt("c13"));
                                        currentIslandChallengesMedium.setc14(currentIslandChallengesMedium.getc14() + rMedium.getInt("c14"));
                                        currentIslandChallengesMedium.setc15(currentIslandChallengesMedium.getc15() + rMedium.getInt("c15"));
                                        currentIslandChallengesMedium.setc16(currentIslandChallengesMedium.getc16() + rMedium.getInt("c16"));
                                        currentIslandChallengesMedium.setc17(currentIslandChallengesMedium.getc17() + rMedium.getInt("c17"));
                                        currentIslandChallengesMedium.setc18(currentIslandChallengesMedium.getc18() + rMedium.getInt("c18"));
                                    }

                                    PreparedStatement getChallengesHard;
                                    getChallengesHard = connection.prepareStatement("SELECT * FROM VSkyblock_Challenges_Hard WHERE uuid = ?");
                                    getChallengesHard.setString(1, currentUUID);
                                    ResultSet rHard = getChallengesHard.executeQuery();
                                    while (rHard.next()) {
                                        currentIslandChallengesHard.setc1(currentIslandChallengesHard.getc1() + rHard.getInt("c1"));
                                        currentIslandChallengesHard.setc2(currentIslandChallengesHard.getc2() + rHard.getInt("c2"));
                                        currentIslandChallengesHard.setc3(currentIslandChallengesHard.getc3() + rHard.getInt("c3"));
                                        currentIslandChallengesHard.setc4(currentIslandChallengesHard.getc4() + rHard.getInt("c4"));
                                        currentIslandChallengesHard.setc5(currentIslandChallengesHard.getc5() + rHard.getInt("c5"));
                                        currentIslandChallengesHard.setc6(currentIslandChallengesHard.getc6() + rHard.getInt("c6"));
                                        currentIslandChallengesHard.setc7(currentIslandChallengesHard.getc7() + rHard.getInt("c7"));
                                        currentIslandChallengesHard.setc8(currentIslandChallengesHard.getc8() + rHard.getInt("c8"));
                                        currentIslandChallengesHard.setc9(currentIslandChallengesHard.getc9() + rHard.getInt("c9"));
                                        currentIslandChallengesHard.setc10(currentIslandChallengesHard.getc10() + rHard.getInt("c10"));
                                        currentIslandChallengesHard.setc11(currentIslandChallengesHard.getc11() + rHard.getInt("c11"));
                                        currentIslandChallengesHard.setc12(currentIslandChallengesHard.getc12() + rHard.getInt("c12"));
                                        currentIslandChallengesHard.setc13(currentIslandChallengesHard.getc13() + rHard.getInt("c13"));
                                        currentIslandChallengesHard.setc14(currentIslandChallengesHard.getc14() + rHard.getInt("c14"));
                                        currentIslandChallengesHard.setc15(currentIslandChallengesHard.getc15() + rHard.getInt("c15"));
                                        currentIslandChallengesHard.setc16(currentIslandChallengesHard.getc16() + rHard.getInt("c16"));
                                        currentIslandChallengesHard.setc17(currentIslandChallengesHard.getc17() + rHard.getInt("c17"));
                                        currentIslandChallengesHard.setc18(currentIslandChallengesHard.getc18() + rHard.getInt("c18"));
                                    }

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                            PreparedStatement updateStatement = connection.prepareStatement("INSERT IGNORE INTO VSkyblock_Challenges(islandid, count, challenge) VALUES (?, ?, ?)");
                            convertChallenges(currentisland, currentIslandChallengesEasy, Challenges.sortedChallengesEasy, updateStatement);
                            convertChallenges(currentisland, currentIslandChallengesMedium, Challenges.sortedChallengesMedium, updateStatement);
                            convertChallenges(currentisland, currentIslandChallengesHard, Challenges.sortedChallengesHard, updateStatement);
                            updateStatement.executeBatch();
                        }
                    }
                }
                try {
                    PreparedStatement renameOldTables = connection.prepareStatement("RENAME TABLE VSkyblock_Challenges_Easy TO VSkyblock_Challenges_EasyOLDTABLE");
                    renameOldTables.executeUpdate();
                    renameOldTables = connection.prepareStatement("RENAME TABLE VSkyblock_Challenges_Medium TO VSkyblock_Challenges_MediumOLDTABLE");
                    renameOldTables.executeUpdate();
                    renameOldTables = connection.prepareStatement("RENAME TABLE VSkyblock_Challenges_Hard TO VSkyblock_Challenges_HardOLDTABLE");
                    renameOldTables.executeUpdate();
                    renameOldTables.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                System.out.println(ANSI_RED + "Converting Challenges finished" + ANSI_RESET);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } finally {
                plugin.getdb().closeConnection(connection);
            }
        });
    }

    private void convertChallenges(int islandId, ChallengesCache challengesCache, List<Challenge> challengeList, PreparedStatement updateStatement) throws SQLException {
        for (int i = 0; i < 18; i++) {
            if (challengesCache.getCurrentChallengeCount(i + 1) != 0) {
                if (challengeList.size() > i) {
                    String mysqlKey = challengeList.get(i).getMySQLKey();
                    if (mysqlKey != null) {
                        updateStatement.setInt(1, islandId);
                        updateStatement.setInt(2, challengesCache.getCurrentChallengeCount(i + 1));
                        updateStatement.setString(3, mysqlKey);
                        updateStatement.addBatch();
                    }
                }
            }
        }
    }
}
