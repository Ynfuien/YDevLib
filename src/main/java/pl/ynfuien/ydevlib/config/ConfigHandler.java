package pl.ynfuien.ydevlib.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import pl.ynfuien.ydevlib.messages.YLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class ConfigHandler {
    private final Plugin plugin;
    private final HashMap<ConfigName, ConfigObject> configs = new HashMap<>();

    public ConfigHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean load(ConfigName name) {
        return load(name, true);
    }

    public boolean load(ConfigName name, boolean updating) {
        return load(name, updating, false);
    }

    public boolean load(ConfigName name, boolean updating, boolean useDefault) {
        return load(name, updating, useDefault, new ArrayList<>());
    }

    public boolean load(ConfigName name, boolean updating, boolean useDefault, List<String> ignoredKeys) {
        ConfigObject config = new ConfigObject(plugin, name);
        config.setUpdating(updating);
        config.setUseDefault(useDefault);
        config.setIgnoredKeys(ignoredKeys);

        if (config.load() == null) {
            logError("Fix the error and restart server.");
            getServer().getPluginManager().disablePlugin(plugin);
            return false;
        }

        configs.put(name, config);
        return true;
    }

    public void saveAll() {
        for (ConfigObject config : configs.values()) {
            config.save();
        }
    }

    public boolean reloadAll() {
        boolean noProblems = true;

        for (ConfigObject configObject : configs.values()) {
            if (!configObject.reload()) noProblems = false;
        }

        return noProblems;
    }

    public ConfigObject getConfigObject(ConfigName name) {
        return configs.get(name);
    }


    public FileConfiguration getConfig(ConfigName name) {
        ConfigObject config = configs.get(name);
        if (config == null) return null;

        return config.getConfig();
    }


    //// Logging methods
    private void logError(String message) {
        YLogger.warn("[Configs] " + message);
    }

    private void logInfo(String message) {
        YLogger.info("[Configs] " + message);
    }
}
