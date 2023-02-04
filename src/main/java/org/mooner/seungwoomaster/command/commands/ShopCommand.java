package org.mooner.seungwoomaster.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mooner.seungwoomaster.command.ICommand;
import org.mooner.seungwoomaster.game.other.HowToPlay;
import org.mooner.seungwoomaster.game.upgrade.TokenGUI;

public class ShopCommand implements ICommand {
    @Override
    public boolean execute(Player sender, String[] args) {
        Bukkit.broadcastMessage("asdf");
        if (args.length <= 0) new TokenGUI(sender);
        else new HowToPlay(sender);
        return true;
    }
}
