package org.mooner.seungwoomaster.game.modifier;

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
import org.mooner.seungwoomaster.game.GameManager;
import org.mooner.seungwoomaster.game.gui.GUIUtils;
import org.mooner.seungwoomaster.skull.SkullCreator;

import java.util.Arrays;

import static org.mooner.seungwoomaster.MoonerUtils.*;
import static org.mooner.seungwoomaster.SeungWooMaster.master;
import static org.mooner.seungwoomaster.game.gui.GUIUtils.*;

public class ClassGUI {
    private Inventory inventory;
    private Player player;
    private final Click listener = new Click();
    private final ItemStack glass = createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " ");

    public ClassGUI(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(master, () -> {
            this.player = player;

            inventory = Bukkit.createInventory(player, 36, "클래스를 선택하세요");
            GameManager gameManager = GameManager.getInstance();
            PlayerModifier modifier = gameManager.getModifier(player);

            for (int i = 0; i < 36; i++) inventory.setItem(i, glass);

            update(modifier);

            inventory.setItem(31, createItem(Material.GRAY_DYE, 10, "&7준비",
                    "&c준비하려면 클릭하세요."
            ));

            for (int i = 9; i >= 1; i--) {
                int finalI = i;
                Bukkit.getScheduler().runTaskLater(master, () ->
                        inventory.setItem(40, createItem(Material.GRAY_DYE, finalI, "&7준비",
                                "&c준비하려면 클릭하세요."
                        )), (10 - i) * 20L);
            }

            Bukkit.getScheduler().runTaskLater(master, () ->
                    inventory.setItem(40, createItem(Material.LIME_DYE, 1, "&a준비",
                            "&e준비하려면 클릭하세요."
                    )), 200);

            Bukkit.getScheduler().runTask(master, () -> {
                Bukkit.getPluginManager().registerEvents(listener, master);
                this.player.openInventory(inventory);
            });
        });
    }

    public void update(PlayerModifier modifier) {

        inventory.setItem(10, createItem(Material.BEACON, 1,
                modifier.getPlayerClass() == PlayerClass.KNIGHT,
                "&e기사",
                "&c[ 공격자 ] &7- &6검술",
                "가하는 치명타는 방어력을 30% 무시합니다.",
                "",
                "&a[ 방어자 ] &7- &6손놀림",
                "매 7번째 공격은 치명타 확률 100%, 치명타 피해 200% 상승합니다."
        ));

        inventory.setItem(11, createItem(Material.DRAGON_BREATH, 1,
                modifier.getPlayerClass() == PlayerClass.WIZARD,
                "&d마법사",
                "치명타 확률과 근접 공격력이 50% 감소합니다.",
                "",
                "&c[ 공격자 ] &7- &6마법",
                "공격을 성공했을 경우 15% 확률로 자신과 같은 곳을 바라보도록 만듭니다.",
                "자신이 사망했을 경우, 방어자를 자신이 사망한 위치로 이동시킵니다.",
                "",
                "&a[ 방어자 ] &7- &6손놀림",
                "매 8번째 공격은 치명타 확률 100%, 치명타 피해 200% 상승합니다."
        ));

        inventory.setItem(12, createItem(Material.BEACON, 1,
                modifier.getPlayerClass() == PlayerClass.VAMPIRE,
                "&6뱀파이어",
                "&c[ 공격자 ] &7- &6흡혈",
                "가한 최종 피해량의 &a13% &7만큼 회복합니다.",
                "",
                "&a[ 방어자 ] &7- &6피의 축제",
                "공격을 받았을 때 상대가 공격한 무기의 피해량의 20%를 기록합니다.",
                "다음 번 공격 시 무기의 공격력에 기록된 피해량이 추가됩니다.",
                "공격을 시전했을 경우, 기록된 피해량은 사라집니다."
        ));

        inventory.setItem(13, createItem(Material.NETHERITE_SCRAP, 1,
                modifier.getPlayerClass() == PlayerClass.BOXER,
                "&b복서",
                "받는 피해가 20% 증가합니다.",
                "주먹으로 공격시 첫 공격과 두 번째 공격 사이의 시간이 80% 감소합니다.",
                "",
                "&c[ 공격자 ] &7- &6강철 주먹",
                "주먹으로 공격시 가지고 있는 가장 강한 무기의 30% 피해를 가합니다.",
                "",
                "&a[ 방어자 ] &7- &6콤보",
                "주먹으로 공격할 때마다 대상에게 가하는 공격력이 첫 피해의 50%씩 증가합니다.",
                "대상이 부활할 경우 증가한 피해량은 초기화됩니다."
        ));

        inventory.setItem(14, createItem(Material.BEACON, 1,
                modifier.getPlayerClass() == PlayerClass.GOBLIN,
                "&a고블린",
                "&c[ 공격자 ] &7- &6금품 갈취",
                "방어자가 소모형 아이템을 소지하고 있고 버서커 상태가 아닐 경우",
                "크리티컬 발생시 50% 확률로 소모형 아이템 한 개를 빼앗습니다.",
                "",
                "&a[ 방어자 ] &7- &6금품 갈취",
                "&6가한 최종 피해량의 11배 &7만큼 상대의 돈을 빼앗습니다."
        ));

        inventory.setItem(15, createItem(Material.BEACON, 1,
                modifier.getPlayerClass() == PlayerClass.SCIENTIST,
                "&7과학자",
                "&c[ 공격자 ] &7- &6영역 교체",
                "아이템을 소모하여 20초 동안 공격자와 방어자의 영역을 반전 시킵니다.",
                "매 라운드마다 반전된 영역은 초기화 됩니다.",
                "라운드 시작시, 아이템이 4개 지급됩니다.",
                "",
                "&a[ 방어자 ] &7- &6타임 머신",
                "공격자를 살해할 때마다 남은 시간이 2초 감소합니다."
        ));

        inventory.setItem(16, createItem(Material.COCOA_BEANS, 1,
                modifier.getPlayerClass() == PlayerClass.NIGGER,
                "&8니거",
                "&c[ 공격자 ] &7- &6비장의 무기",
                "죽은 자리에 33%의 기본 확률로 폭발물을 설치합니다.",
                "",
                "&a[ 방어자 ] &7- &6흑색 불빛",
                "다음 효과가 상시 적용됩니다.",
                "  - 발광 효과가 상시 적용됩니다.",
                "  - 돈을 잃을 경우 20%의 기본 확률로 잃지 않습니다.",
                "  - 16%의 기본 확률로 죽인 자리에 폭발물을 설치합니다.",
                "",
                "&c폭발물",
                "  거리와 상관 없이 폭발 피해를 입을 경우",
                "  방어력과 상관 없이 &c6{hp}의 &f고정 피해&7를 입힙니다."
        ));
    }

    public class Click implements Listener {
        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getInventory().equals(inventory)) {
                ItemStack item = e.getCurrentItem();
                if (item == null || e.getClickedInventory() == null || item.getType().equals(Material.AIR)) return;
                if (e.getInventory().equals(inventory)) {
                    e.setCancelled(true);
                    if (item.getType() == Material.LIME_DYE) {
                        player.closeInventory();
                        return;
                    }
                    GameManager gameManager = GameManager.getInstance();
                    PlayerModifier modifier = gameManager.getModifier(player);
//                    int level;
//                    switch (e.getSlot()) {
//                        default -> {
//                            return;
//                        }
//                    }
//                    String displayName = item.getItemMeta().getDisplayName();
//                    if (level > 0) {
//                        String[] s = displayName.split(" ");
//                        displayName = String.join(" ", Arrays.copyOf(s, s.length - 1));
//                    }
//                    player.sendMessage(chat(displayName + "&a을(를) &b" + (level + 1) + "레벨&a로 강화했습니다."));
//                    player.playSound(player, Sound.BLOCK_ANVIL_USE, 0.8f, 2f);
//                    update(modifier);
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e) {
            if (inventory.equals(e.getInventory())) {
                if (e.getInventory().getItem(40).getType() != Material.LIME_DYE) {
                    Bukkit.getScheduler().runTaskLater(master, () -> e.getPlayer().openInventory(inventory), 1);
                    return;
                }
                GameManager gameManager = GameManager.getInstance();
                gameManager.setReady(player);
                gameManager.getModifier(player).refresh();
                HandlerList.unregisterAll(this);
                player = null;
                inventory = null;
            }
        }
    }
}
