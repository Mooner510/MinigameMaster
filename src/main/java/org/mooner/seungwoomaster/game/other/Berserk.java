package org.mooner.seungwoomaster.game.other;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mooner.seungwoomaster.game.GameManager;
import org.mooner.seungwoomaster.game.listener.EventManager;

import static org.mooner.seungwoomaster.MoonerUtils.chat;
import static org.mooner.seungwoomaster.MoonerUtils.getNearbySafeLocation;
import static org.mooner.seungwoomaster.SeungWooMaster.master;

public class Berserk {
    private int time;
    private int preTime;

    public Berserk(Player p) {
        preTime = 5;
        time = 10 * 10;
        p.playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 2f);
        Bukkit.getScheduler().runTaskTimer(master, task -> {
            if(preTime <= 0) {
                enable(p);
                task.cancel();
                return;
            }
            p.sendMessage(chat("&c" + preTime-- + "초 &e후 &c버서커&7가 해제됩니다."));
            p.playSound(p, Sound.UI_BUTTON_CLICK, 1, 2f);
        }, 0, 20);
    }

    private void enable(Player p) {
        Bukkit.broadcastMessage(chat(" \n  " + p.getDisplayName() + "&7이(가) &c버서커의 포효&7를 느낍니다!\n"));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1.5f);
            player.playSound(p.getLocation(), Sound.ENTITY_WARDEN_EMERGE, 1, 1.5f);
        }
        EventManager.berserk.put(p.getUniqueId(), System.currentTimeMillis());

        p.getInventory().setHelmet(new ItemStack(Material.DRAGON_HEAD));
        Bukkit.getScheduler().runTaskTimer(master, task -> {
            p.spawnParticle(Particle.CRIT_MAGIC, p.getLocation().clone().add(0, 1, 0), 20, 0.2, 0.2, 0.2);
            if(time-- <= 1) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(p.getLocation(), Sound.ENTITY_WARDEN_DEATH, 1, 1);
                }
                GameManager.getInstance().reloadArmor(p);
                task.cancel();
            }
        }, 0, 2);
    }
}
