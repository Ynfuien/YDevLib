package pl.ynfuien.ydevlib.config;

public enum ConfigName {
    LANG,
    CONFIG;

    String getFileName() {
        return name().toLowerCase().replace('_', '-') + ".yml";
    }
}
