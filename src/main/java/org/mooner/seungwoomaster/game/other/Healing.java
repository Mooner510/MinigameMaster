package org.mooner.seungwoomaster.game.other;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import static org.mooner.seungwoomaster.SeungWooMaster.master;

public class Healing {
    private final Player player;
    private boolean start;

    public Healing(Player player) {
        this.player = player;
    }

    public void start() {
        if(start) return;
        if(player.getHealth() < player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()) {
            start = true;
            Bukkit.getScheduler().runTaskTimer(master, task -> {
                if(player.isDead()) return;
                double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                if(player.getHealth() < maxHealth) {
                    player.setHealth(Math.min(maxHealth, player.getHealth() + 1));
                    return;
                }
                start = false;
                task.cancel();
            }, 60, 60);
        }
    }
}
