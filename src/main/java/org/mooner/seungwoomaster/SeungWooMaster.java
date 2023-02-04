package org.mooner.seungwoomaster;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.mooner.seungwoomaster.command.CommandManager;

import java.util.List;

public final class SeungWooMaster extends JavaPlugin {
    public static SeungWooMaster master;
    private static CommandManager commandManager;


    @Override
    public void onEnable() {
        // Plugin startup logic
        master = this;
        commandManager = new CommandManager();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
