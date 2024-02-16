package org.mooner.seungwoomaster.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mooner.seungwoomaster.command.commands.AddCommand;
import org.mooner.seungwoomaster.command.commands.ShopCommand;
import org.mooner.seungwoomaster.command.commands.StartCommand;
import org.mooner.seungwoomaster.command.commands.VoteCommand;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {
    private final Map<String, ICommand> commandMap;

    public CommandManager() {
        commandMap = new HashMap<>();
        commandMap.put("start", new StartCommand());
        commandMap.put("shop", new ShopCommand());
        commandMap.put("add", new AddCommand());
        commandMap.put("vote", new VoteCommand());
    }

    public boolean onCommand(CommandSender sender, Command command, String[] args) {
        ICommand cmd = commandMap.get(command.getName());
        if (cmd == null) return false;
        if (sender instanceof Player player) return cmd.execute(player, args);
        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String[] args) {
        ICommand cmd = commandMap.get(command.getName());
        if (cmd == null) return Collections.emptyList();
        if (sender instanceof Player player) return cmd.tabComplete(player, args);
        return Collections.emptyList();
    }
}
