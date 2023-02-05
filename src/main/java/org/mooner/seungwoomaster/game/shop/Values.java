package org.mooner.seungwoomaster.game.shop;

public enum Values {
    WIN_COIN(3500),
    LOSE_COIN(2500),

    STONE(500),
    IRON(1100),
    GOLD(2400),
    DIAMOND(5000),
    NETHERITE(9000),

    LEATHER_ARMOR(150),
    IRON_ARMOR(400),
    DIAMOND_ARMOR(1000),
    NETHERITE_ARMOR(2000),

    PISTON(800, true, 20),
    GLOWER(1500, true, 60),
    FIRE_FORCE(450, true, 15),
    DARKNESS_BLAST(300, true, 15),
    ENDER_PEARL(120, true, 0),
    GAPPLE(350, true, 0),
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
