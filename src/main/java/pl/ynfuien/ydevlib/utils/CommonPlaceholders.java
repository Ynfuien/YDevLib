package pl.ynfuien.ydevlib.utils;

import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
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

    public static void setDateTime(HashMap<String, Object> placeholders, long timestamp, String prefix) {
        String pfx = prefix == null ? "" : prefix + "-";

        LocalDateTime date = new Timestamp(timestamp).toLocalDateTime();
        // Date
        placeholders.put(pfx+"year", date.getYear());
        placeholders.put(pfx+"month", String.format("%02d", date.getMonthValue()));
        placeholders.put(pfx+"day", String.format("%02d", date.getDayOfMonth()));
        // Time
        placeholders.put(pfx+"hour", String.format("%02d", date.getHour()));
        placeholders.put(pfx+"minute", String.format("%02d", date.getMinute()));
        placeholders.put(pfx+"second", String.format("%02d", date.getSecond()));
    }

    public static void setDuration(HashMap<String, Object> placeholders, long time, String prefix) {
        String pfx = prefix == null ? "" : prefix + "-";

        Duration duration = Duration.ofMillis(time);
        placeholders.put(pfx+"days", duration.toDaysPart());
        placeholders.put(pfx+"hours", duration.toHoursPart());
        placeholders.put(pfx+"minutes", duration.toMinutesPart());
        placeholders.put(pfx+"seconds", duration.toSecondsPart());
    }
}
