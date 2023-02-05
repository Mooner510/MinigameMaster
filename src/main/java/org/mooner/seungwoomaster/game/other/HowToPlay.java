package org.mooner.seungwoomaster.game.other;

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

import static org.mooner.seungwoomaster.SeungWooMaster.master;
import static org.mooner.seungwoomaster.game.gui.GUIUtils.createItem;

public class HowToPlay {
    private Inventory inventory;
    private Player player;
    private final Click listener = new Click();
    private final ItemStack glass = createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " ");

    public HowToPlay(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(master, () -> {
            this.player = player;

            inventory = Bukkit.createInventory(player, 54, "Are you ready?");

            for (int i = 0; i < 54; i++) inventory.setItem(i, glass);

            inventory.setItem(10, createItem(Material.IRON_SWORD, 1, "&cAttacker",
                    "&c공격자&7는, &a방어자&7를 &e5분&7 내에 죽여야 합니다!",
                    "&a방어자&7를 죽인 &c공격자&7는, &6500코인&7을 추가로 획득합니다!",
                    "",
                    "&f기본 아이템:",
                    "  &f• &dElytra &9(Unbreakable)",
                    "  &f• &cInfinity Firework Rocket",
                    "",
                    "&f유틸리티 아이템:",
                    "  &f• &cFire Force",
                    "  &f• &5Ender Pearl",
                    "  &f• &8Darkness Blast"
            ));

            inventory.setItem(13, createItem(Material.DIAMOND, 1, "&bWin / Lose / End",
                    "라운드가 종료되면, 다음과 같은 보상을 획득합니다.",
                    "",
                    "&f승리시:",
                    "  &f• &63500 Coins",
                    "  &f• &5Ability Token x16",
                    "",
                    "&f패배시:",
                    "  &f• &62500 Coins",
                    "  &f• &5Ability Token x10",
                    "",
                    "또한, 피해량이 높을 수록 더 많은 코인을 획득합니다.",
                    "이때 방어자는 피해량 보너스에 해당되지 않습니다.",
                    "  &c1위&f: &61200 Coins",
                    "  &62위&f: &6800 Coins",
                    "  &c3위&f: &6400 Coins"
            ));

            inventory.setItem(16, createItem(Material.IRON_CHESTPLATE, 1, "&aDefender",
                    "&a방어자&7는, &c공격자&7로부터 &e5분&7동안",
                    "생존해야 합니다!",
                    "",
                    "&a방어자&7는 검을 들고 우클릭 할 경우",
                    "모든 &c공격자&7의 &d위치&7를 확인할 수 있습니다!",
                    "&8Cooldown: &a10s",
                    "",
                    "&f기본 지급:",
                    "  &f• &e3x &cHealth {hp}",
                    "  &f• &fFull Iron Armor Set &9(Unbreakable)",
                    "  &f• &eBow &9(Infinity, Punch II)",
                    "",
                    "&f유틸리티 아이템:",
                    "  &f• &cFire Force",
                    "  &f• &8Darkness Blast",
                    "  &f• &8Invisibility Cloak"
            ));

            inventory.setItem(21, createItem(Material.GOLD_NUGGET, 1, "&6Coin",
                    "코인은 아이템을 구매하는데 사용할 수 있습니다.",
                    "자신의 인벤토리를 열어 아이템을 구매할 수 있습니다.",
                    "코인은 다음 라운드까지 보존됩니다.",
                    "게임을 시작할 때마다 코인은 초기화 됩니다."
            ));

            inventory.setItem(23, createItem(Material.DISC_FRAGMENT_5, 1, "&5Ability Token",
                    "능력치 토큰은 자신의 능력치를 소폭 상승시켜줍니다.",
                    "또는 특정 확률을 늘려주기도 합니다.",
                    "",
                    "&f강화 가능:",
                    "  &f• &cHealth Boost",
                    "  &f• &aDefense Boost",
                    "  &f• &2Natural Defense Boost",
                    "  &f• &6Coin Boost",
                    "  &f• &6Melee Attack Boost",
                    "  &f• &6Ranged Attack Boost",
                    "  &f• &5Critical Maker",
                    "  &f• &4Critical Master",
                    "  &f• &7Speed Boost"
            ));

            inventory.setItem(40, createItem(Material.GRAY_DYE, 5, "&7준비",
                    "&c위 설명을 모두 읽고 준비하려면 클릭하세요."
            ));

            for (int i = 4; i >= 1; i--) {
                int finalI = i;
                Bukkit.getScheduler().runTaskLater(master, () -> {
                    inventory.setItem(40, createItem(Material.GRAY_DYE, finalI, "&7준비",
                            "&c위 설명을 모두 읽고 준비하려면 클릭하세요."
                    ));
                }, (5 - i) * 20L);
            }

            Bukkit.getScheduler().runTaskLater(master, () -> {
                inventory.setItem(40, createItem(Material.LIME_DYE, 1, "&a준비",
                        "&e위 설명을 모두 읽고 준비하려면 클릭하세요."
                ));
            }, 100);

            Bukkit.getScheduler().runTask(master, () -> {
                Bukkit.getPluginManager().registerEvents(listener, master);
                this.player.openInventory(inventory);
            });
        });
    }

    public class Click implements Listener {
        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getClickedInventory() == null) return;
            Player p = (Player) e.getWhoClicked();
            if (e.getInventory().equals(p.getInventory())) {
                e.setCancelled(true);
                ItemStack item = e.getCurrentItem();
                if(item != null) {
                    if(item.getType() == Material.LIME_DYE) {
                        p.closeInventory();
                    }
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e) {
            if(inventory.equals(e.getInventory())) {
                if(e.getInventory().getItem(40).getType() == Material.LIME_DYE) {
                    GameManager.getInstance().setReady(player);
                    player = null;
                    inventory = null;
                    HandlerList.unregisterAll(this);
                    Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p, Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2));
                } else {
                    Bukkit.getScheduler().runTaskLater(master, () -> e.getPlayer().openInventory(inventory), 1);
                }
            }
        }
    }
}
