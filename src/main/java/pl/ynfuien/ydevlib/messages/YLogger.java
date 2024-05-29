package pl.ynfuien.ydevlib.messages;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.util.HashMap;

public class YLogger {
    private static final ConsoleCommandSender console = Bukkit.getConsoleSender();
    private static ComponentLogger componentLogger;
    private static String prefix;
    private static boolean debugging = false;

    /**
     * Sets up logger with provided prefix and ComponentLogger.
     */
    public static void setup(String prefix, ComponentLogger componentLogger) {
        YLogger.prefix = prefix;
        YLogger.componentLogger = componentLogger;
    }

    /**
     * Sets whether debug messages should be logged.
     */
    public static void setDebugging(boolean enabled) {
        debugging = enabled;
    }

    /**
     * Logs message at the INFO level.
     */
    public static void info(String message) {
        Messenger.send(console, prefix + message);
    }

    public static void info(String message, HashMap<String, Object> placeholders) {
        Messenger.send(console, prefix + message, placeholders);
    }

    /**
     * Logs message at the WARN level.
     */
    public static void warn(String message) {
        componentLogger.warn(MiniMessage.miniMessage().deserialize(message));
    }

    /**
     * Logs message at the ERROR level.
     */
    public static void error(String message) {
        componentLogger.error(MiniMessage.miniMessage().deserialize(message));
    }

    /**
     * Logs message at the INFO level, but with [Debug] prefix.
     */
    public static void debug(String message) {
        if (!debugging) return;

        info("<dark_aqua>[Debug] " + message);
    }
}
