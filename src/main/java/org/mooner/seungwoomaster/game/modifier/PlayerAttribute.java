package org.mooner.seungwoomaster.game.modifier;

public enum PlayerAttribute {
    HEALTH(2),
    DEFENSE(0.02),
    NATURAL_DEFENSE(0.1),
    COIN_BOOST(0.12),
    MELEE_ATTACK(0.04),
    RANGED_ATTACK(0.03),
    DODGE(0.015),
    COMBO(0.01),
    CRITICAL_CHANCE(0.02),
    CRITICAL_DAMAGE(0.10),
    SPEED(0.015);

    private final double value;

    PlayerAttribute(double v) {
        value = v;
    }

    public double getValue() {
        return value;
    }
}
