package org.mooner.seungwoomaster.game.modifier;

public enum PlayerAttribute {
    HEALTH(2),
    DEFENSE(0.05),
    NATURAL_DEFENSE(0.8),
    COIN_BOOST(0.1),
    MELEE_ATTACK(0.035),
    RANGED_ATTACK(0.025),
    DODGE(0.006),
    COMBO(0.005),
    CRITICAL_CHANCE(0.02),
    CRITICAL_DAMAGE(0.1),
    SPEED(0.04);

    private final double value;

    PlayerAttribute(double v) {
        value = v;
    }

    public double getValue() {
        return value;
    }
}
