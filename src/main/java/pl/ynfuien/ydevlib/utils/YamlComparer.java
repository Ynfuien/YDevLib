package pl.ynfuien.ydevlib.utils;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Set;

public class YamlComparer {
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
}
