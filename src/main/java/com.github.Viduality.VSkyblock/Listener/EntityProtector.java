package com.github.Viduality.VSkyblock.Listener;

import com.github.Viduality.VSkyblock.Commands.Island;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Entity;

import java.util.EnumSet;
import java.util.Set;


public class EntityProtector implements Listener {

    private VSkyblock plugin = VSkyblock.getInstance();


    @EventHandler
    public void onPVP(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        Entity player = entityDamageByEntityEvent.getDamager();
        Entity damagedplayer = entityDamageByEntityEvent.getEntity();
        boolean pvpisland = plugin.getConfig().getBoolean("PvPIslands");
        boolean pvpnether = plugin.getConfig().getBoolean("PvPNether");
        if (player.getType().equals(EntityType.PLAYER) && damagedplayer.getType().equals(EntityType.PLAYER)) {
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
        }
    }



    @EventHandler
    public void entityprotector(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        Entity entity = entityDamageByEntityEvent.getDamager();
        Entity damagedentity = entityDamageByEntityEvent.getEntity();
        if (entity instanceof Player) {
            String uuid = entity.getUniqueId().toString();
            if (!entity.getWorld().getName().equals(Island.playerislands.get(uuid))) {
                if (!hostilemobs.contains(damagedentity.getType()) && damagedentity.getType().equals(EntityType.PLAYER)) {
                    entityDamageByEntityEvent.setCancelled(true);
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
