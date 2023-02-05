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

    public PlayerModifier(UUID uuid) {
        this.uuid = uuid;
        attributeMap = new HashMap<>();
    }

    public void updateHealth() {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) return;
        if(GameManager.getInstance().isAttackPlayer(player))
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20 + getValue(PlayerAttribute.HEALTH));
        else
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue((20 + getValue(PlayerAttribute.HEALTH)) * 3);
    }

    public void addLevel(PlayerAttribute attribute) {
        attributeMap.merge(attribute, 1, Integer::sum);
    }

    public int getLevel(PlayerAttribute attribute) {
        return attributeMap.getOrDefault(attribute, 0);
    }

    public double getValue(PlayerAttribute attribute) {
        return attributeMap.getOrDefault(attribute, 0) * attribute.getValue();
    }

    public void refresh() {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) return;

        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if(attribute != null) attribute.setBaseValue(20 + PlayerAttribute.HEALTH.getValue() * getLevel(PlayerAttribute.HEALTH));

        player.setWalkSpeed((float) (0.2 + 0.2 * getValue(PlayerAttribute.SPEED)));
    }
}
