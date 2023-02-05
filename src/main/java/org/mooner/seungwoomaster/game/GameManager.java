package org.mooner.seungwoomaster.game;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.mooner.seungwoomaster.game.actionbar.ActionBar;
import org.mooner.seungwoomaster.game.listener.EventManager;
import org.mooner.seungwoomaster.game.modifier.PlayerAttribute;
import org.mooner.seungwoomaster.game.modifier.PlayerModifier;
import org.mooner.seungwoomaster.game.other.Healing;
import org.mooner.seungwoomaster.game.other.HowToPlay;
import org.mooner.seungwoomaster.game.shop.ArmorTier;
import org.mooner.seungwoomaster.game.shop.Shop;
import org.mooner.seungwoomaster.game.shop.WeaponTier;
import org.mooner.seungwoomaster.game.upgrade.TokenGUI;

import java.util.*;

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
    private Map<UUID, Integer> score;
    private Map<UUID, Integer> coin;
    private Map<UUID, Double> damageMap;
    private Map<UUID, Double> totalDamageMap;
    private Map<UUID, Integer> tokenMap;
    private Map<UUID, PlayerModifier> modifierMap;
    private Map<UUID, WeaponTier> swordTierMap;
    private Map<UUID, WeaponTier> axeTierMap;
    private Map<UUID, ArmorTier> topArmorTierMap;
    private Map<UUID, ArmorTier> bottomArmorTierMap;
    private Map<UUID, Healing> healingMap;
    private Set<UUID> readySet;
    //    private Map<UUID,>
    private Shop shop;
    private EventManager eventManager;
    private int round;

    public GameManager() {
        start = false;
    }

    public void start() {
        start = true;
        List<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        defensedPlayer = new HashSet<>();
        score = new HashMap<>();
        coin = new HashMap<>();
        damageMap = new HashMap<>();
        totalDamageMap = new HashMap<>();
        tokenMap = new HashMap<>();
        modifierMap = new HashMap<>();
        swordTierMap = new HashMap<>();
        axeTierMap = new HashMap<>();
        topArmorTierMap = new HashMap<>();
        bottomArmorTierMap = new HashMap<>();
        healingMap = new HashMap<>();
        for (Player player : onlinePlayers) healingMap.put(player.getUniqueId(), new Healing(player));
        ActionBar.runActionBar();
        round = 0;

        onlinePlayers.forEach(this::updateInventory);
        Bukkit.getPluginManager().registerEvents(shop = new Shop(), master);
        Bukkit.getPluginManager().registerEvents(eventManager = new EventManager(), master);

        changeDefender();
    }

    public void stop() {
        start = false;
        defensePlayer = null;
        score = null;
        coin = null;
        damageMap = null;
        totalDamageMap = new HashMap<>();
        modifierMap = null;
        swordTierMap = null;
        axeTierMap = null;
        topArmorTierMap = null;
        bottomArmorTierMap = null;

        Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().clear());
        HandlerList.unregisterAll(shop);
        HandlerList.unregisterAll(eventManager);
        shop = null;
        eventManager = null;
    }

    private String coin(Player player, int def, int rank) {
        addMoney(player, def);
        if(rank == -1) return "    &6"+def+" Coins";
        int bonus = (3 - rank) * 400;
        if(bonus >= 0) {
            addMoney(player, bonus);
            return "    &6"+(def + bonus)+" Coins &7(Damage Bonus &6"+bonus+" Coins&7)";
        } else return "    &6"+def+" Coins";
    }

    public void end(boolean attackerWin) {
        Bukkit.broadcastMessage(chat("&a■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■"));
        if (attackerWin)
            Bukkit.broadcastMessage(chat("                 &c&lATTACKERS &f&lWIN!"));
        else
            Bukkit.broadcastMessage(chat("                 &a&lDEFENDERS &f&lWIN!"));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(chat("  &f&lRewards:"));

        damageMap.forEach((uuid, aDouble) -> totalDamageMap.merge(uuid, aDouble, Double::sum));

        List<UUID> damageList = damageMap.entrySet().stream().sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue())).map(Map.Entry::getKey).toList();

        if (attackerWin) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isAttackPlayer(player)) {
                    player.sendMessage(chat(coin(player, 3500, damageList.indexOf(player.getUniqueId()))));
                    player.sendMessage(chat("    &5Ability Token x16"));
                } else {
                    player.sendMessage(chat(coin(player, 2500, damageList.indexOf(player.getUniqueId()))));
                    player.sendMessage(chat("    &5Ability Token x10"));
                }
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isAttackPlayer(player)) {
                    player.sendMessage(chat(coin(player, 2500, damageList.indexOf(player.getUniqueId()))));
                    player.sendMessage(chat("    &5Ability Token x10"));
                } else {
                    player.sendMessage(chat(coin(player, 3500, damageList.indexOf(player.getUniqueId()))));
                    player.sendMessage(chat("    &5Ability Token x16"));
                }
            }
        }
        Bukkit.broadcastMessage(chat("&a■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■"));
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0, false, false));
            player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
            if (attackerWin) {
                if (isAttackPlayer(player)) {
                    player.sendTitle(chat("&aYou Win!"), "", 40, 60, 100);
                    addToken(player, 16);
                    fireWorkSound(player);
                } else {
                    player.sendTitle(chat("&cYou Lose..."), "", 40, 60, 100);
                    addToken(player, 10);
                }
            } else {
                if (!isAttackPlayer(player)) {
                    player.sendTitle(chat("&aYou Win!"), "", 40, 60, 100);
                    addToken(player, 16);
                    fireWorkSound(player);
                } else {
                    player.sendTitle(chat("&cYou Lose..."), "", 40, 60, 100);
                    addToken(player, 10);
                }
            }
        });
        Bukkit.getScheduler().runTaskLater(master, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.sendMessage(chat("&7획득한 토큰을 이용해 능력을 강화하세요. &a모든 토큰을 사용하면 준비 상태가 됩니다."));
                new TokenGUI(player);
            });
        }, 200);
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

    private int changeTick;

    public void changeDefender() {
        if (defensedPlayer != null) defensedPlayer.add(defensePlayer);
        round++;
        resetTeam();
        List<? extends Player> players = Bukkit.getOnlinePlayers().stream()
                .filter(player -> !defensedPlayer.contains(player.getUniqueId()))
                .toList();
        if (players.isEmpty()) {
            stop();
            return;
        }
        Random random = new Random();
        defensePlayer = players.get(random.nextInt(players.size())).getUniqueId();
        changeTick = 0;
        Bukkit.broadcastMessage(chat("&e방어자를 선정합니다."));
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 0, false, false, false));
            player.setGameMode(GameMode.ADVENTURE);
        });
        Bukkit.getScheduler().runTaskTimer(master, task -> {
            changeTick++;
            if (changeTick <= 40 || changeTick <= 60 && changeTick % 2 == 0 || changeTick > 60 && changeTick <= 90 && changeTick % 4 == 0 || changeTick > 90 && changeTick <= 110 && changeTick % 8 == 0 || changeTick > 110 && changeTick <= 140 && changeTick % 16 == 0) {
                Player randomPlayer = players.get(random.nextInt(players.size()));
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.sendTitle(chat("&aDefender selecting..."), randomPlayer.getName(), 0, 20, 0);
                    player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 2);
                });
            }
            if (changeTick > 140) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.sendTitle(chat("&2{def} Round " + round + " Defender {def}"), ChatColor.GREEN + Bukkit.getPlayer(defensePlayer).getName(), 10, 10, 80);
                    player.playSound(player, Sound.UI_BUTTON_CLICK, 1, 2);
                    player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                });
                Bukkit.getScheduler().runTaskLater(master, () -> {
                    requestReady();
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.playSound(player, Sound.UI_TOAST_IN, 1, 1);
                        player.playSound(player, Sound.UI_TOAST_IN, 1, 1);
                        new HowToPlay(player);
                    });
                }, 100);
                updateTeam();
                task.cancel();
            }
        }, 40, 1);
    }

    private void requestReady() {
        readySet = new HashSet<>();
        Bukkit.broadcastMessage(chat("&a모든 플레이어가 준비하면, 게임이 시작됩니다!"));
    }

    public void setReady(Player player) {
        if(readySet.contains(player.getUniqueId())) return;
        readySet.add(player.getUniqueId());
        Bukkit.broadcastMessage(chat("&6" + player.getName() + "&a Ready!"));
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        onlinePlayers.forEach(p -> {
            p.playSound(p, Sound.UI_BUTTON_CLICK, 1, 0.75f);
            p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2f);
        });
        if(readySet.size() == onlinePlayers.size()) {
            changeTick = 5;
            onlinePlayers.forEach(p -> {
                p.playSound(p, Sound.BLOCK_BEACON_ACTIVATE, 1, 1f);
                p.playSound(p, Sound.BLOCK_BEACON_ACTIVATE, 1, 1f);
                p.playSound(p, Sound.BLOCK_BEACON_ACTIVATE, 1, 1f);
                p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1, 0.75f);
            });
            Bukkit.broadcastMessage(chat("&e모든 플레이어가 준비를 마쳤습니다!"));
            Bukkit.getScheduler().runTaskTimer(master, bukkitTask -> {
                if (changeTick == 0) {
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        p.sendTitle("", chat("&cRound Start!"), 20, 40, 60);
                        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1, 0.5f);
                        p.playSound(p, Sound.ENTITY_WOLF_HOWL, 1, 1f);
                        p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                        p.removePotionEffect(PotionEffectType.BLINDNESS);
                    });
                    bukkitTask.cancel();
                } else {
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        p.sendTitle(chat("&eRound " + round), String.valueOf(changeTick), 0, 22, 0);
                        p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.5f);
                    });
                    Bukkit.broadcastMessage(chat("&e게임이 &c" + changeTick + "&e초 후에 시작합니다!"));
                }
                changeTick--;
            }, 20, 20);
        }
    }

    private void updateTeam() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager != null) {
            Scoreboard mainScoreboard = scoreboardManager.getMainScoreboard();
            Team attackTeam = mainScoreboard.getTeam("attack");
            if (attackTeam != null) {
                attackTeam.setAllowFriendlyFire(false);
                attackTeam.setCanSeeFriendlyInvisibles(true);
                attackTeam.setPrefix(chat("&4Attacker "));
                attackTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                attackTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
                attackTeam.setColor(ChatColor.RED);
                Bukkit.getOnlinePlayers().forEach(player -> attackTeam.addEntry(player.getName()));
            }
            Team defenseTeam = mainScoreboard.getTeam("defense");
            if (defenseTeam != null) {
                defenseTeam.setAllowFriendlyFire(false);
                defenseTeam.setCanSeeFriendlyInvisibles(true);
                defenseTeam.setPrefix(chat("&2Defender "));
                defenseTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                defenseTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
                defenseTeam.setColor(ChatColor.GREEN);
                defenseTeam.addEntry(Bukkit.getPlayer(defensePlayer).getName());
            }
        }
    }

    private void resetTeam() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager != null) {
            Scoreboard mainScoreboard = scoreboardManager.getMainScoreboard();
            Team attackTeam = mainScoreboard.getTeam("attack");
            if (attackTeam != null) {
                attackTeam.getEntries().forEach(attackTeam::removeEntry);
            }
            Team defenseTeam = mainScoreboard.getTeam("defense");
            if (defenseTeam != null) {
                defenseTeam.getEntries().forEach(defenseTeam::removeEntry);
            }
        }
    }

    public String clickMessage(int now, int req) {
        if (now == req) return "&a이미 해당 검을 구매했습니다!";
        if (now < req) return "&e클릭해 구매하세요!";
        return "&c이미 더 나은 검을 가지고 있습니다!";
    }

    public void reloadArmor(Player player) {
        ArmorTier topArmorTier = getTopArmorTier(player);
        ArmorTier bottomArmorTier = getBottomArmorTier(player);

        boolean isAttackPlayer = isAttackPlayer(player);
        if (topArmorTier != ArmorTier.NONE) {
            player.getInventory().setItem(EquipmentSlot.HEAD, topArmorTier.getHelmet());
            if (isAttackPlayer) {
                player.getInventory().setItem(EquipmentSlot.FEET, topArmorTier.getBoots());
            } else {
                player.getInventory().setItem(EquipmentSlot.CHEST, topArmorTier.getLeggings());
            }
        }

        if (bottomArmorTier != ArmorTier.NONE) {
            player.getInventory().setItem(EquipmentSlot.LEGS, topArmorTier.getLeggings());
            if (!isAttackPlayer) {
                player.getInventory().setItem(EquipmentSlot.FEET, topArmorTier.getBoots());
            }
        }
    }

    public void updateInventory(Player player) {
        WeaponTier swordTier = getSwordTier(player);
        WeaponTier axeTier = getAxeTier(player);
        ArmorTier topArmorTier = getTopArmorTier(player);
        ArmorTier bottomArmorTier = getBottomArmorTier(player);

        player.getInventory().setItem(9, createItem(Material.STONE_SWORD, 1, "Stone Sword", "Damage: &c3.5", "", "Cost: &650 Coin", "",
                clickMessage(swordTier.ordinal(), WeaponTier.STONE.ordinal())));
        player.getInventory().setItem(10, createItem(Material.IRON_SWORD, 1, "Iron Sword", "Damage: &c4", "", "Cost: &6125 Coins", "",
                clickMessage(swordTier.ordinal(), WeaponTier.IRON.ordinal())));
        player.getInventory().setItem(11, createItem(Material.GOLDEN_SWORD, 1, "Golden Sword", "Damage: &c5", "", "Cost: &6275 Coins", "",
                clickMessage(swordTier.ordinal(), WeaponTier.GOLD.ordinal())));
        player.getInventory().setItem(12, createItem(Material.DIAMOND_SWORD, 1, "Diamond Sword", "Damage: &c6", "", "Cost: &6600 Coins", "",
                clickMessage(swordTier.ordinal(), WeaponTier.DIAMOND.ordinal())));
        player.getInventory().setItem(13, createItem(Material.NETHERITE_SWORD, 1, "Netherite Sword", "Damage: &c7", "", "Cost: &61500 Coins", "",
                clickMessage(swordTier.ordinal(), WeaponTier.NETHERITE.ordinal())));

        player.getInventory().setItem(18, createItem(Material.STONE_AXE, 1, "Stone Axe", "Damage: &c6.5", "", "Cost: &675 Coins", "",
                clickMessage(axeTier.ordinal(), WeaponTier.STONE.ordinal())));
        player.getInventory().setItem(19, createItem(Material.IRON_AXE, 1, "Iron Axe", "Damage: &c7.5", "", "Cost: &6200 Coins", "",
                clickMessage(axeTier.ordinal(), WeaponTier.IRON.ordinal())));
        player.getInventory().setItem(20, createItem(Material.GOLDEN_AXE, 1, "Golden Axe", "Damage: &c8.5", "", "Cost: &6400 Coins", "",
                clickMessage(axeTier.ordinal(), WeaponTier.GOLD.ordinal())));
        player.getInventory().setItem(21, createItem(Material.DIAMOND_AXE, 1, "Diamond Axe", "Damage: &c9", "", "Cost: &6850 Coins", "",
                clickMessage(axeTier.ordinal(), WeaponTier.DIAMOND.ordinal())));
        player.getInventory().setItem(22, createItem(Material.NETHERITE_AXE, 1, "Netherite Axe", "Damage: &c10", "", "Cost: &62000 Coins", "",
                clickMessage(axeTier.ordinal(), WeaponTier.NETHERITE.ordinal())));

        player.getInventory().setItem(14, createItem(Material.BARRIER, 1, " "));

//        player.getInventory().setItem(14, createItem(Material.ENCHANTING_TABLE, 1, "&bSword Enchanting", "", "Right Click: &bSharpness Upgrade", "Upgrade Cost: &6500 Coins", "", "Left Click: &bKnockback Upgrade", "Upgrade Cost: &6500 Coins"));
        player.getInventory().setItem(15, createItem(Material.BLAZE_ROD, 1, "&cFire Force x1", "Set &aall &7enemies &7on &cfire", "&7for &a10 seconds&7!", "", "Cost: &6250 Coins", "", "&e클릭해 구매하세요!"));
        player.getInventory().setItem(16, createItem(Material.BARRIER, 1, " "));
//        player.getInventory().setItem(16, createItem(Material.BOW, 1, "Bow", "Damage: &c5 (when full charged)", "", "Cost: &6400 Coins", "", "&e클릭해 구매하세요!"));
        player.getInventory().setItem(17, createItem(Material.FEATHER, 8, "Arrow x8", "", "Cost: &6100 Coins", "", "&e클릭해 구매하세요!"));

        player.getInventory().setItem(23, createItem(Material.WITHER_SKELETON_SKULL, 1, "&8Darkness Blast x1", "Give &aall &7enemies &8Darkness&7, &8Slowness II", "&7effect &7for &a10 seconds&7!", "", "Cost: &6300 Coins", "", "&e클릭해 구매하세요!"));
        player.getInventory().setItem(24, createItem(Material.ENDER_PEARL, 1, "&5Ender Pearl x1", "", "Cost: &6175 Coins", "", "&e클릭해 구매하세요!"));
        player.getInventory().setItem(25, createItem(Material.GOLDEN_APPLE, 1, "&6 CoinsApple x1", "", "Cost: &6120 Coins", "", "&e클릭해 구매하세요!"));
        player.getInventory().setItem(26, createItem(Material.BARRIER, 1, " "));
//        player.getInventory().setItem(26, createItem(Material.FIRE_CHARGE, 1, "&3Fireball x1", "", "Cost: &650 Coins", "", "&e클릭해 구매하세요!"));

        player.getInventory().setItem(27, createItem(Material.LEATHER_HELMET, 1, "Leather Helmet & Boots", "Defense: &a+5% (+2.5% each)", "", "Cost: &6200 Coins", "",
                clickMessage(topArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
        player.getInventory().setItem(28, createItem(Material.LEATHER_LEGGINGS, 1, "Leather Leggings", "Defense: &a+5%", "", "Cost: &6200 Coins", "",
                clickMessage(bottomArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
        player.getInventory().setItem(29, createItem(Material.IRON_HELMET, 1, "Iron Helmet & Boots", "Defense: &a+10%", "", "Cost: &6450 Coins", "",
                clickMessage(topArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
        player.getInventory().setItem(30, createItem(Material.IRON_LEGGINGS, 1, "Iron Leggings", "Defense: &a+10%", "", "Cost: &6450 Coins", "",
                clickMessage(bottomArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
        player.getInventory().setItem(31, createItem(Material.BARRIER, 1, " "));
        player.getInventory().setItem(32, createItem(Material.DIAMOND_HELMET, 1, "Diamond Helmet & Boots", "Defense: &a+15%", "", "Cost: &6800 Coins", "",
                clickMessage(topArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
        player.getInventory().setItem(33, createItem(Material.DIAMOND_LEGGINGS, 1, "Diamond Leggings", "Defense: &a+15%", "", "Cost: &6800 Coins", "",
                clickMessage(bottomArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
        player.getInventory().setItem(34, createItem(Material.NETHERITE_HELMET, 1, "Netherite Helmet & Boots", "Defense: &a+20%", "", "Cost: &61350 Coins", "",
                clickMessage(topArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
        player.getInventory().setItem(35, createItem(Material.NETHERITE_LEGGINGS, 1, "Netherite Leggings", "Defense: &a+20%", "", "Cost: &61350 Coins", "",
                clickMessage(bottomArmorTier.ordinal(), WeaponTier.NETHERITE.ordinal())));
    }

    public void addDamage(Player player, double damage) {
        damageMap.merge(player.getUniqueId(), damage, Double::sum);
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
        if (money >= amount) {
            coin.put(player.getUniqueId(), money - amount);
            return true;
        }
        player.sendMessage(chat("&c해당 아이템을 구매하기 위한 돈이 부족합니다."));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 0.5f);
        return false;
    }

    public void addMoney(Player player, int amount) {
        amount *= 1 + getModifier(player).getValue(PlayerAttribute.COIN_BOOST);
        coin.merge(player.getUniqueId(), amount, Integer::sum);
    }

    public int getMoney(Player player) {
        return coin.getOrDefault(player.getUniqueId(), 0);
    }

    public void addToken(Player player, int amount) {
        tokenMap.merge(player.getUniqueId(), amount, Integer::sum);
    }

    public boolean removeToken(Player player, int amount) {
        int money = tokenMap.getOrDefault(player.getUniqueId(), 0);
        if (money >= amount) {
            tokenMap.put(player.getUniqueId(), money - amount);
            return true;
        }
        player.sendMessage(chat("&c해당 능력을 강화하기 위한 토큰이 부족합니다."));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.8f, 0.5f);
        return false;
    }

    public int getToken(Player player) {
        return tokenMap.getOrDefault(player.getUniqueId(), 0);
    }

    public PlayerModifier getModifier(Player player) {
        PlayerModifier modifier = modifierMap.get(player.getUniqueId());
        if (modifier != null) return modifier;
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
