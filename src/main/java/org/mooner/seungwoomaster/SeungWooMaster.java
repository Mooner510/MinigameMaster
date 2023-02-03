package org.mooner.seungwoomaster;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

//extends 상속
// implements 인터 페이스 상속

public final class SeungWooMaster extends JavaPlugin implements Listener {
    public static SeungWooMaster master;


    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    // EVENT
    // 받는애(EventHandler, Listener)와 주는애(Sender)

    @EventHandler
    public void onAttackByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.PLAYER) {
            Bukkit.broadcastMessage("멍청한것");
        } else if (event.getDamager() instanceof Zombie) {
            Bukkit.broadcastMessage("나쁜 좀비들");
        } else if( event.getDamager() instanceof Projectile && event.getEntity() instanceof LivingEntity livingEntity) {
            Bukkit.broadcastMessage(livingEntity.getType() + (livingEntity.hasAI() ? " AI 끈다!!" : " AI 킨다!!"));
            livingEntity.setAI(!livingEntity.hasAI());
        } else {
            Bukkit.broadcastMessage("조현석이 때렸다!");
        }
    }

    // https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/entity/package-summary.html


    @EventHandler
    public void puha(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Firework firework) {
            firework.setLife(12700);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return super.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return super.onTabComplete(sender, command, alias, args);
    }
}
