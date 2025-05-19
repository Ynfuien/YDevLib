package pl.ynfuien.ydevlib.utils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.bukkit.Location;
import pl.ynfuien.ydevlib.messages.YLogger;

import java.util.Map;

public class LocationParser {
    private final static Gson gson = new Gson();

    public static Location fromJson(String jsonString) {
        try {
            Map parsed = gson.fromJson(jsonString, Map.class);
            Map<String, Object> map = (Map<String, Object>) parsed;
            return Location.deserialize(map);
        } catch (JsonParseException e) {
            YLogger.warn("Couldn't parse location JSON:\n" + jsonString);
            e.printStackTrace();
            return null;
        }
    }

    public static String toJson(Location location) {
        return gson.toJson(location.serialize());
    }
}
