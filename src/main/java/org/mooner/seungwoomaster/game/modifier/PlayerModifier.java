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

    public void addLevel(PlayerAttribute attribute) {
        attributeMap.merge(attribute, 1, Integer::sum);
    }

    public int getLevel(PlayerAttribute attribute) {
        return attributeMap.getOrDefault(attribute, 0);
    }

    public double getValue(PlayerAttribute attribute) {
        double additive = 0;
        if(attribute == PlayerAttribute.DEFENSE) {
            GameManager gameManager = GameManager.getInstance();
            if(gameManager.isAttackPlayer(uuid)) {
                additive += gameManager.getTopArmorTier(uuid).ordinal() * 0.05 + gameManager.getBottomArmorTier(uuid).ordinal() * 0.05;
            } else {
                additive += gameManager.getTopArmorTier(uuid).ordinal() * 0.075 + gameManager.getBottomArmorTier(uuid).ordinal() * 0.075;
            }
        }
        return attributeMap.getOrDefault(attribute, 0) * attribute.getValue() + additive;
    }

    public void refresh() {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) return;

        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if(attribute != null) {
            if(GameManager.getInstance().isAttackPlayer(player)) attribute.setBaseValue(20 + getValue(PlayerAttribute.HEALTH));
            else attribute.setBaseValue((20 + (getValue(PlayerAttribute.HEALTH))) * Bukkit.getOnlinePlayers().size());
            player.setHealth(attribute.getBaseValue());
        }
        player.setArrowsInBody(0);
        player.setFoodLevel(20);
        player.setWalkSpeed((float) (0.2 + 0.2 * getValue(PlayerAttribute.SPEED)));
    }
}
