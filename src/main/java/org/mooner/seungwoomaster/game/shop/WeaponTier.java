package org.mooner.seungwoomaster.game.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;

import static org.mooner.seungwoomaster.game.gui.GUIUtils.createItem;

public enum WeaponTier {
    WOOD(null,
            createItem(Material.WOODEN_SWORD, 1, "Wooden Sword", "Damage: &c2"),
            createItem(Material.WOODEN_AXE, 1, "Wooden Axe", "Damage: &c2.5")
    ),
    STONE(Values.STONE,
            createItem(Material.STONE_SWORD, 1, "Stone Sword", "Damage: &c2.5"),
            createItem(Material.STONE_AXE, 1, "Stone Axe", "Damage: &c3.5")
    ),
    IRON(Values.IRON,
            createItem(Material.IRON_SWORD, 1, "Iron Sword", "Damage: &c3"),
            createItem(Material.IRON_AXE, 1, "Iron Axe", "Damage: &c4")
    ),
    GOLD(Values.GOLD,
            createItem(Material.GOLDEN_SWORD, 1, "&6Golden Sword", "Damage: &c3.5"),
            createItem(Material.GOLDEN_AXE, 1, "&6Golden Axe", "Damage: &c5")
    ),
    DIAMOND(Values.DIAMOND,
            createItem(Material.DIAMOND_SWORD, 1, "Diamond Sword", "Damage: &c4"),
            createItem(Material.DIAMOND_AXE, 1, "Diamond Axe", "Damage: &c6")
    ),
    NETHERITE(Values.NETHERITE,
            createItem(Material.NETHERITE_SWORD, 1, "Netherite Sword", "Damage: &c5"),
            createItem(Material.NETHERITE_AXE, 1, "Netherite Axe", "Damage: &c7")
    );

    @Nullable
    public static WeaponTier findBySword(Material material) {
        return Arrays.stream(values())
                .filter(it -> it.getSword().getType() == material)
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public static WeaponTier findByAxe(Material material) {
        return Arrays.stream(values())
                .filter(it -> it.getAxe().getType() == material)
                .findFirst()
                .orElse(null);
    }
    private final Values values;
    private final ItemStack sword;
    private final ItemStack axe;

    WeaponTier(Values values, ItemStack sword, ItemStack axe) {
        this.values = values;
        this.sword = sword;
        this.axe = axe;
    }

    public WeaponTier next() {
        return values()[ordinal() + 1];
    }

    public int getSwordCost() {
        return values.getMoney();
    }

    public int getAxeCost() {
        return values.getMoney(1.5);
    }

    public ItemStack getSword() {
        return sword.clone();
    }

    public ItemStack getAxe() {
        return axe.clone();
    }
}
