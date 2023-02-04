package org.mooner.seungwoomaster.command.commands;

import org.bukkit.entity.Player;
import org.mooner.seungwoomaster.command.ICommand;
import org.mooner.seungwoomaster.game.GameManager;

public class StartCommand implements ICommand {
    @Override
    public boolean execute(Player sender, String[] args) {
        GameManager.getInstance().start();
        return true;
    }
}
