package pl.ynfuien.ydevlib.utils;

import org.bukkit.Location;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pl.ynfuien.ydevlib.messages.YLogger;

import java.util.Map;

public class LocationParser {
    public static Location fromJson(String jsonString) {
        try {
            Object parsed = new JSONParser().parse(jsonString);
            if (!(parsed instanceof Map)) return null;

            Map<String, Object> map = (Map<String, Object>) parsed;
            return Location.deserialize(map);
        } catch (ParseException e) {
            YLogger.warn("Couldn't parse location JSON:\n" + jsonString);
            e.printStackTrace();
            return null;
        }
    }

    public static String toJson(Location location) {
        return JSONObject.toJSONString(location.serialize());
    }
}
