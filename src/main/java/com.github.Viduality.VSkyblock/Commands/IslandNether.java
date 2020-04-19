package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Listener.NetherPortalListener;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.DatabaseCache;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IslandNether implements SubCommand {


    @Override
    public void execute(DatabaseCache databaseCache) {
        if (NetherPortalListener.teleportToNetherHome.asMap().containsKey(databaseCache.getUuid())) {
            PotionEffect potionEffect = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 20, 10);
            databaseCache.getPlayer().addPotionEffect(potionEffect);
            databaseCache.getPlayer().teleport(NetherPortalListener.teleportToNetherHome.asMap().get(databaseCache.getUuid()));
            NetherPortalListener.teleportToNetherHome.asMap().remove(databaseCache.getUuid());
            ConfigShorts.messagefromString("TeleportedToNetherHome", databaseCache.getPlayer());
        } else {
            ConfigShorts.messagefromString("NoPendingNetherTeleport", databaseCache.getPlayer());
        }
    }
}
