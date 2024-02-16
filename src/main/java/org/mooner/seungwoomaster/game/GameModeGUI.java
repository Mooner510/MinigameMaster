package org.mooner.seungwoomaster.game;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mooner.seungwoomaster.game.modifier.PlayerAttribute;
import org.mooner.seungwoomaster.game.modifier.PlayerModifier;

import java.util.Arrays;

import static org.mooner.seungwoomaster.MoonerUtils.*;
import static org.mooner.seungwoomaster.SeungWooMaster.master;
import static org.mooner.seungwoomaster.game.gui.GUIUtils.createItem;
import static org.mooner.seungwoomaster.game.gui.GUIUtils.ench;

public class GameModeGUI {
    private Inventory inventory;
    private Player player;
    private final Click listener = new Click();
    private final ItemStack glass = createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " ");
    private boolean voted;

    public GameModeGUI(Player player) {
        voted = false;
        Bukkit.getScheduler().runTaskAsynchronously(master, () -> {
            this.player = player;

            inventory = Bukkit.createInventory(player, 27, "토큰 강화");
            GameManager gameManager = GameManager.getInstance();
            PlayerModifier modifier = gameManager.getModifier(player);

            for (int i = 0; i < 26; i++) inventory.setItem(i, glass);

            inventory.setItem(12, createItem(Material.GRASS_BLOCK, 1, "&a클래식 모드",
                    "&c공격자&7와 &a방어자&7의 클래식한 전투 모드"
            ));
            inventory.setItem(14, createItem(Material.TOTEM_OF_UNDYING, 1, "&d직업 모드",
                    "&b직업&7을 가져 특수 능력을 사용하는 대난투 모드"));

            Bukkit.getScheduler().runTask(master, () -> {
                Bukkit.getPluginManager().registerEvents(listener, master);
                this.player.openInventory(inventory);
            });
        });
    }

    public class Click implements Listener {
        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getInventory().equals(inventory)) {
                ItemStack item = e.getCurrentItem();
                if (item == null || e.getClickedInventory() == null || item.getType().equals(Material.AIR)) return;
                if (e.getInventory().equals(inventory)) {
                    e.setCancelled(true);
                    if (e.getSlot() == 12) {
                        GameManager.getInstance().voteClassic(player);
                        player.closeInventory();
                    } else if (e.getSlot() == 14) {
                        GameManager.getInstance().voteAdvanced(player);
                        player.closeInventory();
                    }
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e) {
            if (inventory.equals(e.getInventory())) {
                if (!voted) {
                    Bukkit.getScheduler().runTaskLater(master, () -> e.getPlayer().openInventory(inventory), 1);
                    return;
                }
                HandlerList.unregisterAll(this);
                player = null;
                inventory = null;
            }
        }
    }
}
