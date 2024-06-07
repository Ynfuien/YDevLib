package pl.ynfuien.ydevlib.utils;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;

public class CommonPlaceholders {
    private final static DoubleFormatter df = new DoubleFormatter();

    public static void setLocation(HashMap<String, Object> placeholders, Location loc) {
        setLocation(placeholders, loc, null);
    }

    public static void setLocation(HashMap<String, Object> placeholders, Location loc, String prefix) {
        String pfx = prefix == null ? "" : prefix + "-";

        placeholders.put(pfx+"x", df.format(loc.getX()));
        placeholders.put(pfx+"y", df.format(loc.getY()));
        placeholders.put(pfx+"z", df.format(loc.getZ()));
        placeholders.put(pfx+"yaw", df.format(loc.getYaw()));
        placeholders.put(pfx+"pitch", df.format(loc.getPitch()));
        World world = loc.getWorld();
        placeholders.put(pfx+"world", world != null ? world.getName() : "undefined");
    }
}
