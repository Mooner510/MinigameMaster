package org.mooner.seungwoomaster.command;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public interface ICommand {
    boolean execute(Player sender, String[] args);

    default List<String> tabComplete(Player sender, String[] args) {
        return Collections.emptyList();
    }
}
