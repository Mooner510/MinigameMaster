package org.mooner.seungwoomaster.game;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mooner.seungwoomaster.game.modifier.PlayerModifier;
import org.mooner.seungwoomaster.game.shop.ArmorTier;
import org.mooner.seungwoomaster.game.shop.Shop;
import org.mooner.seungwoomaster.game.shop.WeaponTier;

import java.util.*;
import java.util.stream.Collectors;

import static org.mooner.seungwoomaster.MoonerUtils.chat;
import static org.mooner.seungwoomaster.SeungWooMaster.master;
import static org.mooner.seungwoomaster.game.gui.GUIUtils.createItem;

public class GameManager {
    private static final GameManager instance = new GameManager();

    public static GameManager getInstance() {
        return instance;
    }

    private boolean start;
    private Set<UUID> defensedPlayer;
    private UUID defensePlayer;
    private Set<UUID> attackPlayer;
    private Map<UUID, Integer> score;
    private Map<UUID, Integer> coin;
    private Map<UUID, Double> damageMap;
    private Map<UUID, Integer> tokenMap;
    private Map<UUID, PlayerModifier> modifierMap;
    private Map<UUID, WeaponTier> swordTierMap;
    private Map<UUID, WeaponTier> axeTierMap;
    private Map<UUID, ArmorTier> topArmorTierMap;
    private Map<UUID, ArmorTier> bottomArmorTierMap;
    private Shop shop;

    public GameManager() {
        start = false;
    }

    public void start() {
        start = true;
        List<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        defensedPlayer = new HashSet<>();
        defensePlayer = onlinePlayers.stream().filter(player -> defensedPlayer.contains(player.getUniqueId())).toList().get(new Random().nextInt(onlinePlayers.size())).getUniqueId();
        attackPlayer = onlinePlayers.stream().map(Entity::getUniqueId).filter(p -> !defensePlayer.equals(p)).collect(Collectors.toSet());
        score = new HashMap<>();
        coin = new HashMap<>();
        damageMap = new HashMap<>();
        tokenMap = new HashMap<>();
        modifierMap = new HashMap<>();
        swordTierMap = new HashMap<>();
        axeTierMap = new HashMap<>();
        topArmorTierMap = new HashMap<>();
        bottomArmorTierMap = new HashMap<>();

        onlinePlayers.forEach(this::updateInventory);
        Bukkit.getPluginManager().registerEvents(shop = new Shop(), master);
    }

    public void stop() {
        start = false;
        defensePlayer = null;
        attackPlayer = null;
        score = null;
        coin = null;
        modifierMap = null;
        swordTierMap = null;
        axeTierMap = null;
        topArmorTierMap = null;
        bottomArmorTierMap = null;

        Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().clear());
        HandlerList.unregisterAll(shop);
        shop = null;
    }

    public void end(boolean attackerWin) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0, false, false));
            player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
            if(attackerWin) {
                if (isAttackPlayer(player)) {
                    player.sendTitle(chat("&a승리!"), chat("&7능력치 토큰 &6+16"), 40, 60, 100);
                    fireWorkSound(player);
                } else {
                    player.sendTitle(chat("&c패배..."), chat("&7능력치 토큰 &6+10"), 40, 60, 100);
                }
            } else {
                if (!isAttackPlayer(player)) {
                    player.sendTitle(chat("&a승리!"), chat("&7능력치 토큰 &6+16"), 40, 60, 100);
                    fireWorkSound(player);
                } else {
                    player.sendTitle(chat("&c패배..."), chat("&7능력치 토큰 &6+10"), 40, 60, 100);
                }
            }
        });

    }

    private Runnable firework(Player player) {
        return () -> {
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
            Bukkit.getScheduler().runTaskLater(master, () -> {
                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
            }, 30);
            Bukkit.getScheduler().runTaskLater(master, () -> {
                player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1, 1);
            }, 46);
        };
    }

    public void fireWorkSound(Player player) {
        for (int i = 0; i < 4; i++) Bukkit.getScheduler().runTaskLater(master, firework(player), i * 12);
        for (int i = 0; i < 4; i++) Bukkit.getScheduler().runTaskLater(master, firework(player), i * 12 + 150);
    }

    public void changeDefender(Player player) {
        attackPlayer.remove(player.getUniqueId());
        defensePlayer = player.getUniqueId();
    }

    public String clickMessage(int now, int req) {
        if(now == req) return "&a이미 해당 검을 구매했습니다!";
        if(now < req) return "&e클릭해 구매하세요!";
        return "&c이미 더 나은 검을 가지고 있습니다!";
    }

    public void updateInventory(Player player) {
        WeaponTier swordTier = getSwordTier(player);
        WeaponTier axeTier = getAxeTier(player);
        ArmorTier topArmorTier = getTopArmorTier(player);
        ArmorTier bottomArmorTier = getBottomArmorTier(player);

        player.getInventory().setItem(9, createItem(Material.STONE_SWORD, 1, "Stone Sword", "Damage: &c3.5", "", "Cost: &650G", "",
                clickMessage(swordTier.ordinal(), WeaponTier.STONE.ordinal())));
        player.getInventory().setItem(10, createItem(Material.IRON_SWORD, 1, "Iron Sword", "Damage: &c4", "", "Cost: &6125G", "",
                clickMessage(swordTier.ordinal(), WeaponTier.IRON.ordinal())));
        player.getInventory().setItem(11, createItem(Material.GOLDEN_SWORD, 1, "Golden Sword", "Damage: &c5", "", "Cost: &6275G", "",
                clickMessage(swordTier.ordinal(), WeaponTier.GOLD.ordinal())));
        player.getInventory().setItem(12, createItem(Material.DIAMOND_SWORD, 1, "Diamond Sword", "Damage: &c6", "", "Cost: &6600G", "",
                clickMessage(swordTier.ordinal(), WeaponTier.DIAMOND.ordinal())));
        player.getInventory().setItem(13, createItem(Material.NETHERITE_SWORD, 1, "Netherite Sword", "Damage: &c7", "", "Cost: &61500G", "",
                clickMessage(swordTier.ordinal(), WeaponTier.NETHERITE.ordinal())));

        player.getInventory().setItem(18, createItem(Material.STONE_AXE, 1, "Stone Axe", "Damage: &c6.5", "", "Cost: &675G", "",
                clickMessage(axeTier.ordinal(), WeaponTier.STONE.ordinal())));
        player.getInventory().setItem(19, createItem(Material.IRON_AXE, 1, "Iron Axe", "Damage: &c7.5", "", "Cost: &6200G", "",
                clickMessage(axeTier.ordinal(), WeaponTier.IRON.ordinal())));
        player.getInventory().setItem(20, createItem(Material.GOLDEN_AXE, 1, "Golden Axe", "Damage: &c8.5", "", "Cost: &6400G", "",
                clickMessage(axeTier.ordinal(), WeaponTier.GOLD.ordinal())));
        player.getInventory().setItem(21, createItem(Material.DIAMOND_AXE, 1, "Diamond Axe", "Damage: &c9", "", "Cost: &6850G", "",
                clickMessage(axeTier.ordinal(), WeaponTier.DIAMOND.ordinal())));
        player.getInventory().setItem(22, createItem(Material.NETHERITE_AXE, 1, "Netherite Axe", "Damage: &c10", "", "Cost: &62000G", "",
                clickMessage(axeTier.ordinal(), WeaponTier.NETHERITE.ordinal())));

//        player.getInventory().setItem(14, createItem(Material.ENCHANTING_TABLE, 1, "&bSword Enchanting", "", "Right Click: &bSharpness Upgrade", "Upgrade Cost: &6500G", "", "Left Click: &bKnockback Upgrade", "Upgrade Cost: &6500G"));
        player.getInventory().setItem(15, createItem(Material.BLAZE_ROD, 1, "&cFire Force x1", "Set &aall &7enemies &7on &cfire","&7for &a10 seconds&7!", "", "Cost: &6250G", "", "&e클릭해 구매하세요!"));
//        player.getInventory().setItem(16, createItem(Material.BOW, 1, "Bow", "Damage: &c5 (when full charged)", "", "Cost: &6400G", "", "&e클릭해 구매하세요!"));
        player.getInventory().setItem(17, createItem(Material.FEATHER, 8, "Arrow x8", "", "Cost: &6100G", "", "&e클릭해 구매하세요!"));

        player.getInventory().setItem(23, createItem(Material.WITHER_SKELETON_SKULL, 1, "&8Darkness Blast x1", "Give &aall &7enemies &8Darkness&7, &8Slowness II", "&7effect &7for &a10 seconds&7!", "", "Cost: &6300G", "", "&e클릭해 구매하세요!"));
        player.getInventory().setItem(24, createItem(Material.ENDER_PEARL, 1, "&5Ender Pearl x1", "", "Cost: &6175G", "", "&e클릭해 구매하세요!"));
        player.getInventory().setItem(25, createItem(Material.GOLDEN_APPLE, 1, "&6GApple x1", "", "Cost: &6120G", "", "&e클릭해 구매하세요!"));
//        player.getInventory().setItem(26, createItem(Material.FIRE_CHARGE, 1, "&3Fireball x1", "", "Cost: &650G", "", "&e클릭해 구매하세요!"));

        player.getInventory().setItem(27, createItem(Material.LEATHER_HELMET, 1, "Leather Helmet & Boots", "Defense: &a+5% (+2.5% each)", "", "Cost: &6200G", "",
                clickMessage(topArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
        player.getInventory().setItem(28, createItem(Material.LEATHER_LEGGINGS, 1, "Leather Leggings", "Defense: &a+5%", "", "Cost: &6200G", "",
                clickMessage(bottomArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
        player.getInventory().setItem(29, createItem(Material.IRON_HELMET, 1, "Iron Helmet & Boots", "Defense: &a+10%", "", "Cost: &6450G", "",
                clickMessage(topArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
        player.getInventory().setItem(30, createItem(Material.IRON_LEGGINGS, 1, "Iron Leggings", "Defense: &a+10%", "", "Cost: &6450G", "",
                clickMessage(bottomArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
        player.getInventory().setItem(32, createItem(Material.DIAMOND_HELMET, 1, "Diamond Helmet & Boots", "Defense: &a+15%", "", "Cost: &6800G", "",
                clickMessage(topArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
        player.getInventory().setItem(33, createItem(Material.DIAMOND_LEGGINGS, 1, "Diamond Leggings", "Defense: &a+15%", "", "Cost: &6800G", "",
                clickMessage(bottomArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
        player.getInventory().setItem(34, createItem(Material.NETHERITE_HELMET, 1, "Netherite Helmet & Boots", "Defense: &a+20%", "", "Cost: &61350G", "",
                clickMessage(topArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
        player.getInventory().setItem(35, createItem(Material.NETHERITE_LEGGINGS, 1, "Netherite Leggings", "Defense: &a+20%", "", "Cost: &61350G", "",
                clickMessage(bottomArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
    }

    public boolean isAttackPlayer(Player player) {
        return !defensePlayer.equals(player.getUniqueId());
    }

    public boolean isStarted() {
        return start;
    }

    public boolean hasMoney(Player player, int amount) {
        return coin.getOrDefault(player.getUniqueId(), 0) >= amount;
    }

    public boolean removeMoney(Player player, int amount) {
        int money = coin.getOrDefault(player.getUniqueId(), 0);
        if(money >= amount) {
            coin.put(player.getUniqueId(), money - amount);
            return true;
        }
        player.sendMessage(chat("&c해당 아이템을 구매하기 위한 돈이 부족합니다."));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 0.5f);
        return false;
    }

    public void addMoney(Player player, int amount) {
        coin.merge(player.getUniqueId(), amount, Integer::sum);
    }

    public int getMoney(Player player) {
        return coin.getOrDefault(player.getUniqueId(), 0);
    }

    public void addToken(Player player, int amount) {
        tokenMap.merge(player.getUniqueId(), amount, Integer::sum);
    }

    public int removeToken(Player player) {
        tokenMap.merge(player.getUniqueId(), -1, Integer::sum);
        return tokenMap.get(player.getUniqueId());
    }

    public int getToken(Player player) {
        return tokenMap.getOrDefault(player.getUniqueId(), 0);
    }

    public PlayerModifier getModifier(Player player) {
        PlayerModifier modifier = modifierMap.get(player.getUniqueId());
        if(modifier != null) return modifier;
        modifier = new PlayerModifier(player.getUniqueId());
        modifierMap.put(player.getUniqueId(), modifier);
        return modifier;
    }

    public void setSwordTier(Player player, WeaponTier tier) {
        swordTierMap.put(player.getUniqueId(), tier);
    }

    public void setAxeTier(Player player, WeaponTier tier) {
        axeTierMap.put(player.getUniqueId(), tier);
    }

    public void setTopArmorTier(Player player, ArmorTier tier) {
        topArmorTierMap.put(player.getUniqueId(), tier);
    }

    public void setBottomArmorTier(Player player, ArmorTier tier) {
        bottomArmorTierMap.put(player.getUniqueId(), tier);
    }

    public WeaponTier getSwordTier(Player player) {
        return swordTierMap.getOrDefault(player.getUniqueId(), WeaponTier.WOOD);
    }

    public WeaponTier getAxeTier(Player player) {
        return axeTierMap.getOrDefault(player.getUniqueId(), WeaponTier.WOOD);
    }

    public ArmorTier getTopArmorTier(Player player) {
        return topArmorTierMap.getOrDefault(player.getUniqueId(), ArmorTier.NONE);
    }

    public ArmorTier getBottomArmorTier(Player player) {
        return bottomArmorTierMap.getOrDefault(player.getUniqueId(), ArmorTier.NONE);
    }
}
