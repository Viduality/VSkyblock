package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseCache {


    private VSkyblock plugin = VSkyblock.getInstance();


    private UUID playerId;
    private String name = null;
    private int islandid = 0;
    private boolean islandowner = false;
    private String islandowneruuid = null;
    private boolean kicked = false;
    private String islandname = null;
    private int islandLevel = 0;
    private List<String> islandmembers = new ArrayList<>();
    private String arg = null;
    private int deathCount = 0;
    private double spawnX;
    private double spawnY;
    private double spawnZ;
    private double spawnPitch;
    private double spawnYaw;

    private OfflinePlayer offlinePlayer;


    /**
     * Returns the player.
     * @return Player
     */
    public Player getPlayer() {
        return playerId != null ? Bukkit.getPlayer(playerId) : null;
    }

    /**
     * Sets the player.
     * @param player
     */
    public void setPlayer(Player player) {
        this.playerId = player.getUniqueId();
    }

    /**
     * Returns the target (player).
     * @return Player
     */
    public OfflinePlayer getTargetPlayer() {
        return offlinePlayer;
    }

    /**
     * Sets the target (player).
     * @param targetPlayer
     */
    public void setTargetPlayer(OfflinePlayer targetPlayer) {
        this.offlinePlayer = targetPlayer;
    }

    /**
     * Sets the argument from the chat input.
     * @param arg
     */
    public void setArg(String arg) {
        this.arg = arg;
    }

    /**
     * Returns the argument.
     * @return String
     */
    public String getArg() {
        return arg;
    }

    /**
     * Sets the uuid from the player.
     * @param uuid
     */
    public void setUuid(String uuid) {
        this.playerId = UUID.fromString(uuid);
    }

    /**
     * Returns the playerrs uuid.
     * @return String
     */
    public UUID getUuid() {
        return playerId;
    }

    /**
     * Sets the players name.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the players name.
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the id from the players island.
     * @param islandid
     */
    public void setIslandId(int islandid) {
        this.islandid = islandid;
    }

    /**
     * Returns the id from the players island.
     * @return int
     */
    public int getIslandId() {
        return islandid;
    }

    /**
     * Sets the island owner from the players island.
     * @param islandowner
     */
    public void setIslandowner(boolean islandowner) {
        this.islandowner = islandowner;
    }

    /**
     * Returns true if the player is the owner of the island where he is playing.
     * @return boolean
     */
    public boolean isIslandowner() {
        return islandowner;
    }

    /**
     * Sets the uuid from the island owner.
     * @param islandowneruuid
     */
    public void setIslandowneruuid(String islandowneruuid) {
        this.islandowneruuid = islandowneruuid;
    }

    /**
     * Returns the uuid from the island owner.
     * @return String
     */
    public String getIslandowneruuid() {
        return islandowneruuid;
    }

    /**
     * Sets a boolean in case the player has been kicked from his last island.
     * @param kicked
     */
    public void setKicked(boolean kicked) {
        this.kicked = kicked;
    }

    /**
     * Returns true if the player was kicked from his last island.
     * @return boolean
     */
    public boolean isKicked() {
        return kicked;
    }

    /**
     * Sets the island name.
     * @param islandname
     */
    public void setIslandname(String islandname) {
        this.islandname = islandname;
    }

    /**
     * Returns the island name.
     * @return String
     */
    public String getIslandname() {
        return islandname;
    }

    /**
     * Sets the islands level.
     * @param islandLevel
     */
    public void setIslandLevel(int islandLevel) {
        this.islandLevel = islandLevel;
    }

    /**
     * Returns the islands level.
     * @return int
     */
    public int getIslandLevel() {
        return islandLevel;
    }

    /**
     * Resets the list of island members.
     */
    public void resetislandmembers() {
        islandmembers.clear();
    }

    /**
     * Adds an member to the island (uuid).
     * @param uuid
     */
    public void addIslandMember(String uuid) {
        islandmembers.add(uuid);
    }

    /**
     * Returns a list of all island members.
     * @return list of all members
     */
    public List<String> getislandmembers() {
        return islandmembers;
    }

    /**
     * Sets the death count for the player.
     */
    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    /**
     * Returns the death count of the player.
     * @return deathcount
     */
    public int getDeathCount() {
        return deathCount;
    }
}
