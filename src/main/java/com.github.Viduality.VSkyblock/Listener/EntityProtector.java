package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.EnumSet;
import java.util.Set;


public class EntityProtector implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();
    private WorldManager wm = new WorldManager();


    @EventHandler
    public void onPVP(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        Entity player = entityDamageByEntityEvent.getDamager();
        Entity damagedplayer = entityDamageByEntityEvent.getEntity();
        boolean pvpisland = plugin.getConfig().getBoolean("PvPIslands");
        boolean pvpnether = plugin.getConfig().getBoolean("PvPNether");
        if (player.getType().equals(EntityType.PLAYER) && damagedplayer.getType().equals(EntityType.PLAYER)) {
            if (Island.playerislands.get(player.getUniqueId().toString()) != null) {
                if (player.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                    if (!pvpnether) {
                        entityDamageByEntityEvent.setCancelled(true);
                    }
                } else {
                    if (!Island.playerislands.get(player.getUniqueId().toString()).equals(Island.playerislands.get(damagedplayer.getUniqueId().toString()))) {
                        entityDamageByEntityEvent.setCancelled(true);
                    }
                    if (!pvpisland) {
                        entityDamageByEntityEvent.setCancelled(true);
                    }
                }
            } else {
                entityDamageByEntityEvent.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void islandVisitProtection(EntityDamageEvent entityDamageEvent) {
        Entity entity = entityDamageEvent.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (Island.playerislands.get(player.getUniqueId().toString()) != null) {
                if (!player.getWorld().getEnvironment().equals(World.Environment.NETHER) && !Island.playerislands.get(player.getUniqueId().toString()).equals(player.getWorld().getName())) {
                    if (entity.getLocation().getBlockY() > 0) {
                        entityDamageEvent.setCancelled(true);
                    } else {
                        double playerhealth = player.getHealth();
                        player.setHealth(playerhealth + 4);
                        player.teleport(wm.getSpawnLocation(plugin.getConfig().getString("SpawnWorld")));
                    }
                }
            } else {
                if (entity.getLocation().getBlockY() > 0) {
                    entityDamageEvent.setCancelled(true);
                } else {
                    double playerhealth = player.getHealth();
                    player.setHealth(playerhealth + 4);
                    player.teleport(wm.getSpawnLocation(plugin.getConfig().getString("SpawnWorld")));
                }
            }
        }
    }



    @EventHandler
    public void entityprotector(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        Entity entity = entityDamageByEntityEvent.getDamager();
        Entity damagedentity = entityDamageByEntityEvent.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            String uuid = player.getUniqueId().toString();
            if (!entity.getWorld().getName().equals(Island.playerislands.get(uuid)) && !entity.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
                    entityDamageByEntityEvent.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void targetPlayer(EntityTargetEvent entityTargetEvent) {
        Entity entity = entityTargetEvent.getTarget();
        if (entity instanceof Player) {
            String uuid = entity.getUniqueId().toString();
            if (!entity.getWorld().getName().equals(Island.playerislands.get(uuid)) && !entity.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                entityTargetEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void inventoryListener(InventoryOpenEvent inventoryOpenEvent) {
        Player player = (Player) inventoryOpenEvent.getPlayer();
        if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
            if (!player.getWorld().getName().equals(Island.playerislands.get(player.getUniqueId().toString())) && !player.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                if (inventoryOpenEvent.getInventory().getType().equals(InventoryType.MERCHANT)) {
                    inventoryOpenEvent.setCancelled(true);
                }
            }
        }
    }


    private static final Set<EntityType> hostilemobs = EnumSet.of(
            EntityType.BLAZE,
            EntityType.CREEPER,
            EntityType.DROWNED,
            EntityType.ELDER_GUARDIAN,
            EntityType.GUARDIAN,
            EntityType.ENDERMITE,
            EntityType.EVOKER,
            EntityType.EVOKER_FANGS,
            EntityType.GHAST,
            EntityType.HUSK,
            EntityType.MAGMA_CUBE,
            EntityType.PHANTOM,
            EntityType.SHULKER,
            EntityType.SILVERFISH,
            EntityType.SKELETON,
            EntityType.SLIME,
            EntityType.STRAY,
            EntityType.VEX,
            EntityType.VINDICATOR,
            EntityType.WITCH,
            EntityType.WITHER_SKELETON,
            EntityType.WITHER,
            EntityType.ZOMBIE,
            EntityType.ZOMBIE_VILLAGER,
            EntityType.ILLUSIONER,
            EntityType.CAVE_SPIDER,
            EntityType.ENDERMAN,
            EntityType.SPIDER,
            EntityType.PIG_ZOMBIE

    );
}
