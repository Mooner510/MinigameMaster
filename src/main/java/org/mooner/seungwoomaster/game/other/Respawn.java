package org.mooner.seungwoomaster.game.other;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.mooner.seungwoomaster.game.GameManager;
import org.mooner.seungwoomaster.game.modifier.PlayerClass;
import org.mooner.seungwoomaster.game.modifier.PlayerModifier;

import java.util.Random;

import static org.mooner.seungwoomaster.MoonerUtils.chat;
import static org.mooner.seungwoomaster.MoonerUtils.getNearbySafeLocation;
import static org.mooner.seungwoomaster.SeungWooMaster.master;

public class Respawn {
    private int time;

    private static void spawnTNT(Location location) {
        World world = location.getWorld();
        if (world == null) return;
        TNTPrimed tnt = (TNTPrimed) world.spawnEntity(location, EntityType.PRIMED_TNT);
        tnt.setFuseTicks(new Random().nextInt(31) + 20); // 20 ~ 20 + 31
    }

    public Respawn(Player player) {
        GameManager gameManager = GameManager.getInstance();

        // 죽은 순간
        if (!gameManager.isClassic()) {
            if (gameManager.isAttackPlayer(player) && gameManager.getModifier(player).getPlayerClass() == PlayerClass.NIGGER && Math.random() < 0.33) {
                TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(new Random().nextInt(31) + 20); // 20 ~ 20 + 31
            }

            if (!gameManager.isAttackPlayer(player) && gameManager.getModifier(player.getKiller()).getPlayerClass() == PlayerClass.NIGGER && Math.random() < 0.16) {
                TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(new Random().nextInt(31) + 20); // 20 ~ 20 + 31
            }
        }

//        player.spigot().respawn();
        player.setGameMode(GameMode.SPECTATOR);
//        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        time = 6;
        Bukkit.getScheduler().runTaskTimer(master, task -> {
            if (time-- <= 1) {
                player.sendTitle(chat("&a살아났농!!"), "", 20, 60, 20);
                player.setGameMode(GameMode.ADVENTURE);
                player.teleport(getNearbySafeLocation(gameManager.getPlayMap().getLocation(), 4, 12));

                for (Player p : Bukkit.getOnlinePlayers()) {
                    PlayerModifier modifier = gameManager.getModifier(p);
                    modifier.resetCounter(player.getName());
                }

                task.cancel();
            } else {
                player.sendTitle(chat("&c에휴 죽었농!"), chat("&e부활은 &c" + time + " &e초 후에!"), 0, 22, 0);
            }
        }, 0, 20);
    }
}
