package org.mooner.seungwoomaster.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mooner.seungwoomaster.command.ICommand;
import org.mooner.seungwoomaster.game.GameManager;

import java.util.List;

public class StartCommand implements ICommand {
    @Override
    public boolean execute(Player sender, String[] args) {
        GameManager.getInstance().start();
        return true;
    }
}
