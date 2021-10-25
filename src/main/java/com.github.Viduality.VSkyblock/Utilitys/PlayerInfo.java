package com.github.Viduality.VSkyblock.Utilitys;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerInfo {
    private UUID playerId;
    private String name = null;
    private int islandId = 0;
    private boolean isIslandOwner = false;
    private String islandOwnerUuid = null;
    private boolean kicked = false;
    private String islandName = null;
    private int islandLevel = 0;
    private String arg = null;
    private int deathCount = 0;

    private OfflinePlayer targetPlayer;


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
        this.islandId = islandid;
    }

    /**
     * Returns the id from the players island.
     * @return int
     */
    public int getIslandId() {
        return islandId;
    }

    /**
     * Sets the island owner from the players island.
     * @param islandowner
     */
    public void setIsIslandOwner(boolean islandowner) {
        this.isIslandOwner = islandowner;
    }

    /**
     * Returns true if the player is the owner of the island where he is playing.
     * @return boolean
     */
    public boolean isIslandOwner() {
        return isIslandOwner;
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
    public void setIslandName(String islandname) {
        this.islandName = islandname;
    }

    /**
     * Returns the island name.
     * @return String
     */
    public String getIslandName() {
        return islandName;
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
