package org.mooner.seungwoomaster.game.modifier;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.mooner.seungwoomaster.game.GameManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerModifier {
    private final UUID uuid;
    private final Map<PlayerAttribute, Integer> attributeMap;
    private PlayerClass playerClass;
    private final Map<String, Integer> intCounter;
    private final Map<String, Double> doubleCounter;

    public PlayerModifier(UUID uuid) {
        this.uuid = uuid;
        attributeMap = new HashMap<>();
        intCounter = new HashMap<>();
        doubleCounter = new HashMap<>();
    }

    public void addCounter(String key) {
        intCounter.merge(key, 1, Integer::sum);
    }

    public void addCounter(String key, int addition) {
        intCounter.merge(key, addition, Integer::sum);
    }

    public void addCounter(String key, double addition) {
        doubleCounter.merge(key, addition, Double::sum);
    }

    public void resetIntCounter() {
        intCounter.clear();
    }

    public void resetDoubleCounter() {
        doubleCounter.clear();
    }

    public void resetCounter(String key) {
        intCounter.remove(key);
        doubleCounter.remove(key);
    }

    public int getIntCounter(String key) {
        return intCounter.getOrDefault(key, 0);
    }

    public double getDoubleCounter(String key) {
        return doubleCounter.getOrDefault(key, 0d);
    }

    public void setPlayerClass(PlayerClass playerClass) {
        this.playerClass = playerClass;
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public void addLevel(PlayerAttribute attribute) {
        attributeMap.merge(attribute, 1, Integer::sum);
    }

    public int resetLevel(PlayerAttribute attribute) {
        Integer value = attributeMap.remove(attribute);
        return value == null ? 0 : value;
    }

    public int getLevel(PlayerAttribute attribute) {
        return attributeMap.getOrDefault(attribute, 0);
    }

    public double getValue(PlayerAttribute attribute) {
        double additive = 0;
        if (attribute == PlayerAttribute.DEFENSE) {
            GameManager gameManager = GameManager.getInstance();
            additive += gameManager.getBottomArmorTier(uuid).ordinal() * 0.05 + gameManager.getTopArmorTier(uuid).ordinal() * (gameManager.isAttackPlayer(uuid) ? 0.025 : 0.05);
            return Math.min(0.99, attributeMap.getOrDefault(attribute, 0) * attribute.getValue() + additive);
        }
        return attributeMap.getOrDefault(attribute, 0) * attribute.getValue() + additive;
    }

    public void refresh() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            if (GameManager.getInstance().isAttackPlayer(player))
                attribute.setBaseValue(20 + getValue(PlayerAttribute.HEALTH));
            else attribute.setBaseValue((20 + (getValue(PlayerAttribute.HEALTH))) * Bukkit.getOnlinePlayers().size());
            player.setHealth(attribute.getBaseValue());
        }
        player.setArrowsInBody(0);
        player.setFoodLevel(20);
        player.setWalkSpeed((float) (0.2 + 0.2 * getValue(PlayerAttribute.SPEED)));
    }
}
