package org.mooner.seungwoomaster.command.commands;

import org.bukkit.entity.Player;
import org.mooner.seungwoomaster.command.ICommand;
import org.mooner.seungwoomaster.game.GameManager;
import org.mooner.seungwoomaster.game.PlayMap;

import java.util.Arrays;
import java.util.List;

public class StartCommand implements ICommand {
    @Override
    public boolean execute(Player sender, String[] args) {
        if (!sender.getName().equals("Mooner510")) return false;
        if (args.length == 0) GameManager.getInstance().stop();
        GameManager.multipleRound = Integer.parseInt(args[0]);
        if (args.length == 1) GameManager.getInstance().start();
        else if (args.length == 2) GameManager.getInstance().start(PlayMap.valueOf(args[1]));
        return true;
    }

    @Override
    public List<String> tabComplete(Player sender, String[] args) {
        if (args.length == 2)
            return Arrays.stream(PlayMap.values()).map(Enum::toString).filter(it -> it.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        else return ICommand.super.tabComplete(sender, args);
    }
}
