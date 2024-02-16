package org.mooner.seungwoomaster.game.actionbar;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mooner.seungwoomaster.game.GameManager;
import org.mooner.seungwoomaster.game.modifier.PlayerAttribute;
import org.mooner.seungwoomaster.game.modifier.PlayerClass;
import org.mooner.seungwoomaster.game.modifier.PlayerModifier;

import static org.mooner.seungwoomaster.MoonerUtils.*;
import static org.mooner.seungwoomaster.SeungWooMaster.master;

public class ActionBar {
    public static int inverseTicks;

    private static boolean isInside(Location mapLocation, Location playerLocation) {
        boolean b = Math.abs(mapLocation.getX() - playerLocation.getX()) < 2.6 && Math.abs(mapLocation.getZ() - playerLocation.getZ()) < 2.6 && mapLocation.getY() - 1 <= playerLocation.getY() && mapLocation.getY() + 3 >= playerLocation.getY();
        if (inverseTicks > 0) return !b;
        return b;
    }

    public static void inverse() {
        if (inverseTicks <= 0) {
            inverseTicks = 400;
            Bukkit.getScheduler().runTaskTimer(master, task -> {
                if (inverseTicks-- <= 0) {
                    task.cancel();
                }
            }, 0, 1);
            return;
        }
        inverseTicks = 400;
    }

    public static void runActionBar() {
        GameManager gameManager = GameManager.getInstance();

        Bukkit.getScheduler().runTaskTimer(master, task -> {
            if (!gameManager.isStarted()) {
                task.cancel();
                return;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerModifier modifier = gameManager.getModifier(player);
                String builder = "&c" + parseString(player.getHealth(), 1) + '/' + parseString(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(), 1) + "{hp}" +
                        "    &aDEF " + parseString(modifier.getValue(PlayerAttribute.DEFENSE) * 100) + '%' +
                        (gameManager.getStartTime() != 0 ? ("    &b" + calcTime(gameManager.getStartTime(), 180)) : "") +
                        "    &6" + gameManager.getMoney(player) + " Coins" +
                        "    &5" + gameManager.getToken(player) + " Tokens";
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(chat(builder)));
                player.setFoodLevel(20);
            }
        }, 0, 1);

        Location loc = gameManager.getPlayMap().getLocation();
        Bukkit.getScheduler().runTaskTimer(master, task -> {
            if (!gameManager.isStarted()) {
                task.cancel();
                return;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (gameManager.getStartTime() == 0) {
                    player.removePotionEffect(PotionEffectType.CONFUSION);
                    player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                    player.removePotionEffect(PotionEffectType.SLOW);
                    player.removePotionEffect(PotionEffectType.POISON);
                    return;
                }
                Location location = player.getLocation();
                if (player.hasPotionEffect(PotionEffectType.GLOWING)) {
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                    player.removePotionEffect(PotionEffectType.CONFUSION);
                    player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                    player.removePotionEffect(PotionEffectType.SLOW);
                    player.removePotionEffect(PotionEffectType.POISON);
                    return;
                }
                if (gameManager.isAttackPlayer(player)) {
//                    Bukkit.broadcastMessage("x: " + Math.abs(loc.getX() - player.getLocation().getX()) + " / z: " + Math.abs(loc.getZ() - player.getLocation().getZ()));
                    if (isInside(loc, location)) {
                        if (!player.hasPotionEffect(PotionEffectType.BLINDNESS))
                            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10000000, 0, false, false, false));
                        if (!player.hasPotionEffect(PotionEffectType.CONFUSION))
                            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10000000, 5, false, false, false));
                        if (!player.hasPotionEffect(PotionEffectType.SLOW_DIGGING))
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 10000000, 10, false, false, false));
                        if (!player.hasPotionEffect(PotionEffectType.SLOW))
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10000000, 3, false, false, false));
                        if (!player.hasPotionEffect(PotionEffectType.POISON))
                            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 10000000, 3, false, false, false));
                    } else {
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        player.removePotionEffect(PotionEffectType.CONFUSION);
                        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                        player.removePotionEffect(PotionEffectType.SLOW);
                        player.removePotionEffect(PotionEffectType.POISON);
                    }
                } else {
                    PlayerModifier modifier = gameManager.getModifier(player);
                    if (!gameManager.isClassic()) {
                        if (modifier.getPlayerClass() == PlayerClass.NIGGER) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 0, false, false, false));
                            player.removePotionEffect(PotionEffectType.BLINDNESS);
                            player.removePotionEffect(PotionEffectType.CONFUSION);
                            player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                            player.removePotionEffect(PotionEffectType.SLOW);
                            player.removePotionEffect(PotionEffectType.POISON);
                            return;
                        }
                    }
                    if (isInside(loc, location)) {
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        player.removePotionEffect(PotionEffectType.CONFUSION);
                        player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
                        player.removePotionEffect(PotionEffectType.SLOW);
                        player.removePotionEffect(PotionEffectType.POISON);
                    } else {
                        if (!player.hasPotionEffect(PotionEffectType.BLINDNESS))
                            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10000000, 0, false, false, false));
                        if (!player.hasPotionEffect(PotionEffectType.CONFUSION))
                            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10000000, 0, false, false, false));
                        if (!player.hasPotionEffect(PotionEffectType.SLOW_DIGGING))
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 10000000, 4, false, false, false));
                        if (!player.hasPotionEffect(PotionEffectType.SLOW))
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10000000, 2, false, false, false));
                        if (!player.hasPotionEffect(PotionEffectType.POISON))
                            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 10000000, 1, false, false, false));
                    }
                }
            }
        }, 0, 1);

        Bukkit.getScheduler().runTaskTimer(master, task -> {
            if (!gameManager.isStarted()) {
                task.cancel();
                return;
            }
            if (gameManager.getStartTime() != 0 && gameManager.getStartTime() + 180000 <= System.currentTimeMillis()) {
                gameManager.end(false);
            }
        }, 0, 1);
    }
}
