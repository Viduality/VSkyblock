package com.github.Viduality.VSkyblock.Commands;

import com.github.Viduality.VSkyblock.Listener.NetherPortalListener;
import com.github.Viduality.VSkyblock.Utilitys.ConfigShorts;
import com.github.Viduality.VSkyblock.Utilitys.PlayerInfo;
import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/*
 * Teleports to the nether portal home point.
 */
public class IslandNether extends PlayerSubCommand {

    protected IslandNether(VSkyblock plugin) {
        super(plugin, "nether");
    }

    @Override
    public void execute(CommandSender sender, PlayerInfo playerInfo, String[] args) {
        if (NetherPortalListener.teleportToNetherHome.asMap().containsKey(playerInfo.getUuid())) {
            PotionEffect potionEffect = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 20, 10);
            playerInfo.getPlayer().addPotionEffect(potionEffect);
            playerInfo.getPlayer().teleportAsync(NetherPortalListener.teleportToNetherHome.asMap().get(playerInfo.getUuid())).whenComplete((b, e) -> {
                NetherPortalListener.teleportToNetherHome.asMap().remove(playerInfo.getUuid());
                ConfigShorts.messagefromString("TeleportedToNetherHome", sender);
                if (e != null) {
                    e.printStackTrace();
                }
            });
        } else {
            ConfigShorts.messagefromString("NoPendingNetherTeleport", sender);
        }
    }
}
