package org.mooner.seungwoomaster.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public enum PlayMap {
    END("The Ender", 2, 276, 80),
    NETHER("The Nether", 75, 275, 80),
    ;

    private final String name;
    private final int x;
    private final int y;
    private final int z;

    PlayMap(String name, int x, int y, int z) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld("world"), x + 0.5, y, z + 0.5);
    }
}
