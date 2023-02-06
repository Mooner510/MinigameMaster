package org.mooner.seungwoomaster.game.total;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.mooner.seungwoomaster.game.GameManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mooner.seungwoomaster.MoonerUtils.chat;
import static org.mooner.seungwoomaster.MoonerUtils.parseString;

public class Total {
    private final Map<UUID, Double> damageMap;
    private final Map<UUID, Double> gainDamageMap;
    private final Map<UUID, Double> defencedMap;
    private final Map<UUID, Double> criticalMap;
    private final Map<UUID, Integer> dodgeMap;

    public Total() {
        damageMap = new HashMap<>();
        gainDamageMap = new HashMap<>();
        defencedMap = new HashMap<>();
        criticalMap = new HashMap<>();
        dodgeMap = new HashMap<>();
    }

    public void addDamage(Player player, double value) {
        damageMap.merge(player.getUniqueId(), value, Double::sum);
    }

    public void addGainDamage(Player player, double value) {
        gainDamageMap.merge(player.getUniqueId(), value, Double::sum);
    }

    public void addDefenced(Player player, double value) {
        defencedMap.merge(player.getUniqueId(), value, Double::sum);
    }

    public void addCritical(Player player, double value) {
        criticalMap.merge(player.getUniqueId(), value, Double::sum);
    }

    public void addDodge(Player player) {
        dodgeMap.merge(player.getUniqueId(), 1, Integer::sum);
    }

    private String getRank(List<UUID> list, Player player) {
        int i = list.indexOf(player.getUniqueId());
        if (i == -1) return " &7(순위 없음)";
        else return " &7(" + (list.indexOf(player.getUniqueId()) + 1) + "위)";
    }

    public void send() {
        List<UUID> damageList = damageMap.entrySet().stream()
                .sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()))
                .map(Map.Entry::getKey)
                .toList();
        List<UUID> gainDamageList = gainDamageMap.entrySet().stream()
                .sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()))
                .map(Map.Entry::getKey)
                .toList();
        List<UUID> defencedList = defencedMap.entrySet().stream()
                .sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()))
                .map(Map.Entry::getKey)
                .toList();
        List<UUID> criticalList = criticalMap.entrySet().stream()
                .sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()))
                .map(Map.Entry::getKey)
                .toList();
        List<UUID> dodgeList = dodgeMap.entrySet().stream()
                .sorted((o1, o2) -> Double.compare(o2.getValue(), o1.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
                player.setGameMode(GameMode.ADVENTURE);
                player.playSound(player, Sound.UI_TOAST_IN, 1, 1);
                player.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                GameManager.getInstance().fireWorkSound(player);
            }
            player.sendTitle(chat("&aGAME END"), "", 20, 60, 100);
            player.sendMessage(
                    chat("&a■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■"),
                    "   ",
                    chat("                           &a&lGAME END"),
                    "    ",
                    chat("   &f&l가한 피해량: &r&c" + parseString(damageMap.getOrDefault(player.getUniqueId(), 0d), 1) + getRank(damageList, player)),
                    chat("   &f&l받은 피해량: &r&c" + parseString(gainDamageMap.getOrDefault(player.getUniqueId(), 0d), 1) + getRank(gainDamageList, player)),
                    chat("   &f&l흡수한 피해량: &r&c" + parseString(defencedMap.getOrDefault(player.getUniqueId(), 0d), 1) + getRank(defencedList, player)),
                    chat("   &f&l크리티컬 피해량: &r&c" + parseString(criticalMap.getOrDefault(player.getUniqueId(), 0d), 1) + getRank(criticalList, player)),
                    chat("   &f&l회피: &r&c" + parseString(dodgeMap.getOrDefault(player.getUniqueId(), 0), 1) + getRank(dodgeList, player)),
                    "      ",
                    chat("&a■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■")
            );
        }
    }
}
