package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.Utilitys.WorldManager;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
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
            if (player.getWorld().getEnvironment() != World.Environment.NETHER && !player.getWorld().getName().equals(Island.playerislands.get(player.getUniqueId().toString()))) {
                entityDamageEvent.setCancelled(true);
                if (entityDamageEvent.getCause() == EntityDamageEvent.DamageCause.VOID || player.getLocation().getY() < 0) {
                    player.setFallDistance(0);
                    player.teleport(wm.getSpawnLocation(plugin.getConfig().getString("SpawnWorld")));
                }
            }
        }
    }



    @EventHandler
    public void entityprotector(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        Entity entity = entityDamageByEntityEvent.getDamager();
        Player player = null;
        if (entity instanceof Player) {
            player = (Player) entity;
        } else if (entity instanceof Projectile && ((Projectile) entity).getShooter() instanceof Player) {
            player = (Player) ((Projectile) entity).getShooter();
        } else if (entity instanceof TNTPrimed) {
            player = getSource((TNTPrimed) entity);
        }

        if (player != null) {
            String uuid = player.getUniqueId().toString();
            if (!entity.getWorld().getName().equals(Island.playerislands.get(uuid)) && entity.getWorld().getEnvironment() != World.Environment.NETHER) {
                if (!player.hasPermission("VSkyblock.IgnoreProtected")) {
                    entityDamageByEntityEvent.setCancelled(true);
                }
            }
        }
    }

    private Player getSource(TNTPrimed tnt) {
        if (tnt.getSource() != null && tnt.getSource().isValid()) {
            if (tnt.getSource() instanceof Player) {
                return (Player) tnt.getSource();
            } else if (tnt.getSource() instanceof Projectile) {
                return getShooter((Projectile) tnt.getSource());
            } else if (tnt.getSource() instanceof TNTPrimed) {
                return getSource(tnt);
            }
        }
        return null;
    }

    private Player getShooter(Projectile projectile) {
        if (projectile.getShooter() instanceof Player) {
            return (Player) projectile.getShooter();
        }
        return null;
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
                inventoryOpenEvent.setCancelled(true);
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
