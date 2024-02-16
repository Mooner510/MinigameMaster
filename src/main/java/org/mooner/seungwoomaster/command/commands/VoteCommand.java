package org.mooner.seungwoomaster.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.mooner.seungwoomaster.command.ICommand;
import org.mooner.seungwoomaster.game.GameManager;
import org.mooner.seungwoomaster.game.GameModeGUI;
import org.mooner.seungwoomaster.game.PlayMap;

import java.util.Arrays;
import java.util.List;

public class VoteCommand implements ICommand {
    @Override
    public boolean execute(Player sender, String[] args) {
        if (!sender.getName().equals("Mooner510")) return false;
        if (args.length == 0 || args[0].equals("random")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                new GameModeGUI(player);
            }
        } else if (args[1].equals("classic")) {
            GameManager.getInstance().voteRun(1);
        } else {
            GameManager.getInstance().voteRun(2);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(Player sender, String[] args) {
        return List.of("random", "classic", "advanced");
    }
}
