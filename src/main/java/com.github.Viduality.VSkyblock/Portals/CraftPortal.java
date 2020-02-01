package com.github.Viduality.VSkyblock.Portals;

import com.github.Viduality.VSkyblock.VSkyblock;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class CraftPortal {

    private VSkyblock plugin = VSkyblock.getInstance();

    // TODO implement portals
    // NOT IN USE YET
    public void createPortalRecipe() {
        ItemStack portalFrame = new ItemStack(Material.END_PORTAL_FRAME);
        ItemMeta portalFramemeta = portalFrame.getItemMeta();
        portalFramemeta.setDisplayName(ChatColor.DARK_PURPLE + "Teleporter");
        portalFramemeta.addEnchant(Enchantment.DURABILITY, 1, false);
        portalFramemeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        portalFrame.setItemMeta(portalFramemeta);

        NamespacedKey portalFrameKey = NamespacedKey.minecraft("vskyblockportalframe");
        ShapedRecipe portal = new ShapedRecipe(portalFrameKey, portalFrame);
        portal.shape("ded", "oeo", "sss");
        portal.setIngredient('e', Material.ENDER_EYE);
        portal.setIngredient('d', Material.DIAMOND);
        portal.setIngredient('o', Material.OBSIDIAN);
        portal.setIngredient('s', Material.END_STONE);
        plugin.getServer().addRecipe(portal);
    }
}
