package org.mooner.seungwoomaster.game.actionbar;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.mooner.seungwoomaster.game.GameManager;
import org.mooner.seungwoomaster.game.modifier.PlayerAttribute;
import org.mooner.seungwoomaster.game.modifier.PlayerModifier;

import static org.mooner.seungwoomaster.MoonerUtils.chat;
import static org.mooner.seungwoomaster.MoonerUtils.parseString;
import static org.mooner.seungwoomaster.SeungWooMaster.master;

public class ActionBar {
    public static void runActionBar() {
        Bukkit.getScheduler().runTaskTimer(master, task -> {
            GameManager gameManager = GameManager.getInstance();
            if(!gameManager.isStarted()) {
                task.cancel();
                return;
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerModifier modifier = gameManager.getModifier(player);
                String builder = "&c" + parseString(player.getHealth(), 1) + '/' + parseString(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(), 1) + "{hp}" +
                        "    &aDEF " + parseString(modifier.getValue(PlayerAttribute.DEFENSE) * 100) + '%' +
                        "    &6" + gameManager.getMoney(player) + " Coins" +
                        "    &5" + gameManager.getToken(player) + " Tokens";
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(chat(builder)));
            }
        }, 0, 1);
    }
}
