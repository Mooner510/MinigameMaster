package org.mooner.seungwoomaster.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mooner.seungwoomaster.command.ICommand;
import org.mooner.seungwoomaster.game.GameManager;

import java.util.List;
import java.util.stream.Stream;

public class AddCommand implements ICommand {
    @Override
    public boolean execute(Player sender, String[] args) {
        if (!sender.getName().equals("Mooner510")) return false;
        GameManager gameManager = GameManager.getInstance();

        if (args.length <= 1) return false;
        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) return false;

        if (args[0].equals("token")) {
            gameManager.addToken(player, Integer.parseInt(args[2]));
        } else if (args[0].equals("money")) {
            gameManager.addMoney(player, Integer.parseInt(args[2]));
        }
        return true;
    }

    @Override
    public List<String> tabComplete(Player sender, String[] args) {
        if (!sender.getName().equals("Mooner510")) return ICommand.super.tabComplete(sender, args);

        if (args.length == 1)
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(it -> it.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        else if (args.length == 2)
            return Stream.of("token", "money").filter(it -> it.toLowerCase().startsWith(args[0].toLowerCase())).toList();

        return ICommand.super.tabComplete(sender, args);
    }
}
