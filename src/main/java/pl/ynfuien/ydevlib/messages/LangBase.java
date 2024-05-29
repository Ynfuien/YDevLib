package pl.ynfuien.ydevlib.messages;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public abstract class LangBase {
    private static FileConfiguration langConfig;

    public static void loadLang(FileConfiguration langConfig) {
        LangBase.langConfig = langConfig;
    }

    @Nullable
    public static String get(String path) {
        return langConfig.getString(path);
    }

    @Nullable
    public static String get(String path, HashMap<String, Object> placeholders) {
        placeholders.put("prefix", get("prefix"));

        return Messenger.parsePluginPlaceholders(langConfig.getString(path), placeholders);
    }

    public static void sendMessage(CommandSender sender, Message message, HashMap<String, Object> placeholders) {
        List<String> messages;

        String path = message.getName();
        if (!langConfig.isSet(path)) {
            YLogger.error(String.format("There is no message '%s'!", path));
            return;
        }

        if (langConfig.isList(path)) messages = langConfig.getStringList(path);
        else messages = List.of(langConfig.getString(path));

        placeholders.put("prefix", get("prefix"));
        for (String msg : messages) {
            // Skip if message is empty
            if (msg.isEmpty()) continue;

            Messenger.send(sender, msg, placeholders);
        }
    }

    public interface Message {
        String getName();
    }
}
