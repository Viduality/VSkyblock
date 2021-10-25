package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Listener.NetherPortalListener;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IslandNether implements SubCommand {


    @Override
    public void execute(ExecutionInfo execution) {
        PlayerInfo playerInfo = execution.getPlayerInfo();
        if (NetherPortalListener.teleportToNetherHome.asMap().containsKey(playerInfo.getUuid())) {
            PotionEffect potionEffect = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 20, 10);
            playerInfo.getPlayer().addPotionEffect(potionEffect);
            playerInfo.getPlayer().teleportAsync(NetherPortalListener.teleportToNetherHome.asMap().get(playerInfo.getUuid())).whenComplete((b, e) -> {
                NetherPortalListener.teleportToNetherHome.asMap().remove(playerInfo.getUuid());
                ConfigShorts.messagefromString("TeleportedToNetherHome", playerInfo.getPlayer());
                if (e != null) {
                    e.printStackTrace();
                }
            });
        } else {
            ConfigShorts.messagefromString("NoPendingNetherTeleport", playerInfo.getPlayer());
        }
    }
}
