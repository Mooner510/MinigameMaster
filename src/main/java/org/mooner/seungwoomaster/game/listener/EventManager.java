package org.mooner.seungwoomaster.game.listener;

import com.google.common.collect.ImmutableSet;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mooner.seungwoomaster.game.GameManager;
import org.mooner.seungwoomaster.game.actionbar.ActionBar;
import org.mooner.seungwoomaster.game.modifier.PlayerAttribute;
import org.mooner.seungwoomaster.game.modifier.PlayerModifier;
import org.mooner.seungwoomaster.game.other.Respawn;
import org.mooner.seungwoomaster.game.total.Total;
import org.mooner.seungwoomaster.raycast.RayCastUtility;

import java.util.*;

import static org.mooner.seungwoomaster.MoonerUtils.chat;
import static org.mooner.seungwoomaster.SeungWooMaster.master;

public class EventManager implements Listener {
    public static Map<UUID, Long> berserk = new HashMap<>();

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player defender) {
            GameManager gameManager = GameManager.getInstance();
            if (e.getDamager() instanceof Player attacker) {
                if (e.isCancelled()) return;
                calc(e, attacker, defender);
//                Material material = attacker.getLocation().getBlock().getType();
//                if (attacker.getFallDistance() > 0 && attacker.getVelocity().getY() < 0 && !attacker.isInsideVehicle() && !attacker.hasPotionEffect(PotionEffectType.BLINDNESS) && material != Material.LADDER && material != Material.VINE && material != Material.TWISTING_VINES_PLANT && material != Material.WEEPING_VINES_PLANT) {
//                    e.setDamage(e.getDamage() * 1.5);
//                }
                GameManager.getInstance().addMoney(attacker, (int) Math.ceil((e.getFinalDamage() * 100) / (e.getFinalDamage() + 20)));
            } else if (e.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player attacker) {
                calc(e, attacker, defender);
                gameManager.addMoney(attacker, (int) Math.ceil((e.getFinalDamage() * 500 * (gameManager.isAttackPlayer(attacker) ? 1 : 3)) / (e.getFinalDamage() + 30)));
            } else if (!gameManager.isClassic() && e.getDamager() instanceof TNTPrimed) {
                e.setDamage(EntityDamageEvent.DamageModifier.BASE, 6);
                e.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
                e.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);
                e.setDamage(EntityDamageEvent.DamageModifier.MAGIC, 0);
                e.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, 0);
            }
        }
    }


//    @EventHandler(priority = EventPriority.HIGH)
//    public void onFinal(EntityDamageByEntityEvent e) {
//        if (e.getEntity() instanceof Player) {
//            if (e.getDamager() instanceof Player attacker) {
//                if (!e.isCancelled()) {
//                    GameManager.getInstance().addMoney(attacker, (int) Math.ceil((e.getDamage() * 100) / (e.getDamage() + 20)));
//                }
//            }
//        }
//    }

    @EventHandler
    public void onDamaged(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player defender) {
            if (GameManager.getInstance().getStartTime() == 0) {
                e.setCancelled(true);
                return;
            }
            GameManager gameManager = GameManager.getInstance();
            switch (e.getCause()) {
                case CONTACT, SUFFOCATION, FIRE, LAVA, FALL, MELTING, DROWNING, BLOCK_EXPLOSION, ENTITY_EXPLOSION, VOID, LIGHTNING, SUICIDE, STARVATION, POISON, MAGIC, WITHER, FALLING_BLOCK, THORNS, DRAGON_BREATH, CUSTOM, FLY_INTO_WALL, HOT_FLOOR, CRAMMING, DRYOUT, FREEZE, SONIC_BOOM ->
                        e.setDamage(Math.max(0, e.getDamage() * (1 - gameManager.getModifier(defender).getValue(PlayerAttribute.NATURAL_DEFENSE))));
            }
            if (defender.getHealth() - e.getFinalDamage() > 0) {
                gameManager.checkHeal(defender);
            }
        }
    }

    public void calc(EntityDamageByEntityEvent e, Player attacker, Player defender) {
        GameManager gameManager = GameManager.getInstance();
        PlayerModifier attack = gameManager.getModifier(attacker);
        PlayerModifier defense = gameManager.getModifier(defender);
        Total total = gameManager.getTotal();

        boolean berserker = berserk.getOrDefault(attacker.getUniqueId(), 0L) + 10000 >= System.currentTimeMillis();
        boolean defBerserker = berserk.getOrDefault(defender.getUniqueId(), 0L) + 10000 >= System.currentTimeMillis();
        if (berserker) attacker.playSound(attacker.getLocation(), Sound.ENTITY_WARDEN_ATTACK_IMPACT, 1, 0.5f);

        double damage = (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE ? 5 : getToolDamage(attacker.getInventory().getItemInMainHand()));

        double criticalChance = attack.getValue(PlayerAttribute.CRITICAL_CHANCE) + 0.05 + (berserker ? 0.2 : 0);
        double criticalDamage = PlayerAttribute.CRITICAL_DAMAGE.getValue() * Math.max(0, attack.getLevel(PlayerAttribute.CRITICAL_DAMAGE) - defense.getLevel(PlayerAttribute.DEFENSE) * 0.4) + 0.25 + (berserker ? 0.04 : 0);

        if (!defBerserker && Math.random() < defense.getValue(PlayerAttribute.DODGE)) {
            attacker.playSound(attacker, Sound.ENTITY_IRON_GOLEM_REPAIR, 0.4f, 2);
            defender.playSound(defender, Sound.ENTITY_IRON_GOLEM_REPAIR, 0.4f, 2);
            attacker.sendTitle(" ", chat("&7MISS"), 2, 6, 3);
            defender.sendTitle(" ", chat("&9Dodged from " + attacker.getName() + "!"), 2, 15, 3);
            e.setDamage(0);
            total.addDodge(defender);
            return;
        }

        double additiveMultiplier = attack.getValue(PlayerAttribute.MELEE_ATTACK) + (berserker ? 0.24 : 0);
        double reducedMultiplier = defense.getValue(PlayerAttribute.DEFENSE);

        if (!gameManager.isClassic()) {
            switch (attack.getPlayerClass()) {
                case KNIGHT -> {
                    if (gameManager.isAttackPlayer(attacker)) {
                        reducedMultiplier *= 0.7;
                    } else {
                        attack.addCounter("knight");
                        if (attack.getIntCounter("knight") >= 7) {
                            criticalChance += 1;
                            criticalDamage += 2;
                            attack.resetCounter("knight");
                        }
                    }
                }
                case WIZARD -> {
                    criticalChance *= 0.5;
                    damage *= 0.5;
                    if (gameManager.isAttackPlayer(attacker)) {
                        if (Math.random() < 0.15) {
                            Location location = defender.getLocation().clone();
                            location.setDirection(attacker.getLocation().getDirection());
                            defender.teleport(location);
                        }
                    }
                }
                case BOXER -> {
                    if (attacker.getInventory().getItemInMainHand().getType() == Material.AIR) {
                        if (gameManager.isAttackPlayer(attacker)) {
                            damage = Math.max(getToolDamage(gameManager.getSwordTier(attacker).getSword()), getToolDamage(gameManager.getAxeTier(attacker).getAxe())) * .3;
                        } else {
                            damage += damage * .5 * attack.getIntCounter(defender.getName());
                            attack.addCounter(defender.getName());
                        }
                        attacker.setNoDamageTicks(2);
                    }
                }
                case NIGGER -> {
                }
                case SCIENTIST -> {
                }
            }
            switch (defense.getPlayerClass()) {
                case VAMPIRE -> {
                    if (!gameManager.isAttackPlayer(attacker)) {
                        defense.addCounter("vampire", damage * 0.2);
                    }
                }
                case GOBLIN -> {
                }
                case NIGGER -> {

                }
                case SCIENTIST -> {
                }
            }
        }

        double criticalMultiplier = 0;
        if (Math.random() < criticalChance && criticalDamage > 0) {
            criticalMultiplier = criticalDamage;
            attacker.playSound(attacker, Sound.BLOCK_ANVIL_LAND, 0.4f, 0.8f + (float) (Math.random() - 0.5f) * 0.2f);
            defender.playSound(defender, Sound.BLOCK_ANVIL_LAND, 0.4f, 0.8f + (float) (Math.random() - 0.5f) * 0.2f);
            attacker.sendTitle(" ", chat("&4{cc} CRITICAL! {cc}"), 3, 5, 15);
            defender.sendTitle(" ", chat("&c{cc} Critical by " + attacker.getName() + "! {cc}"), 3, 5, 15);

            if (Math.random() < (defense.getValue(PlayerAttribute.SWAP))) {
                for (int i = 0; i < 3; i++) {
                    Bukkit.getScheduler().runTaskLater(master, () -> {
                        attacker.playSound(attacker, Sound.BLOCK_BELL_USE, 1f, 1.5f);
                        defender.playSound(defender, Sound.BLOCK_BELL_USE, 1f, 1.5f);
                    }, 4 * i);
                }
                List<ItemStack> items = new ArrayList<>();
                for (int i = 0; i < 9; i++) {
                    items.add(attacker.getInventory().getItem(i));
                }
                Collections.shuffle(items);
                for (int i = 0; i < items.size(); i++) {
                    attacker.getInventory().setItem(i, items.get(i));
                }
                attacker.sendTitle(" ", chat("&4{cd} CRITICAL &9SWAP!! &4{cd}"), 3, 5, 15);
                defender.sendTitle(" ", chat("&4{cd} &9SWAP!! &4Critical by " + attacker.getName() + "! {cd}"), 3, 5, 15);
            } else {
                attacker.sendTitle(" ", chat("&4{cc} CRITICAL! {cc}"), 3, 5, 15);
                defender.sendTitle(" ", chat("&4{cc} Critical by " + attacker.getName() + "! {cc}"), 3, 5, 15);
            }
        }

        total.addDamage(attacker, damage * (1 + additiveMultiplier + criticalMultiplier));
        total.addCritical(attacker, damage * (1 + criticalMultiplier));
        total.addGainDamage(defender, damage * (1 + additiveMultiplier + criticalMultiplier));
        total.addDefenced(defender, damage * reducedMultiplier);

        if (criticalMultiplier - reducedMultiplier > 0)
            damage *= ((1 + additiveMultiplier) * (criticalMultiplier - reducedMultiplier));
        else damage *= (1 + criticalMultiplier - reducedMultiplier);

        if (!gameManager.isClassic()) {
            switch (attack.getPlayerClass()) {
                case VAMPIRE -> {
                    if (gameManager.isAttackPlayer(attacker)) {
                        attacker.setHealth(attacker.getHealth() + damage * 0.13);
                    }
                }
                case GOBLIN -> {
                    if (gameManager.isAttackPlayer(attacker)) {
                        if (!berserker && criticalMultiplier > 0 && Math.random() < 0.5) {
                            for (int i = 0; i < 9; i++) {
                                ItemStack item = defender.getInventory().getItem(i);
                                if (item != null && item.getType() != Material.AIR) {
                                    if (item.getType() == Material.GOLDEN_APPLE || item.getType() == Material.ENDER_PEARL || item.getType() == Material.DIAMOND) {
                                        item.setAmount(item.getAmount() - 1);
                                        ItemStack clone = item.clone();
                                        clone.setAmount(1);
                                        attacker.getInventory().addItem(clone);
                                        return;
                                    }
                                }
                            }
                        }
                    } else {
                        int removedMoney = gameManager.forceRemoveMoney(defender, (int) (damage * 11));
                        gameManager.addMoney(attacker, removedMoney);
                    }
                }
            }
        }

        e.setDamage(EntityDamageEvent.DamageModifier.BASE, damage);
        e.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
        e.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);
        e.setDamage(EntityDamageEvent.DamageModifier.MAGIC, 0);
        e.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, 0);

        // DEBUG MODE
//        Bukkit.broadcastMessage(chat(attacker.getName() + ": &7"+String.format("%3.1f", baseDamage)+" &c" + String.format("%3.1f", additiveMultiplier) + " &4" +  String.format("%3.1f", criticalMultiplier) + " &a" + String.format("%3.1f", reducedMultiplier) + "&f = &3" + String.format("%3.1f", damage)));

//        if (e.getDamage() != e.getFinalDamage()) {
//            if (e.getDamage() != 0 && e.getFinalDamage() != 0) {
//                e.setDamage(e.getDamage() / (e.getFinalDamage() / e.getDamage()));
//            }
//        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        GameManager gameManager = GameManager.getInstance();
        e.getEntity().setGameMode(GameMode.SPECTATOR);
        e.getEntity().setBedSpawnLocation(gameManager.getPlayMap().getLocation(), true);
        if (gameManager.isAttackPlayer(e.getEntity())) {
            new Respawn(e.getEntity());
        } else {
            Player killer = e.getEntity().getKiller();
            if (killer != null) {
                Bukkit.getScheduler().runTaskLater(master, () -> {
                    gameManager.addMoney(killer, 500);
                    killer.sendMessage(chat("&eYou killed &2Defender&e! You get &6500 coins&e more!"));
                    killer.playSound(killer, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                }, 20);
            }
            e.getEntity().setGameMode(GameMode.SPECTATOR);
            gameManager.end(true);
        }
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                e.setCancelled(true);
            } else if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN) {
                if (GameManager.getInstance().isAttackPlayer(e.getEntity().getUniqueId())) {
                    e.setAmount(e.getAmount() * 2);
                }
            }
        }
    }

    private final Map<UUID, Long> fierceEyesTime = new HashMap<>();

    private boolean canUse(Player player) {
        return fierceEyesTime.getOrDefault(player.getUniqueId(), 0L) < System.currentTimeMillis();
    }

    public void setTime(Player player) {
        fierceEyesTime.put(player.getUniqueId(), System.currentTimeMillis() + 10000);
    }

    public int getTime(Player player) {
        return (int) Math.ceil((fierceEyesTime.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000d);
    }

    private final ImmutableSet<Material> swords = ImmutableSet.of(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD);

    @EventHandler  // Removed Fierce Eyes
    public void onInteract(PlayerInteractEvent e) {
        GameManager gameManager = GameManager.getInstance();
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem() == null) return;

            PlayerModifier modifier = gameManager.getModifier(e.getPlayer());
            switch (e.getItem().getType()) {
                case GOLDEN_APPLE -> {
                    if (!gameManager.isAttackPlayer(e.getPlayer())) {
                        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
                        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 600, (int) Math.floor(Math.sqrt(Bukkit.getOnlinePlayers().size())) - 1));
                        e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);
                        e.getItem().setAmount(e.getItem().getAmount() - 1);
                    }
                }
//                case STONE_HOE -> {
//                    if (gameManager.isClassic()) return;
//                    List<Entity> players = RayCastUtility.rayCastPlayers(e.getPlayer(), 10, true, RayCastUtility.Precision.ACCURATE_ENTITY);
//                    if (gameManager.isAttackPlayer(e.getPlayer())) {
//                        for (Entity entity : players) {
//                            if (entity instanceof Player player) {
//                                if (gameManager.isAttackPlayer(player)) return;
//
//                            }
//                        }
//                    }
//                }
                case DIAMOND -> {
                    if (gameManager.isClassic()) return;
                    ActionBar.inverse();
                    e.getItem().setAmount(e.getItem().getAmount() - 1);
                }
            }
//                else if (swords.contains(e.getItem().getType())) {
//                    if (!canUse(e.getPlayer())) {
////                        e.getPlayer().sendMessage(chat("&cThis ability is on cooldown for &c" + getTime(e.getPlayer()) + "s."));
//                        return;
//                    }
//                    setTime(e.getPlayer());
//                    e.getPlayer().sendMessage(chat("&eYou used &6Fierce Eyes&e!"));
//                    for (Player player : Bukkit.getOnlinePlayers()) {
//                        if (GameManager.getInstance().isAttackPlayer(e.getPlayer())) {
//                            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20, 0, true, true, true));
//                        }
//                    }
//
//                    Bukkit.getScheduler().runTaskLater(master, () -> {
//                        for (Player player : Bukkit.getOnlinePlayers()) {
//                            if (GameManager.getInstance().isAttackPlayer(e.getPlayer())) {
//                                player.removePotionEffect(PotionEffectType.GLOWING);
//                            }
//                        }
//                    }, 100);
//                }
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e) {
        if (!GameManager.getInstance().isAttackPlayer(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    public double getToolDamage(ItemStack item) {
        return getToolDamage(item.getType());
    }

    public double getToolDamage(Material item) {
        return switch (item) {
            case WOODEN_SWORD -> 2;
            case STONE_SWORD -> 2.5;
            case IRON_SWORD -> 3;
            case GOLDEN_SWORD -> 3.5;
            case DIAMOND_SWORD -> 4;
            case NETHERITE_SWORD -> 5;

            case WOODEN_AXE -> 2.5;
            case STONE_AXE -> 3.5;
            case IRON_AXE -> 4;
            case GOLDEN_AXE -> 5;
            case DIAMOND_AXE -> 6;
            case NETHERITE_AXE -> 7;

//            case WOODEN_SWORD -> 3;
//            case STONE_SWORD -> 3.5;
//            case IRON_SWORD -> 4;
//            case GOLDEN_SWORD -> 5;
//            case DIAMOND_SWORD -> 6;
//            case NETHERITE_SWORD -> 7;
//
//            case WOODEN_AXE -> 5;
//            case STONE_AXE -> 6.5;
//            case IRON_AXE -> 7.5;
//            case GOLDEN_AXE -> 8.5;
//            case DIAMOND_AXE -> 9;
//            case NETHERITE_AXE -> 10;

            default -> 1;
        };
    }
}
