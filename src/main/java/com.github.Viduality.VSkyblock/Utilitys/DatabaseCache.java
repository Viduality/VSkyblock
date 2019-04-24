package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DatabaseCache {


    private VSkyblock plugin = VSkyblock.getInstance();



    private String uuidcache = null;
    private String name = null;
    private int islandid = 0;
    private boolean islandowner = false;
    private String islandowneruuid = null;
    private boolean kicked = false;
    private String islandname = null;
    private int islandLevel = 0;
    private List<String> islandmembers = new ArrayList<>();
    private String arg = null;

    private Player player;
    private OfflinePlayer offlinePlayer;




    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public OfflinePlayer getTargetPlayer() {
        return offlinePlayer;
    }

    public void setTargetPlayer(OfflinePlayer targetPlayer) {
        this.offlinePlayer = plugin.getServer().getOfflinePlayer(String.valueOf(targetPlayer));
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    public String getArg() {
        return arg;
    }


    //////////////
    // NOT USED //
    //////////////
    public enum Operation {
        ISLAND, ACCEPT, INVITE, KICK, LEAVE, LEAVECONFIRM, LEVEL, MEMBERS, RESTART, RESTARTCONFIRM, SETHOME, SETOWNER;


        public static Operation getOperation(String key) {
            for (Operation operation : Operation.values()) {
                if (operation.name().equalsIgnoreCase(key)) {
                    return operation;
                }
            }
            return null;
        }


        public static boolean needsValue(Operation operation) {
            switch (operation) {
                case KICK:
                case SETOWNER:
                case INVITE:
                    return true;
                case SETHOME:
                case RESTARTCONFIRM:
                case RESTART:
                case MEMBERS:
                case LEVEL:
                case LEAVECONFIRM:
                case LEAVE:
                case ACCEPT:
                case ISLAND:
                    return false;
            }
            return false;
        }
    }






















    // UUID
    public void setUuid(String uuid) {
        this.uuidcache = uuid;
    }

    public String getuuid() {
        return uuidcache;
    }



    // Name
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    //islandID
    public void setIslandId(int islandid) {
        this.islandid = islandid;
    }

    public int getIslandId() {
        return islandid;
    }



    // Islandowner
    public void setIslandowner(boolean islandowner) {
        this.islandowner = islandowner;
    }

    public boolean isIslandowner() {
        return islandowner;
    }


    //islandownerUUID
    public void setIslandowneruuid(String islandowneruuid) {
        this.islandowneruuid = islandowneruuid;
    }

    public String getIslandowneruuid() {
        return islandowneruuid;
    }


    // Kicked?
    public void setKicked(boolean kicked) {
        this.kicked = kicked;
    }

    public boolean isKicked() {
        return kicked;
    }


    // Island name
    public void setIslandname(String islandname) {
        this.islandname = islandname;
    }

    public String getIslandname() {
        return islandname;
    }


    // Island level
    public void setIslandLevel(int islandLevel) {
        this.islandLevel = islandLevel;
    }

    public int getIslandLevel() {
        return islandLevel;
    }


    // Island members
    public void resetislandmembers() {
        islandmembers.clear();
    }

    public void addIslandMember(String uuid) {
        islandmembers.add(uuid);
    }

    public List<String> getislandmembers() {
        return islandmembers;
    }
}
