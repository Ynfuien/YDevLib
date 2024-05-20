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

    public static void setup(String prefix, ComponentLogger componentLogger) {
        YLogger.prefix = prefix;
        YLogger.componentLogger = componentLogger;
    }

    public static void setDebugging(boolean enabled) {
        debugging = enabled;
    }

    public static void info(String message) {
        Messenger.send(console, prefix + message);
    }

    public static void info(String message, HashMap<String, Object> placeholders) {
        Messenger.send(console, prefix + message, placeholders);
    }

    public static void warn(String message) {
        componentLogger.warn(MiniMessage.miniMessage().deserialize(message));
    }

    public static void error(String message) {
        componentLogger.error(MiniMessage.miniMessage().deserialize(message));
    }

    public static void debug(String message) {
        if (!debugging) return;

        Messenger.send(console, String.format("%s <dark_aqua>[Debug] %s", prefix, message));
    }
}
