package org.mooner.seungwoomaster.game.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.mooner.seungwoomaster.game.GameManager;
import org.mooner.seungwoomaster.game.modifier.PlayerAttribute;
import org.mooner.seungwoomaster.game.modifier.PlayerModifier;
import org.mooner.seungwoomaster.game.other.Respawn;

import static org.mooner.seungwoomaster.MoonerUtils.chat;

public class EventManager implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player defender) {
            GameManager gameManager = GameManager.getInstance();
            if (e.getDamager() instanceof Player attacker) {
                calc(e, attacker, defender);
                Material material = attacker.getLocation().getBlock().getType();
                if(attacker.getFallDistance() > 0 && attacker.getVelocity().getY() < 0 && !attacker.isInsideVehicle() && !attacker.hasPotionEffect(PotionEffectType.BLINDNESS) && material != Material.LADDER && material != Material.VINE && material != Material.TWISTING_VINES_PLANT && material != Material.WEEPING_VINES_PLANT) {
                    e.setDamage(e.getDamage() * 1.5);
                }
                gameManager.addMoney(attacker, (int) ((e.getDamage() * 30 * (gameManager.isAttackPlayer(attacker) ? 1 : 3)) / (e.getDamage() + 30)));
            } else if (e.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player attacker) {
                calc(e, attacker, defender);
                gameManager.addMoney(attacker, (int) ((e.getDamage() * 50 * (gameManager.isAttackPlayer(attacker) ? 1 : 3)) / (e.getDamage() + 30)));
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
            attacker.sendTitle(" ", chat("&c{cc} Critical by " + attacker.getName() + "! {cc}"), 3, 5, 15);
            additiveMultiplier *= attack.getValue(PlayerAttribute.CRITICAL_DAMAGE);
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
        if (!gameManager.isAttackPlayer(e.getEntity())) {
            Player killer = e.getEntity().getKiller();
            if (killer != null) {
                gameManager.addMoney(killer, 500);
                killer.sendMessage(chat("&eYou killed &2Defender&e! You get &6500 coins&e!"));
            }
            e.getEntity().setGameMode(GameMode.SPECTATOR);
            e.getEntity().setHealth(e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            gameManager.end(true);
        } else {
            new Respawn(e.getEntity());
        }
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player player) {
            if(e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                e.setCancelled(true);
            }
        }
    }

    public double getToolDamage(ItemStack item) {
        return switch (item.getType()) {
            case WOODEN_SWORD -> 3;
            case STONE_SWORD -> 3.5;
            case IRON_SWORD -> 4;
            case GOLDEN_SWORD -> 5;
            case DIAMOND_SWORD -> 6;
            case NETHERITE_SWORD -> 7;

            case WOODEN_AXE -> 5;
            case STONE_AXE -> 6.5;
            case IRON_AXE -> 7.5;
            case GOLDEN_AXE -> 8.5;
            case DIAMOND_AXE -> 9;
            case NETHERITE_AXE -> 10;

            default -> 1;
        };
    }

//    public void on
}
