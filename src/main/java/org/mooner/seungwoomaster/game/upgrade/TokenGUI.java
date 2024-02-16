package org.mooner.seungwoomaster.game.upgrade;

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
import org.mooner.seungwoomaster.game.modifier.PlayerAttribute;
import org.mooner.seungwoomaster.game.modifier.PlayerModifier;

import java.util.Arrays;

import static org.mooner.seungwoomaster.MoonerUtils.*;
import static org.mooner.seungwoomaster.SeungWooMaster.master;
import static org.mooner.seungwoomaster.game.gui.GUIUtils.createItem;
import static org.mooner.seungwoomaster.game.gui.GUIUtils.ench;

public class TokenGUI {
    private Inventory inventory;
    private Player player;
    private final Click listener = new Click();
    private final ItemStack glass = createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " ");

    public int getReq(int level) {
        return level + 1;
//        return (level + 1) * (level + 2) / 2;
    }

    public TokenGUI(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(master, () -> {
            this.player = player;

            inventory = Bukkit.createInventory(player, 54, "토큰 강화");
            GameManager gameManager = GameManager.getInstance();
            PlayerModifier modifier = gameManager.getModifier(player);

            for (int i = 0; i < 54; i++) inventory.setItem(i, glass);

            update(modifier);

            inventory.setItem(40, createItem(Material.GRAY_DYE, 10, "&7준비",
                    "&c준비하려면 클릭하세요."
            ));

            inventory.setItem(41, createItem(Material.CHEST, 1, "&c토큰 초기화",
                    "&7강화한 토큰 레벨을 모두 초기화합니다."
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
        int token = GameManager.getInstance().getToken(player);
        if (token > 0) {
            inventory.setItem(4, createItem(Material.DISC_FRAGMENT_5, token, "&5Ability Token: x" + token));
        } else {
            inventory.setItem(4, createItem(Material.STRUCTURE_VOID, token, "&5Ability Token: x" + token));
        }

        inventory.setItem(11, createItem(Material.GOLDEN_APPLE, Math.max(modifier.getLevel(PlayerAttribute.HEALTH), 1),
                "&cHealth Boost " + rome(modifier.getLevel(PlayerAttribute.HEALTH)),
                "&7체력이 레벨당 &c" + parseString(PlayerAttribute.HEALTH.getValue()) + "&7 증가합니다.",
                "",
                "&7체력 증가: +&c" + parseString(modifier.getValue(PlayerAttribute.HEALTH) * 100, 1) + " {hp}",
                "",
                "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.HEALTH)) + " Token"));

        inventory.setItem(12, createItem(Material.IRON_CHESTPLATE, Math.max(modifier.getLevel(PlayerAttribute.DEFENSE), 1),
                "&aDefense Boost " + rome(modifier.getLevel(PlayerAttribute.DEFENSE)),
                "&7방어력이 레벨당 &a" + parseString(PlayerAttribute.DEFENSE.getValue() * 100, 1) + "%&7 증가합니다.",
                "&7레벨당 &4Critical Master&7를 &a0.4&7레벨 억제합니다.",
                "",
                "&7방어력 증가: +&a" + parseString(modifier.getValue(PlayerAttribute.DEFENSE) * 100, 1) + "%",
                "&4Critical Master&& 억제: &a" + parseString(modifier.getValue(PlayerAttribute.DEFENSE) * 0.4, 1) + " 레벨",
                "",
                "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.DEFENSE)) + " Token"));

        inventory.setItem(13, createItem(Material.SHIELD, Math.max(modifier.getLevel(PlayerAttribute.DODGE), 1),
                "&9Dodge " + rome(modifier.getLevel(PlayerAttribute.DODGE)),
                "&7레벨당 &a" + parseString(PlayerAttribute.DODGE.getValue() * 100, 2) + "%&7 확률로 공격을 회피합니다.",
                "",
                "&7현재 회피 확률: &9" + parseString(modifier.getValue(PlayerAttribute.DODGE) * 100, 1) + "%",
                "",
                "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.DODGE)) + " Token"));

        int naturalDefense = modifier.getLevel(PlayerAttribute.NATURAL_DEFENSE);
        if (naturalDefense >= 8) {
            inventory.setItem(14, ench(createItem(Material.OAK_SAPLING, naturalDefense,
                    "&2Natural Defense Boost " + rome(naturalDefense),
                    "&7자연적으로 받는 피해가 레벨당 &a" + parseString(PlayerAttribute.NATURAL_DEFENSE.getValue() * 100, 1) + "%&7 감소합니다.",
                    "",
                    "&7자연적 피해 배율: &e" + parseString(100 - modifier.getValue(PlayerAttribute.NATURAL_DEFENSE) * 100, 1) + "%",
                    "",
                    "&c&lMAX LEVEL")));
        } else {
            inventory.setItem(14, createItem(Material.OAK_SAPLING, Math.max(naturalDefense, 1),
                    "&2Natural Defense Boost " + rome(naturalDefense),
                    "&7자연적으로 받는 피해가 레벨당 &a" + parseString(PlayerAttribute.NATURAL_DEFENSE.getValue() * 100, 1) + "%&7 감소합니다.",
                    "",
                    "&7피해 감소 배율: &a" + parseString(100 - modifier.getValue(PlayerAttribute.NATURAL_DEFENSE) * 100, 1) + "%",
                    "",
                    "&7Cost: &5" + getReq(naturalDefense) + " Token"));
        }

        inventory.setItem(15, createItem(Material.EMERALD, Math.max(modifier.getLevel(PlayerAttribute.COIN_BOOST), 1),
                "&6Coin Boost " + rome(modifier.getLevel(PlayerAttribute.COIN_BOOST)),
                "&7코인 획득량이 레벨당 &a" + parseString(PlayerAttribute.COIN_BOOST.getValue() * 100, 1) + "%&7 증가합니다.",
                "",
                "&7코인 획득 배율: &c" + parseString(100 + modifier.getValue(PlayerAttribute.COIN_BOOST) * 100, 1) + "%",
                "",
                "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.COIN_BOOST)) + " Token"));

        inventory.setItem(20, createItem(Material.IRON_SWORD, Math.max(modifier.getLevel(PlayerAttribute.MELEE_ATTACK), 1),
                "&cMelee Attack Boost " + rome(modifier.getLevel(PlayerAttribute.MELEE_ATTACK)),
                "&7가하는 근접 피해가 레벨당 &c" + parseString(PlayerAttribute.MELEE_ATTACK.getValue() * 100, 1) + "%&7 증가합니다.",
                "",
                "&7근접 피해 배율: &c" + parseString(100 + modifier.getValue(PlayerAttribute.MELEE_ATTACK) * 100, 1) + "%",
                "",
                "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.MELEE_ATTACK)) + " Token"));

        inventory.setItem(21, createItem(Material.IRON_BOOTS, Math.max(modifier.getLevel(PlayerAttribute.SPEED), 1),
                "&fSpeed Boost " + rome(modifier.getLevel(PlayerAttribute.SPEED)),
                "&7이동속도가 레벨당 &a" + parseString(PlayerAttribute.SPEED.getValue() * 100, 1) + "%&7 증가합니다.",
                "",
                "&7이동 속도: &a" + parseString(100 + modifier.getValue(PlayerAttribute.SPEED) * 100, 1),
                "",
                "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.SPEED)) + " Token"));

        inventory.setItem(22, ench(createItem(Material.ENDER_EYE, Math.max(modifier.getLevel(PlayerAttribute.SWAP), 1),
                "&9Swap " + rome(modifier.getLevel(PlayerAttribute.SWAP)),
                "상대방의 공격으로 자신에게 &5크리티컬&7이 발동되었을 때,",
                "레벨당 " + parseString(PlayerAttribute.SWAP.getValue() * 100, 1) + " 확률로 &9핫바 슬롯의 아이템을 무작위로 배치&7시킵니다.",
                "",
                "&7핫바 스왑 확률: &9" + parseString(modifier.getValue(PlayerAttribute.SWAP) * 100, 1) + "%",
                "",
                "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.SWAP)) * 2 + " Token")));

//        inventory.setItem(21, createItem(Material.BOW, Math.max(modifier.getLevel(PlayerAttribute.RANGED_ATTACK), 1),
//                "&cRanged Attack Boost " + rome(modifier.getLevel(PlayerAttribute.RANGED_ATTACK)),
//                "&7원거리 공격력이 레벨당 &a" + parseString(PlayerAttribute.RANGED_ATTACK.getValue() * 100, 1) + "%&7 증가합니다.",
//                "",
//                "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.RANGED_ATTACK)) + " Token"));

        inventory.setItem(23, createItem(Material.NETHERITE_AXE, Math.max(modifier.getLevel(PlayerAttribute.CRITICAL_CHANCE), 1),
                "&5Critical Maker " + rome(modifier.getLevel(PlayerAttribute.CRITICAL_CHANCE)),
                "&7치명타를 발생할 확률이 레벨당 &a" + parseString(PlayerAttribute.CRITICAL_CHANCE.getValue() * 100, 1) + "%&7 증가합니다.",
                "",
                "&7치명타 확률: &c" + parseString(PlayerAttribute.CRITICAL_CHANCE.getValue() * 100 + 5, 1) + "%",
                "",
                "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.CRITICAL_CHANCE)) + " Token"));

        inventory.setItem(24, createItem(Material.BLAZE_POWDER, Math.max(modifier.getLevel(PlayerAttribute.CRITICAL_DAMAGE), 1),
                "&4Critical Master " + rome(modifier.getLevel(PlayerAttribute.CRITICAL_DAMAGE)),
                "&7치명타시 공격력이 레벨당 &a" + parseString(PlayerAttribute.CRITICAL_DAMAGE.getValue() * 100, 1) + "%&7 증가합니다.",
                "",
                "&7치명타 피해: &c" + parseString(PlayerAttribute.CRITICAL_DAMAGE.getValue() * 100 + 10, 1) + "%",
                "",
                "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.CRITICAL_DAMAGE)) + " Token"));
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
                    int level;
                    switch (e.getSlot()) {
                        case 11 -> {
                            level = modifier.getLevel(PlayerAttribute.HEALTH);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.HEALTH);
                                modifier.refresh();
                            } else return;
                        }
                        case 12 -> {
                            level = modifier.getLevel(PlayerAttribute.DEFENSE);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.DEFENSE);
                            } else return;
                        }
                        case 13 -> {
                            level = modifier.getLevel(PlayerAttribute.DODGE);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.DODGE);
                            } else return;
                        }
                        case 14 -> {
                            level = modifier.getLevel(PlayerAttribute.NATURAL_DEFENSE);
                            if (level >= 8) return;
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.NATURAL_DEFENSE);
                            } else return;
                        }
                        case 15 -> {
                            level = modifier.getLevel(PlayerAttribute.COIN_BOOST);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.COIN_BOOST);
                            } else return;
                        }
                        case 20 -> {
                            level = modifier.getLevel(PlayerAttribute.MELEE_ATTACK);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.MELEE_ATTACK);
                            } else return;
                        }
//                        case 21 -> {
//                            level = modifier.getLevel(PlayerAttribute.RANGED_ATTACK);
//                            if (gameManager.removeToken(player, getReq(level))) {
//                                modifier.addLevel(PlayerAttribute.RANGED_ATTACK);
//                            } else return;
//                        }
                        case 21 -> {
                            level = modifier.getLevel(PlayerAttribute.SPEED);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.SPEED);
                            } else return;
                        }
                        case 22 -> {
                            level = modifier.getLevel(PlayerAttribute.SWAP);
                            if (gameManager.removeToken(player, getReq(level) * 2)) {
                                modifier.addLevel(PlayerAttribute.SWAP);
                            } else return;
                        }
                        case 23 -> {
                            level = modifier.getLevel(PlayerAttribute.CRITICAL_CHANCE);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.CRITICAL_CHANCE);
                            } else return;
                        }
                        case 24 -> {
                            level = modifier.getLevel(PlayerAttribute.CRITICAL_DAMAGE);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.CRITICAL_DAMAGE);
                            } else return;
                        }
                        case 41 -> {
                            int token = 0;
                            for (PlayerAttribute attribute : PlayerAttribute.values()) {
                                int levels = modifier.resetLevel(attribute);
                                token += (levels * (levels + 1)) / (attribute == PlayerAttribute.SWAP ? 1 : 2);
                            }
                            gameManager.addToken(player, token);
                            player.playSound(player, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1.5f);
                            update(modifier);
                            return;
                        }
                        default -> {
                            return;
                        }
                    }
                    String displayName = item.getItemMeta().getDisplayName();
                    if (level > 0) {
                        String[] s = displayName.split(" ");
                        displayName = String.join(" ", Arrays.copyOf(s, s.length - 1));
                    }
                    player.sendMessage(chat(displayName + "&a을(를) &b" + (level + 1) + "레벨&a로 강화했습니다."));
                    player.playSound(player, Sound.BLOCK_ANVIL_USE, 0.8f, 2f);
                    update(modifier);
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
