package pl.ynfuien.ydevlib.guis;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pl.ynfuien.ydevlib.messages.YLogger;

public class GUISound {
    // Like "open-sound", "close-sound"
    protected String name;
    protected Sound sound;
    protected SoundCategory soundCategory = SoundCategory.MASTER;
    protected Float volume = 1f;
    protected Float pitch = 1f;

    public GUISound(String name) {
        this.name = name;
    }

    public boolean load(ConfigurationSection configSection) {
        if (!configSection.contains("sound")) {
            log("Missing key 'sound'");
            return false;
        }

        // Sound
        try {
            String soundString = configSection.getString("sound");
//            soundString = soundString.replace('.', '_').toUpperCase();
            sound = Registry.SOUNDS.get(NamespacedKey.minecraft(soundString));
        } catch (IllegalArgumentException e) {
            log("Provided sound is incorrect!");
            return false;
        }

        // Category
        if (configSection.contains("category")) {
            try {
                soundCategory = SoundCategory.valueOf(configSection.getString("category").toUpperCase());
            } catch (IllegalArgumentException e) {
                log("Provided sound category is incorrect!");
                return false;
            }
        }

        // Volume
        if (configSection.contains("volume")) {
            try {
                volume = Float.valueOf(configSection.getString("volume"));
            } catch (IllegalArgumentException e) {
                log("Provided volume is incorrect!");
                return false;
            }
        }

        // Pitch
        if (configSection.contains("pitch")) {
            try {
                pitch = Float.valueOf(configSection.getString("pitch"));
            } catch (IllegalArgumentException e) {
                log("Provided pitch is incorrect!");
                return false;
            }
        }

        return true;
    }

    protected void log(String message) {
        YLogger.warn(String.format("[Sound-%s] %s", name, message));
    }

    public Sound sound() {
        return sound;
    }

    public Float volume() {
        return volume;
    }

    public Float pitch() {
        return pitch;
    }

    public void playSound(Player p) {
        p.playSound(p.getLocation(), sound, soundCategory, volume, pitch);
    }
}
