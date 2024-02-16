package org.mooner.seungwoomaster.game.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mooner.seungwoomaster.game.GameManager;
import org.mooner.seungwoomaster.game.listener.EventManager;
import org.mooner.seungwoomaster.game.other.Berserk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mooner.seungwoomaster.MoonerUtils.chat;
import static org.mooner.seungwoomaster.game.gui.GUIUtils.createItem;

public class Shop implements Listener {
    public int findWeapon(Player player, Material tierMaterial) {
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == tierMaterial) {
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

    private static Map<UUID, Map<Values, Long>> timeMap;

    public static void resetTimeMap() {
        timeMap = new HashMap<>();
    }

    private static boolean check(Player p, Values values) {
        Map<Values, Long> map = timeMap.get(p.getUniqueId());
        if(map == null) {
            timeMap.put(p.getUniqueId(), new HashMap<>());
            map = timeMap.get(p.getUniqueId());
        }
        Long time = map.getOrDefault(values, 0L);
        long now = System.currentTimeMillis();
        boolean b = time <= now;
        if(!b) {
//            p.sendMessage(chat("&cThis ability is on cooldown for &c" + Math.ceil((time - now) / 1000d) + "s."));
            p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 0.75f, 0.5f);
        }
        return b;
    }

    private static void setTime(Player p, Values values) {
        if(GameManager.getInstance().isAttackPlayer(p) && values == Values.GLOWER) {
            timeMap.get(p.getUniqueId()).put(values, System.currentTimeMillis() + values.getCooltime() * 500L);
        } else {
            timeMap.get(p.getUniqueId()).put(values, System.currentTimeMillis() + values.getCooltime() * 1000L);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        timeMap.put(e.getPlayer().getUniqueId(), new HashMap<>());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        GameManager gameManager = GameManager.getInstance();
        if (!gameManager.isStarted()) return;
        if (e.getClickedInventory() == null) return;
        Player p = (Player) e.getWhoClicked();
        if (e.getClickedInventory().equals(p.getInventory())) {
            if (e.getSlot() >= 9 && e.getSlot() <= 35) {
                e.setCancelled(true);
            } else {
                if (e.getSlotType() == InventoryType.SlotType.ARMOR || e.getSlotType() == InventoryType.SlotType.CRAFTING || e.getSlotType() == InventoryType.SlotType.RESULT) {
                    e.setCancelled(true);
                }
                return;
            }
            ItemStack item = e.getCurrentItem();
            if (item == null) return;
            WeaponTier swordTier = gameManager.getSwordTier(p);
            WeaponTier axeTier = gameManager.getAxeTier(p);
            ArmorTier topArmorTier = gameManager.getTopArmorTier(p);
            ArmorTier bottomArmorTier = gameManager.getBottomArmorTier(p);

            WeaponTier clickWeaponTier = WeaponTier.findBySword(item.getType());
            if (clickWeaponTier != null) {
                if (swordTier.ordinal() > clickWeaponTier.ordinal()) {
                    p.sendMessage(chat("&c이미 더 나은 검을 가지고 있습니다!"));
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 0.5f);
                    return;
                }
                if (gameManager.removeMoney(p, clickWeaponTier.getSwordCost())) {
                    int slot = findWeapon(p, swordTier.getSword().getType());
                    ItemStack buy = clickWeaponTier.getSword();
                    p.getInventory().setItem(slot, buy);
                    gameManager.setSwordTier(p, clickWeaponTier);

                    p.sendMessage(chat("&6" + buy.getItemMeta().getDisplayName() + "&a을(를) 구매했습니다."));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                }
                return;
            }
            clickWeaponTier = WeaponTier.findByAxe(item.getType());
            if (clickWeaponTier != null) {
                if (axeTier.ordinal() > clickWeaponTier.ordinal()) {
                    p.sendMessage(chat("&c이미 더 나은 도끼를 가지고 있습니다!"));
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 0.5f);
                    return;
                }
                if (gameManager.removeMoney(p, clickWeaponTier.getAxeCost())) {
                    int slot = findWeapon(p, axeTier.getAxe().getType());
                    ItemStack buy = clickWeaponTier.getAxe();
                    p.getInventory().setItem(slot, buy);
                    gameManager.setAxeTier(p, clickWeaponTier);

                    p.sendMessage(chat("&6" + buy.getItemMeta().getDisplayName() + "&a을(를) 구매했습니다."));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                }
                return;
            }

            ArmorTier clickArmorTier = ArmorTier.findByHelmet(item.getType());
            if (clickArmorTier != null) {
                if (topArmorTier.ordinal() > clickArmorTier.ordinal()) {
                    if (gameManager.isAttackPlayer(p)) p.sendMessage(chat("&c이미 더 나은 투구와 신발을 입고 있습니다!"));
                    else p.sendMessage(chat("&c이미 더 나은 투구와 흉갑을 입고 있습니다!"));
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 0.5f);
                    return;
                }
                if (gameManager.removeMoney(p, clickArmorTier.getTopCost())) {
                    gameManager.setTopArmorTier(p, clickArmorTier);

                    p.sendMessage(chat("&6" + clickArmorTier.getHelmet().getItemMeta().getDisplayName() + " & Boots&a을(를) 구매했습니다."));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                    gameManager.reloadArmor(p);
                }
                return;
            }
            clickArmorTier = ArmorTier.findByLeggings(item.getType());
            if (clickArmorTier != null) {
                if (bottomArmorTier.ordinal() > clickArmorTier.ordinal()) {
                    if (gameManager.isAttackPlayer(p)) p.sendMessage(chat("&c이미 더 나은 바지를 입고 있습니다!"));
                    else p.sendMessage(chat("&c이미 더 나은 바지와 신발을 입고 있습니다!"));
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 0.5f);
                    return;
                }
                if (gameManager.removeMoney(p, clickArmorTier.getBottomCost())) {
                    gameManager.setBottomArmorTier(p, clickArmorTier);

                    p.sendMessage(chat("&6" + clickArmorTier.getLeggings().getItemMeta().getDisplayName() + "&a을(를) 구매했습니다."));
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                    gameManager.reloadArmor(p);
                }
                return;
            }
            Player defensePlayer = gameManager.getDefensePlayer();
            switch (e.getSlot()) {
                case 14 -> {
                    if(gameManager.isAttackPlayer(p)) {
                        if(check(p, Values.BERSERK) && gameManager.removeMoney(p, Values.BERSERK.getMoney())) {
                            new Berserk(p);
                        }
                        return;
                    }
                    if (check(p, Values.PISTON) && gameManager.removeMoney(p, Values.PISTON.getMoney())) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (gameManager.isAttackPlayer(player)) {
                                player.setVelocity(player.getLocation().toVector().subtract(defensePlayer.getLocation().toVector()).normalize().multiply(2.5).setY(1));
                                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 1, 0.5f);
                                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 0.6f, 0.5f);
                                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 1, 0.5f);
                            }
                        }
                        Shop.setTime(p, Values.PISTON);
                        p.sendMessage(chat(item.getItemMeta().getDisplayName() + "&a을(를) 구매했습니다."));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                    }
                }
                case 15 -> {
                    if (check(p, Values.GLOWER) && gameManager.removeMoney(p, Values.GLOWER.getMoney())) {
                        if(gameManager.isAttackPlayer(p)) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 300, 0, true, true, true));
                        } else {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 600, 0, true, true, true));
                        }
                        Shop.setTime(p, Values.GLOWER);
                        p.sendMessage(chat(item.getItemMeta().getDisplayName() + "&a을(를) 구매했습니다."));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                    }
                }
                case 16 -> {
                    if(gameManager.isAttackPlayer(p)) return;
                    if (check(p, Values.FIRE_FORCE) && gameManager.removeMoney(p, Values.FIRE_FORCE.getMoney())) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (gameManager.isAttackPlayer(player)) {
                                player.setFireTicks(200);
                                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1, 0.5f);
                            }
                        }
                        Shop.setTime(p, Values.FIRE_FORCE);
                        p.sendMessage(chat(item.getItemMeta().getDisplayName() + "&a을(를) 구매했습니다."));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                    }
                }
                case 17 -> {
                    if(gameManager.isAttackPlayer(p)) return;
                    if (check(p, Values.DARKNESS_BLAST) && gameManager.removeMoney(p, Values.DARKNESS_BLAST.getMoney())) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (gameManager.isAttackPlayer(player)) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 200, 0, false, true, true));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1, false, true, true));
                                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1, 0.5f);
                            }
                        }
                        Shop.setTime(p, Values.DARKNESS_BLAST);
                        p.sendMessage(chat(item.getItemMeta().getDisplayName() + "&a을(를) 구매했습니다."));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                    }
                }
                case 23 -> {
                    if (check(p, Values.ENDER_PEARL) && gameManager.removeMoney(p, Values.ENDER_PEARL.getMoney())) {
                        p.getInventory().addItem(enderPearl.clone());

                        p.sendMessage(chat(item.getItemMeta().getDisplayName() + "&a을(를) 구매했습니다."));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                    }
                }
                case 24 -> {
                    if (gameManager.removeMoney(p, Values.GAPPLE.getMoney())) {
                        p.getInventory().addItem(gApple.clone());

                        p.sendMessage(chat(item.getItemMeta().getDisplayName() + "&a을(를) 구매했습니다."));
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
                    }
                }
//                case 25 -> {
//                    if (gameManager.removeMoney(p, 120)) {
//                        p.getInventory().addItem(gApple);
//
//                        p.sendMessage(chat(item.getItemMeta().getDisplayName() + "&a을(를) 구매했습니다."));
//                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
//                    }
//                }
//                case 26 -> {
//                    if (gameManager.removeMoney(p, 50)) {
//                        p.getInventory().addItem(fireball);
//
//                        p.sendMessage(chat(item.getItemMeta().getDisplayName() + "&a을(를) 구매했습니다."));
//                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 2f);
//                    }
//                }
            }
        }
    }
}
