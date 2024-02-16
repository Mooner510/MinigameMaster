package org.mooner.seungwoomaster;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mooner.seungwoomaster.command.CommandManager;
import org.mooner.seungwoomaster.game.GameManager;
import org.mooner.seungwoomaster.game.listener.DoorOpener;

import java.util.List;

import static org.mooner.seungwoomaster.MoonerUtils.chat;

public final class SeungWooMaster extends JavaPlugin implements Listener {
    public static SeungWooMaster master;
    private static CommandManager commandManager;


    @Override
    public void onEnable() {
        // Plugin startup logic
        master = this;
        commandManager = new CommandManager();
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new DoorOpener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (!GameManager.getInstance().isStarted()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onExplode(BlockExplodeEvent e) {
        e.blockList().clear();
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        e.blockList().clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commandManager.onCommand(sender, command, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return commandManager.onTabComplete(sender, command, args);
    }
}
