package pl.ynfuien.ydevlib.utils;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YamlHelper {
    /**
     * Returns a map of changed keys with new values. It only checks keys that are both in the old and new config.
     */
    public static HashMap<String, Object> getChangedFields(ConfigurationSection oldConfig, ConfigurationSection newConfig) {
        HashMap<String, Object> changedFields = new HashMap<>();

        Set<String> keys = newConfig.getKeys(false);

        for (String key : keys) {
            if (!oldConfig.contains(key)) continue;

            Object oldValue = oldConfig.get(key);
            Object newValue = newConfig.get(key);

            if (!oldValue.equals(newValue)) changedFields.put(key, newValue);
        }

        return changedFields;
    }

    private final static Pattern rangePattern = Pattern.compile("^(\\d+)(-(\\d+))?$");
    public static Set<Integer> getIntSetFromRangePattern(String entry) {
        Set<Integer> result = new HashSet<>();

        Matcher matcher = rangePattern.matcher(entry);
        if (!matcher.matches()) return result;

        int start = Integer.parseInt(matcher.group(1));
        int end = start;
        String thirdGroup = matcher.group(3);
        if (thirdGroup != null) end = Integer.parseInt(thirdGroup);

        for (int i = start; i <= end; i++) result.add(i);

        return result;
    }
}
