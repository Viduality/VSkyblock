package com.github.Viduality.VSkyblock.Portals;

import com.github.Viduality.VSkyblock.VSkyblock;

public class CraftPortal {

    private final VSkyblock plugin;

    public CraftPortal(VSkyblock plugin) {
        this.plugin = plugin;
    }

    // TODO implement portals
    // NOT IN USE YET
    /*
    public void createPortalRecipe() {
        ItemStack portalFrame = new ItemStack(Material.END_PORTAL_FRAME);
        ItemMeta portalFramemeta = portalFrame.getItemMeta();
        portalFramemeta.setDisplayName(ChatColor.DARK_PURPLE + "Teleporter");
        portalFramemeta.addEnchant(Enchantment.DURABILITY, 1, false);
        portalFramemeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        portalFrame.setItemMeta(portalFramemeta);

        NamespacedKey portalFrameKey = new NamespacedKey(plugin, "portalframe");
        ShapedRecipe portal = new ShapedRecipe(portalFrameKey, portalFrame);
        portal.shape("ded", "oeo", "sss");
        portal.setIngredient('e', Material.ENDER_EYE);
        portal.setIngredient('d', Material.DIAMOND);
        portal.setIngredient('o', Material.OBSIDIAN);
        portal.setIngredient('s', Material.END_STONE);
        plugin.getServer().addRecipe(portal);
    }
     */

}
