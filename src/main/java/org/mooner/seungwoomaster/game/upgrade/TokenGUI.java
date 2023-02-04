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

import static org.mooner.seungwoomaster.MoonerUtils.chat;
import static org.mooner.seungwoomaster.MoonerUtils.parseString;
import static org.mooner.seungwoomaster.SeungWooMaster.master;
import static org.mooner.seungwoomaster.game.gui.GUIUtils.createItem;

public class TokenGUI {
    private Inventory inventory;
    private Player player;
    private final Click listener = new Click();
    private final ItemStack glass = createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " ");

    public int getReq(int level) {
        return (level + 1) * (level + 2) / 2;
    }

    public TokenGUI(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(master, () -> {
            this.player = player;

            inventory = Bukkit.createInventory(player, 36, "토큰 강화");
            GameManager gameManager = GameManager.getInstance();
            PlayerModifier modifier = gameManager.getModifier(player);

            for (int i = 0; i < 36; i++) {
                if(i >= 9 && i < 27 && (i % 9 == 0 || i % 9 == 8)) continue;
                inventory.setItem(i, glass);
            }

            update(modifier);

            Bukkit.getScheduler().runTask(master, () -> {
                Bukkit.getPluginManager().registerEvents(listener, master);
                this.player.openInventory(inventory);
            });
        });
    }

    public void update(PlayerModifier modifier) {
        inventory.setItem(11, createItem(Material.GOLDEN_APPLE, Math.max(modifier.getLevel(PlayerAttribute.HEALTH), 1), "&cHealth Boost", "&7체력이 레벨당 &c"+PlayerAttribute.HEALTH.getValue()+"&7 증가합니다.", "", "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.HEALTH)) + " Token"));

        inventory.setItem(12, createItem(Material.IRON_CHESTPLATE, Math.max(modifier.getLevel(PlayerAttribute.DEFENSE), 1), "&cDefense Boost", "&7플레이어로부터 받는 피해가 레벨당 &a"+parseString(PlayerAttribute.DEFENSE.getValue() * 100)+"%&7 감소합니다.", "", "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.DEFENSE)) + " Token"));

        inventory.setItem(13, createItem(Material.SHIELD, Math.max(modifier.getLevel(PlayerAttribute.DODGE), 1), "&cDodge", "&7레벨당 &a"+parseString(PlayerAttribute.DODGE.getValue() * 100)+"%&7 확률로 공격을 회피합니다.", "", "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.DODGE)) + " Token"));

        inventory.setItem(14, createItem(Material.OAK_SAPLING, Math.max(modifier.getLevel(PlayerAttribute.NATURAL_DEFENSE), 1), "&cNatural Defense Boost", "&7자연적으로 받는 피해가 레벨당 &a"+parseString(PlayerAttribute.NATURAL_DEFENSE.getValue() * 100)+"%&7 감소합니다.", "", "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.NATURAL_DEFENSE)) + " Token"));

        inventory.setItem(15, createItem(Material.EMERALD, Math.max(modifier.getLevel(PlayerAttribute.COIN_BOOST), 1), "&6Coin Boost", "&7코인 획득량이 레벨당 &a"+parseString(PlayerAttribute.COIN_BOOST.getValue() * 100)+"%&7 증가합니다.", "", "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.COIN_BOOST)) + " Token"));

        inventory.setItem(20, createItem(Material.IRON_SWORD, Math.max(modifier.getLevel(PlayerAttribute.MELEE_ATTACK), 1), "&6Melee Attack Boost", "&7근접 공격력이 레벨당 &a"+parseString(PlayerAttribute.MELEE_ATTACK.getValue() * 100)+"%&7 증가합니다.", "", "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.MELEE_ATTACK)) + " Token"));

        inventory.setItem(21, createItem(Material.BOW, Math.max(modifier.getLevel(PlayerAttribute.RANGED_ATTACK), 1), "&6Ranged Attack Boost", "&7원거리 공격력이 레벨당 &a"+parseString(PlayerAttribute.RANGED_ATTACK.getValue() * 100)+"%&7 증가합니다.", "", "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.RANGED_ATTACK)) + " Token"));

        inventory.setItem(22, createItem(Material.NETHERITE_AXE, Math.max(modifier.getLevel(PlayerAttribute.CRITICAL_CHANCE), 1), "&6Critical Maker", "&7치명타를 발생할 확률이 레벨당 &a"+parseString(PlayerAttribute.CRITICAL_CHANCE.getValue() * 100)+"%&7 증가합니다.", "", "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.CRITICAL_CHANCE)) + " Token"));

        inventory.setItem(23, createItem(Material.BLAZE_POWDER, Math.max(modifier.getLevel(PlayerAttribute.CRITICAL_DAMAGE), 1), "&6Critical Master", "&7치명타시 공격력이 레벨당 &a"+parseString(PlayerAttribute.CRITICAL_DAMAGE.getValue() * 100)+"%&7 증가합니다.", "", "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.CRITICAL_DAMAGE)) + " Token"));

        inventory.setItem(24, createItem(Material.IRON_BOOTS, Math.max(modifier.getLevel(PlayerAttribute.SPEED), 1), "&6Speed Boost", "&7이동속도가 레벨당 &a"+parseString(PlayerAttribute.SPEED.getValue() * 100)+"%&7 증가합니다.", "", "&7Cost: &5" + getReq(modifier.getLevel(PlayerAttribute.SPEED)) + " Token"));
    }

    public class Click implements Listener {
        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if(e.getInventory().equals(inventory)) {
                ItemStack item = e.getCurrentItem();
                if (item == null || e.getClickedInventory() == null || item.getType().equals(Material.AIR)) return;
                if(e.getClickedInventory().equals(inventory)) {
                    e.setCancelled(true);
                    GameManager gameManager = GameManager.getInstance();
                    PlayerModifier modifier = gameManager.getModifier(player);
                    int level;
                    switch (e.getSlot()) {
                        case 11 -> {
                            level = modifier.getLevel(PlayerAttribute.HEALTH);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.HEALTH);
                            }
                        }
                        case 12 -> {
                            level = modifier.getLevel(PlayerAttribute.DEFENSE);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.DEFENSE);
                            }
                        }
                        case 13 -> {
                            level = modifier.getLevel(PlayerAttribute.DODGE);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.DODGE);
                            }
                        }
                        case 14 -> {
                            level = modifier.getLevel(PlayerAttribute.NATURAL_DEFENSE);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.NATURAL_DEFENSE);
                            }
                        }
                        case 15 -> {
                            level = modifier.getLevel(PlayerAttribute.COIN_BOOST);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.COIN_BOOST);
                            }
                        }
                        case 20 -> {
                            level = modifier.getLevel(PlayerAttribute.MELEE_ATTACK);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.MELEE_ATTACK);
                            }
                        }
                        case 21 -> {
                            level = modifier.getLevel(PlayerAttribute.RANGED_ATTACK);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.RANGED_ATTACK);
                            }
                        }
                        case 22 -> {
                            level = modifier.getLevel(PlayerAttribute.CRITICAL_CHANCE);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.CRITICAL_CHANCE);
                            }
                        }
                        case 23 -> {
                            level = modifier.getLevel(PlayerAttribute.CRITICAL_DAMAGE);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.CRITICAL_DAMAGE);
                            }
                        }
                        case 24 -> {
                            level = modifier.getLevel(PlayerAttribute.SPEED);
                            if (gameManager.removeToken(player, getReq(level))) {
                                modifier.addLevel(PlayerAttribute.SPEED);
                            }
                        }
                        default -> {
                            return;
                        }
                    }
                    player.sendMessage(chat(item.getItemMeta().getDisplayName()+"&a을(를) &b"+(level + 1)+" 레벨&a로 강화했습니다."));
                    player.playSound(player, Sound.BLOCK_ANVIL_USE, 0.8f, 2f);
                    player.closeInventory();
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(inventory.equals(e.getInventory())) {
                HandlerList.unregisterAll(this);
                if(GameManager.getInstance().getToken(player) > 0) {
                    new TokenGUI(player);
                } else {
                    player.sendMessage(chat("&e토큰을 모두 사용하였습니다!"));
                    Bukkit.broadcastMessage(chat("&6" + player.getName() + "&a Ready!"));
                }
                player = null;
                inventory = null;
            }
        }
    }
}
