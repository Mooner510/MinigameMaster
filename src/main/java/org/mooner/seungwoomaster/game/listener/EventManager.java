package org.mooner.seungwoomaster.game.listener;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import org.inventivetalent.glow.GlowAPI;
import org.mooner.seungwoomaster.game.GameManager;
import org.mooner.seungwoomaster.game.modifier.PlayerAttribute;
import org.mooner.seungwoomaster.game.modifier.PlayerModifier;
import org.mooner.seungwoomaster.game.other.Respawn;

import static org.mooner.seungwoomaster.MoonerUtils.chat;
import static org.mooner.seungwoomaster.SeungWooMaster.master;

public class EventManager implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player defender) {
            GameManager gameManager = GameManager.getInstance();
            if (e.getDamager() instanceof Player attacker) {
                calc(e, attacker, defender);
                Material material = attacker.getLocation().getBlock().getType();
                if (attacker.getFallDistance() > 0 && attacker.getVelocity().getY() < 0 && !attacker.isInsideVehicle() && !attacker.hasPotionEffect(PotionEffectType.BLINDNESS) && material != Material.LADDER && material != Material.VINE && material != Material.TWISTING_VINES_PLANT && material != Material.WEEPING_VINES_PLANT) {
                    e.setDamage(e.getDamage() * 1.5);
                }
                gameManager.addMoney(attacker, (int) Math.ceil((e.getDamage() * 30 * (gameManager.isAttackPlayer(attacker) ? 1 : 3)) / (e.getDamage() + 30)));
            } else if (e.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player attacker) {
                calc(e, attacker, defender);
                gameManager.addMoney(attacker, (int) Math.ceil((e.getDamage() * 50 * (gameManager.isAttackPlayer(attacker) ? 1 : 3)) / (e.getDamage() + 30)));
            }
        }
    }

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
        PlayerModifier attack = GameManager.getInstance().getModifier(attacker);
        PlayerModifier defense = GameManager.getInstance().getModifier(defender);

        if (Math.random() < defense.getValue(PlayerAttribute.DODGE)) {
            attacker.sendTitle(" ", chat("&7MISS"), 2, 6, 3);
            defender.sendTitle(" ", chat("&9Dodged from " + attacker.getName() + "!"), 2, 15, 3);
            e.setDamage(0);
            return;
        }

        double additiveMultiplier = attack.getValue(PlayerAttribute.MELEE_ATTACK);
        if (Math.random() < attack.getValue(PlayerAttribute.CRITICAL_CHANCE)) {
            attacker.sendTitle(" ", chat("&4{cc} CRITICAL! {cc}"), 3, 5, 15);
            defender.sendTitle(" ", chat("&c{cc} Critical by " + attacker.getName() + "! {cc}"), 3, 5, 15);
            additiveMultiplier += PlayerAttribute.CRITICAL_DAMAGE.getValue() * Math.max(0, attack.getLevel(PlayerAttribute.CRITICAL_DAMAGE) - attack.getLevel(PlayerAttribute.DEFENSE) * 0.9);
        }

        double reducedMultiplier = attack.getValue(PlayerAttribute.DEFENSE);
        double damage = (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE ? 5 : getToolDamage(attacker.getInventory().getItemInMainHand())) * (1 + additiveMultiplier - reducedMultiplier);
        e.setDamage(damage);
        GameManager.getInstance().addDamage(attacker, damage);

        if (e.getDamage() != e.getFinalDamage()) {
            if (e.getDamage() != 0 && e.getFinalDamage() != 0) {
                e.setDamage(e.getDamage() / (e.getFinalDamage() / e.getDamage()));
            }
        }
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
        e.getEntity().setBedSpawnLocation(gameManager.getPlayMap().getLocation());
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
                if (!GameManager.getInstance().isAttackPlayer(e.getEntity().getUniqueId())) {
                    e.setAmount(e.getAmount() * 2);
                }
            }
        }
    }

    private long fierceEyesTime = 0;

    private final ImmutableSet<Material> swords = ImmutableSet.of(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD);

    @EventHandler
    public void onInterect(PlayerInteractEvent e) {
        if (!GameManager.getInstance().isAttackPlayer(e.getPlayer())) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getItem() == null) return;
                if (e.getItem().getType() == Material.GOLDEN_APPLE) {
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
                    e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 1));
                    e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);
                    e.getItem().setAmount(e.getItem().getAmount() - 1);
                } else if (swords.contains(e.getItem().getType())) {
                    if (fierceEyesTime > System.currentTimeMillis()) {
                        e.getPlayer().sendMessage(chat("&cThis ability is on cooldown for &c" + Math.ceil(fierceEyesTime / 1000d) + "s."));
                        e.getPlayer().playSound(e.getPlayer(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.75f, 0.5f);
                        return;
                    }
                    fierceEyesTime = System.currentTimeMillis() + 10000;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        GlowAPI.setGlowing(player, GlowAPI.Color.RED, e.getPlayer());
                    }

                    Bukkit.getScheduler().runTaskLater(master, () -> {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            GlowAPI.setGlowing(player, GlowAPI.Color.NONE, e.getPlayer());
                        }
                    }, 100);
                }
            }
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
            case WOODEN_SWORD -> 1.5;
            case STONE_SWORD -> 2;
            case IRON_SWORD -> 2.5;
            case GOLDEN_SWORD -> 3;
            case DIAMOND_SWORD -> 4;
            case NETHERITE_SWORD -> 5.5;

            case WOODEN_AXE -> 2.5;
            case STONE_AXE -> 3.25;
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
