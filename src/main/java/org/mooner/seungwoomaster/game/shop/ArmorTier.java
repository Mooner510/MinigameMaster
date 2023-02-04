package org.mooner.seungwoomaster.game.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;

import static org.mooner.seungwoomaster.game.gui.GUIUtils.createItem;

public enum ArmorTier {
    NONE(0, 0, null, null, null, null),
    LEATHER(200, 200,
            createItem(Material.LEATHER_HELMET, 1, "Leather Cap", "Defense: &a+2.5%"),
            createItem(Material.LEATHER_CHESTPLATE, 1, "Leather Tunic", "Defense: &a+5%"),
            createItem(Material.LEATHER_LEGGINGS, 1, "Leather Pants", "Defense: &a+5%"),
            createItem(Material.LEATHER_BOOTS, 1, "Leather Boots", "Defense: &a+2.5%")
    ),
    IRON(450, 450,
            createItem(Material.IRON_HELMET, 1, "Iron Helmet", "Defense: &a+5%"),
            createItem(Material.IRON_CHESTPLATE, 1, "Iron Chestplate", "Defense: &a+10%"),
            createItem(Material.IRON_LEGGINGS, 1, "Iron Leggings", "Defense: &a+10%"),
            createItem(Material.IRON_BOOTS, 1, "Iron Boots", "Defense: &a+5%")
    ),
    DIAMOND(800, 800,
            createItem(Material.DIAMOND_HELMET, 1, "Diamond Helmet", "Defense: &a+7.5%"),
            createItem(Material.DIAMOND_CHESTPLATE, 1, "Diamond Chestplate", "Defense: &a+15%"),
            createItem(Material.DIAMOND_LEGGINGS, 1, "Diamond Leggings", "Defense: &a+15%"),
            createItem(Material.DIAMOND_BOOTS, 1, "Diamond Boots", "Defense: &a+7.5%")
    ),
    NETHERITE(1350, 1350,
            createItem(Material.NETHERITE_HELMET, 1, "Netherite Helmet", "Defense: &a+10%"),
            createItem(Material.NETHERITE_CHESTPLATE, 1, "Netherite Chestplate", "Defense: &a+20%"),
            createItem(Material.NETHERITE_LEGGINGS, 1, "Netherite Leggings", "Defense: &a+20%"),
            createItem(Material.NETHERITE_BOOTS, 1, "Netherite Boots", "Defense: &a+10%")
    );

    @Nullable
    public static ArmorTier findByHelmet(Material material) {
        return Arrays.stream(values())
                .filter(it -> it.getHelmet().getType() == material)
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public static ArmorTier findByLeggings(Material material) {
        return Arrays.stream(values())
                .filter(it -> it.getLeggings().getType() == material)
                .findFirst()
                .orElse(null);
    }

    private final int topCost;
    private final int bottomCost;

    private final ItemStack helmet;
    private final ItemStack leggings;
    private final ItemStack boots;

    ArmorTier(int topCost, int bottomCost, ItemStack helmet, ItemStack chest, ItemStack leggings, ItemStack boots) {
        this.topCost = topCost;
        this.bottomCost = bottomCost;
        this.helmet = helmet;
        this.leggings = leggings;
        this.boots = boots;
    }

    public int getTopCost() {
        return topCost;
    }

    public int getBottomCost() {
        return bottomCost;
    }

    public ItemStack getHelmet() {
        return helmet.clone();
    }

    public ItemStack getBoots() {
        return boots.clone();
    }

    public ItemStack getLeggings() {
        return leggings.clone();
    }
}
