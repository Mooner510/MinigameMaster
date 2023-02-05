package org.mooner.seungwoomaster;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashSet;

public class MoonerUtils {
    public static String calcTime(long start, int second) {
        long left = second - ((System.currentTimeMillis() - start) / 1000);
        return zeroValue(left / 60) + ":" + zeroValue(left % 60);
    }

    public static String zeroValue(long value) {
        if(value < 10) return "0" + value;
        else return String.valueOf(value);
    }

    /**
     * Checks if a location is safe (solid ground with 2 breathable blocks)
     *
     * @param location Location to check
     * @return True if location is safe
     */
    public static boolean isSafeLocation(Location location) {
        Block feet = location.getBlock();
        if (!feet.getType().isTransparent() && !feet.getLocation().add(0, 1, 0).getBlock().getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block head = feet.getRelative(BlockFace.UP);
        if (!head.getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block ground = feet.getRelative(BlockFace.DOWN);
        return ground.getType().isSolid(); // not solid
    }

    public static double absFix(double value, double add) {
        if(value < 0) return value - add;
        else return value + add;
    }

    public static Location getNearbySafeLocation(Location loc, double noDistance, double distance) {
        Location location;
        int maxY = loc.getBlockY() + 4;
        HashSet<Integer> x = new HashSet<>();
        HashSet<Integer> z = new HashSet<>();
        double tooDistance = distance - noDistance;
        while (true) {
            location = loc.clone().add(absFix(Math.random() * tooDistance * 2 - tooDistance, noDistance), 0, absFix(Math.random() * tooDistance * 2 - tooDistance, noDistance));
            if (x.contains(location.getBlockX()) && z.contains(location.getBlockY())) continue;
            x.add(location.getBlockX());
            z.add(location.getBlockZ());
            location.setY(location.getBlockY() - 4);
            while (true) {
                if (isSafeLocation(location)) {
                    return location.add(0, 1, 0);
                }
                if (location.getY() >= maxY) {
                    break;
                }
                location.add(0, 1, 0);
            }
        }
    }

    public static String chat(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg)
                .replace("{hp}", "❤")
                .replace("{def}", "❈")
                .replace("{str}", "❁")
                .replace("{sp}", "✦")
                .replace("{speed}", "✦")
                .replace("{cc}", "☣")
                .replace("{cd}", "☠")
                .replace("{bh}", "✿")
                .replace("{int}", "✎")
                .replace("{ad}", "๑")
                .replace("{mana}", "✎")
                .replace("{cb}", "⫽")
                .replace("{mf}", "✯")
                .replace("{as}", "⚔")
                .replace("{ms}", "⸕")
                .replace("{ft}", "☘")
                .replace("{td}", "❂")
                .replace("{sc}", "α");
    }

    public static String commaNumber(int number) {
        return NumberFormat.getInstance().format(number);
    }

    public static String commaNumber(double number) {
        return NumberFormat.getInstance().format(number);
    }

    public static String parseIfInt(double value, boolean comma) {
        if (value >= Integer.MAX_VALUE) {
            return numberTic(value, 3);
        }
        if (comma) {
            if (Math.floor(value) == value) {
                return commaNumber((int) Math.floor(value));
            }
            return commaNumber(value);
        } else {
            if (Math.floor(value) == value) {
                return ((int) Math.floor(value)) + "";
            }
            return BigDecimal.valueOf(value).toPlainString();
        }
    }

    public static String rome(long value) {
        StringBuilder s = new StringBuilder();
        if (value <= 0) return "";
        if (value >= 10000) return value + "";
        long v = value;
        while (v >= 9000) {
            s.append("FMF");
            v -= 9000;
        }
        while (v >= 5000) {
            s.append("F");
            v -= 5000;
        }
        while (v >= 4000) {
            s.append("MF");
            v -= 4000;
        }
        while (v >= 1000) {
            s.append("M");
            v -= 1000;
        }
        while (v >= 900) {
            s.append("CM");
            v -= 900;
        }
        while (v >= 500) {
            s.append("D");
            v -= 500;
        }
        while (v >= 400) {
            s.append("CD");
            v -= 400;
        }
        while (v >= 100) {
            s.append("C");
            v -= 100;
        }
        while (v >= 90) {
            s.append("XC");
            v -= 100;
        }
        while (v >= 50) {
            s.append("L");
            v -= 50;
        }
        while (v >= 40) {
            s.append("XL");
            v -= 40;
        }
        while (v >= 10) {
            s.append("X");
            v -= 10;
        }
        while (v >= 9) {
            s.append("IX");
            v -= 9;
        }
        while (v >= 5) {
            s.append("V");
            v -= 5;
        }
        while (v >= 4) {
            s.append("IV");
            v -= 4;
        }
        while (v >= 1) {
            s.append("I");
            v -= 1;
        }
        return s.toString();
    }

    private static final ImmutableList<String> suffix = ImmutableList.of("", "k", "M", "B", "T", "Q");

    public static String numberTic(double value, int a) {
        if (value < 1) {
            return (int) Math.round(value) + "";
        }
        double amount = Math.floor(Math.floor(Math.log10(value)) / 3);
        if (amount != 0) {
            value = value * Math.pow(0.001, amount);
            return parseString(value, a, true) + suffix.get((int) Math.floor(amount));
        }
        return parseString(value, a, true);
    }

    public static String parseString(double value) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(0, RoundingMode.DOWN);
        return parseIfInt(Double.parseDouble(b.toString()), false);
    }

    public static String parseString(double value, int amount) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(amount, RoundingMode.DOWN);
        return parseIfInt(Double.parseDouble(b.toString()), false);
    }

    public static String parseString(double value, boolean comma) {
        BigDecimal b;
        try {
            b = BigDecimal.valueOf(value);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.broadcastMessage(value + "");
            return value + "";
        }
        b = b.setScale(0, RoundingMode.DOWN);
        return parseIfInt(Double.parseDouble(b.toString()), comma);
    }

    public static String parseString(double value, int amount, boolean comma) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(amount, RoundingMode.DOWN);
        return parseIfInt(Double.parseDouble(b.toString()), comma);
    }
}
