package org.mooner.seungwoomaster.game.shop;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.mooner.seungwoomaster.game.GameManager;

import static org.mooner.seungwoomaster.MoonerUtils.chat;
import static org.mooner.seungwoomaster.game.gui.GUIUtils.createItem;

public class Shop implements Listener {
    public int findWeapon(Player player, Material tierMaterial) {
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if(item != null && item.getType() == tierMaterial) {
                return i;
            }
        }
        return -1;
    }

    private final ItemStack fireForce = createItem(Material.BLAZE_ROD, 1, "&cFire Force", "Set &aall &7enemies &7on &cfire", "&7for &a10 seconds&7!");

    private final ItemStack bow = createItem(Material.BLAZE_ROD, 1, "Bow", "Damage: &c5 (when full charged)");

    private final ItemStack darknessBlast = createItem(Material.WITHER_SKELETON_SKULL, 1, "&8Darkness Blast", "Give &aall &7enemies &8Darkness&7, &8Slowness II", "&7effect &7for &a10 seconds&7!");

    private final ItemStack enderPearl = createItem(Material.ENDER_PEARL, 1, "&5Ender Pearl");

    private final ItemStack gApple = createItem(Material.GOLDEN_APPLE, 1, "&6GApple");

    private final ItemStack fireball = createItem(Material.FIRE_CHARGE, 1, "&3Fireball");

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        GameManager gameManager = GameManager.getInstance();
        if(!gameManager.isStarted()) return;
        if(e.getClickedInventory() == null) return;
        Player p = (Player) e.getWhoClicked();
        if(e.getClickedInventory().equals(p.getInventory())) {
            if(e.getSlot() >= 9 && e.getSlot() <= 35) {
                e.setCancelled(true);
            } else {
                if(e.getSlotType() == InventoryType.SlotType.ARMOR || e.getSlotType() == InventoryType.SlotType.CRAFTING || e.getSlotType() == InventoryType.SlotType.RESULT) {
                    e.setCancelled(true);
                }
                return;
            }
            ItemStack item = e.getCurrentItem();
            if(item == null) return;
            WeaponTier swordTier = gameManager.getSwordTier(p);
            WeaponTier axeTier = gameManager.getAxeTier(p);
            ArmorTier topArmorTier = gameManager.getTopArmorTier(p);
            ArmorTier bottomArmorTier = gameManager.getBottomArmorTier(p);

            WeaponTier clickWeaponTier = WeaponTier.findBySword(item.getType());
            if(clickWeaponTier != null) {
                if(swordTier.ordinal() > clickWeaponTier.ordinal()) {
                    p.sendMessage(chat("&c이미 더 나은 검을 가지고 있습니다!"));
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 0.5f);
                    return;
                }
                if(gameManager.removeMoney(p, clickWeaponTier.getSwordCost())) {
                    int slot = findWeapon(p, swordTier.getSword().getType());
                    ItemStack buy = clickWeaponTier.getSword();
                    p.getInventory().setItem(slot, buy);
                    gameManager.setSwordTier(p, clickWeaponTier);

                    p.sendMessage(chat("&6"+buy.getItemMeta().getDisplayName()+"&a을(를) 구매했습니다."));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                }
                return;
            }
            clickWeaponTier = WeaponTier.findByAxe(item.getType());
            if(clickWeaponTier != null) {
                if(axeTier.ordinal() > clickWeaponTier.ordinal()) {
                    p.sendMessage(chat("&c이미 더 나은 도끼를 가지고 있습니다!"));
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 0.5f);
                    return;
                }
                if(gameManager.removeMoney(p, clickWeaponTier.getAxeCost())) {
                    int slot = findWeapon(p, axeTier.getAxe().getType());
                    ItemStack buy = clickWeaponTier.getAxe();
                    p.getInventory().setItem(slot, buy);
                    gameManager.setAxeTier(p, clickWeaponTier);

                    p.sendMessage(chat("&6"+buy.getItemMeta().getDisplayName()+"&a을(를) 구매했습니다."));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                }
                return;
            }

            ArmorTier clickArmorTier = ArmorTier.findByHelmet(item.getType());
            if(clickArmorTier != null) {
                if(topArmorTier.ordinal() > clickArmorTier.ordinal()) {
                    p.sendMessage(chat("&c이미 더 나은 투구와 신발을 입고 있습니다!"));
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 0.5f);
                    return;
                }
                if(gameManager.removeMoney(p, clickArmorTier.getTopCost())) {
                    ItemStack buy = clickArmorTier.getHelmet();
                    p.getInventory().setItem(EquipmentSlot.HEAD, buy);
                    p.getInventory().setItem(EquipmentSlot.FEET, clickArmorTier.getBoots());
                    gameManager.setTopArmorTier(p, clickArmorTier);

                    p.sendMessage(chat("&6"+ buy.getItemMeta().getDisplayName()+" & Boots&a을(를) 구매했습니다."));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                }
                return;
            }
            clickArmorTier = ArmorTier.findByLeggings(item.getType());
            if(clickArmorTier != null) {
                if(bottomArmorTier.ordinal() > clickArmorTier.ordinal()) {
                    p.sendMessage(chat("&c이미 더 나은 바지를 입고 있습니다!"));
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 0.5f);
                    return;
                }
                if(gameManager.removeMoney(p, clickArmorTier.getBottomCost())) {
                    ItemStack buy = clickArmorTier.getLeggings();
                    p.getInventory().setItem(EquipmentSlot.LEGS, buy);
                    gameManager.setBottomArmorTier(p, clickArmorTier);

                    p.sendMessage(chat("&6"+ buy.getItemMeta().getDisplayName()+"&a을(를) 구매했습니다."));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                }
                return;
            }
            switch (e.getSlot()) {
                case 15 -> {
                    if(gameManager.removeMoney(p, 250)) {
                        p.getInventory().addItem(fireForce);

                        p.sendMessage(chat(fireForce.getItemMeta().getDisplayName()+"&a을(를) 구매했습니다."));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                    }
                }
                case 16 -> {
                    if(gameManager.removeMoney(p, 400)) {
                        p.getInventory().addItem(bow);

                        p.sendMessage(chat(bow.getItemMeta().getDisplayName()+"&a을(를) 구매했습니다."));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                    }
                }
                case 17 -> {
                    if(gameManager.removeMoney(p, 100)) {
                        p.getInventory().addItem(new ItemStack(Material.ARROW, 8));

                        p.sendMessage(chat("&fArrow&a을(를) 구매했습니다."));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                    }
                }
                case 23 -> {
                    if(gameManager.removeMoney(p, 300)) {
                        p.getInventory().addItem(darknessBlast);

                        p.sendMessage(chat(darknessBlast.getItemMeta().getDisplayName()+"&a을(를) 구매했습니다."));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                    }
                }
                case 24 -> {
                    if(gameManager.removeMoney(p, 175)) {
                        p.getInventory().addItem(enderPearl);

                        p.sendMessage(chat(enderPearl.getItemMeta().getDisplayName()+"&a을(를) 구매했습니다."));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                    }
                }
                case 25 -> {
                    if(gameManager.removeMoney(p, 120)) {
                        p.getInventory().addItem(gApple);

                        p.sendMessage(chat(gApple.getItemMeta().getDisplayName()+"&a을(를) 구매했습니다."));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                    }
                }
                case 26-> {
                    if(gameManager.removeMoney(p, 50)) {
                        p.getInventory().addItem(fireball);

                        p.sendMessage(chat(fireball.getItemMeta().getDisplayName()+"&a을(를) 구매했습니다."));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                    }
                }
            }
        }
    }
}
