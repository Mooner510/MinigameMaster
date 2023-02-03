package org.mooner.seungwoomaster.command.commands;

import org.bukkit.command.CommandSender;
import org.mooner.seungwoomaster.command.ICommand;

import java.util.List;

public class StartCommand implements ICommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
