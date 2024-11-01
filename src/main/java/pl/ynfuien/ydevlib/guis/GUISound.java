package pl.ynfuien.ydevlib.guis;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pl.ynfuien.ydevlib.messages.YLogger;

import java.text.MessageFormat;

public class GUISound {
    // Like "open-sound", "close-sound"
    protected String name;
    protected Sound sound;
    protected Float volume;
    protected Float pitch;

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
            soundString = soundString.replace('.', '_').toUpperCase();
            sound = Sound.valueOf(soundString);
        } catch (IllegalArgumentException e) {
            log("Provided sound is incorrect!");
            return false;
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
        else {
            volume = 1f;
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
        else {
            pitch = 1f;
        }

        return true;
    }

    protected void log(String message) {
        YLogger.warn(MessageFormat.format("[Sound-{0}] {1}", name, message));
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
        p.playSound(p.getLocation(), sound, volume, pitch);
    }
}
