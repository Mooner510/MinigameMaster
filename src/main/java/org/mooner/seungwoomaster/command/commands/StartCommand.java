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
        if (args.length <= 0) GameManager.getInstance().start();
        else GameManager.getInstance().start(PlayMap.valueOf(args[0]));
        return true;
    }

    @Override
    public List<String> tabComplete(Player sender, String[] args) {
        return Arrays.stream(PlayMap.values()).map(Enum::toString).filter(it -> it.startsWith(args[0])).toList();
    }
}
