package org.mooner.seungwoomaster.game;

public enum Values {
    WIN_COIN(3500),
    LOSE_COIN(2500),

    STONE(100),
    IRON(400),
    GOLD(700),
    DIAMOND(1200),
    NETHERITE(2000),

    LEATHER_ARMOR(250),
    IRON_ARMOR(550),
    DIAMOND_ARMOR(1200),
    NETHERITE_ARMOR(2500),

    PISTON(100, true, 30),
    GLOWER(600, true, 60),
    FIRE_FORCE(250, true, 10),
    DARKNESS_BLAST(350, true, 10),
    ENDER_PEARL(100, true, 0),
    GAPPLE(125, true, 0),
    ;

    private final int money;
    private final boolean ignoreMulti;
    private final int cooltime;

    Values(int money) {
        this.money = money;
        ignoreMulti = false;
        cooltime = 0;
    }

    Values(int money, boolean ignoreMulti, int cooltime) {
        this.money = money;
        this.ignoreMulti = ignoreMulti;
        this.cooltime = cooltime;
    }

    public int getMoney() {
        return money;
    }

    public int getCooltime() {
        return cooltime;
    }

    public int getMoney(double multi) {
        if(ignoreMulti) return money;
        return (int) Math.floor(money * multi);
    }

    public String toString(double multi) {
        if(ignoreMulti) return "Cost: &6"+money+" Coin";
        return "Cost: &6"+(int) Math.floor(money * multi)+" Coin";
    }

    @Override
    public String toString() {
        return "Cost: &6"+money+" Coin";
    }
}
