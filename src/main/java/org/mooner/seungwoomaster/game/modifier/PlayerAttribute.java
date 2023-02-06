package org.mooner.seungwoomaster.game.modifier;

public enum PlayerAttribute {
    HEALTH(4),
    DEFENSE(0.035),
    NATURAL_DEFENSE(0.12),
    COIN_BOOST(0.1),
    MELEE_ATTACK(0.01),
    RANGED_ATTACK(0.03),
    DODGE(0.02),
    COMBO(0.01),
    CRITICAL_CHANCE(0.01),
    CRITICAL_DAMAGE(0.03),
    SPEED(0.035);

    private final double value;

    PlayerAttribute(double v) {
        value = v;
    }

    public double getValue() {
        return value;
    }
}
