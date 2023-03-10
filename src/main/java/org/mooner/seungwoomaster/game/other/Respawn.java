package org.mooner.seungwoomaster.game.other;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.mooner.seungwoomaster.game.GameManager;

import static org.mooner.seungwoomaster.MoonerUtils.chat;
import static org.mooner.seungwoomaster.MoonerUtils.getNearbySafeLocation;
import static org.mooner.seungwoomaster.SeungWooMaster.master;

public class Respawn {
    private int time;

    public Respawn(Player player) {
//        player.spigot().respawn();
        player.setGameMode(GameMode.SPECTATOR);
//        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        time = 6;
        Bukkit.getScheduler().runTaskTimer(master, task -> {
            if(time-- <= 1) {
                player.sendTitle(chat("&aRESPAWNED!"), "", 20, 60, 20);
                player.setGameMode(GameMode.ADVENTURE);
                player.teleport(getNearbySafeLocation(GameManager.getInstance().getPlayMap().getLocation(), 4, 12));
                task.cancel();
            } else {
                player.sendTitle(chat("&cYOU DIED!"), chat("&eYou will respawn in &c" + time + " &eseconds!"), 0, 22, 0);
            }
        }, 0, 20);
    }
}
