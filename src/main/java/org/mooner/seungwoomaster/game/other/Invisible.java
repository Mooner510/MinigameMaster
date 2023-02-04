package org.mooner.seungwoomaster.game.other;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

import static org.mooner.seungwoomaster.SeungWooMaster.master;

public class Invisible {
    private final UUID uuid;
    private int tick;

    public Invisible(Player player, int tick) {
        this.uuid = player.getUniqueId();
        this.tick = tick;


//        Bukkit.getScheduler().runTaskTimer(master, task -> {
//            if(this.tick-- > 0) {
//                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 21, 0, false, false, false));
//            } else {
//                player.
//            }
//        })
    }

    public void addTime(int tick) {
        this.tick += tick;
    }
}
