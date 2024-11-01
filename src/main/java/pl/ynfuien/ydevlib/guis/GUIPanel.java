package pl.ynfuien.ydevlib.guis;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.ydevlib.messages.YLogger;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class GUIPanel implements InventoryHolder {
    protected Inventory inventory;

    protected final String name;
    protected String title = null;
    protected Short rows;

    protected GUISound openSound = null;
    protected GUISound closeSound = null;

    protected Item emptySlotItem = null;
    protected HashMap<Short, Item> slots;

    public GUIPanel(String name) {
        this.name = name;
    }

    public boolean load(ConfigurationSection config) {
        if (!config.contains("rows")) {
            log("Missing key 'rows'");
            return false;
        }

        // Title
        if (config.contains("title")) title = config.getString("title");

        // Rows
        try {
            rows = (short) config.getInt("rows");
        } catch (NumberFormatException e) {
            log("Rows number is incorrect!");
            return false;
        }

        if (rows > 6) {
            log("Rows number can't be higher than 6!");
            return false;
        }

        if (rows < 1) {
            log("Rows number can't be lower than 1!");
            return false;
        }

        inventory = Bukkit.createInventory(this, rows, title);

        // Sounds
        for (String soundName : Arrays.asList("open-sound", "close-sound")) {
            if (config.contains(soundName)) {
                boolean loaded;

                if (soundName.equals("open-sound")) {
                    openSound = new GUISound(soundName);
                    loaded = openSound.load(config.getConfigurationSection(soundName));
                } else {
                    closeSound = new GUISound(soundName);
                    loaded = closeSound.load(config.getConfigurationSection(soundName));
                }

                if (!loaded) log(MessageFormat.format("%s couldn't be loaded!", soundName));
            }
        }

        // Empty slot item
        if (config.contains("items.empty-slot")) {
            emptySlotItem = Item.load(config.getConfigurationSection("items"), "empty-slot");
        }

        // Slots
        ConfigurationSection slotsSection = config.getConfigurationSection("slots");
        if (slotsSection == null) return true;

        int maxSlotIndex = (rows * 9) - 1;

        Set<String> slotNumbers = slotsSection.getKeys(false);
        for (String slotNumber : slotNumbers) {
            short slot;
            try {
                slot = Short.parseShort(slotNumber);
            } catch (NumberFormatException e) {
                log(String.format("Slot number '%s' is incorrect! It has to be a number.", slotNumber));
                continue;
            }

            if (slot < 0) {
                log(String.format("Slot number '%d' is lower than 0!", slot));
                continue;
            }

            if (slot > (rows * 9) - 1) {
                log(String.format("Slot number '%d' is higher than %d!", slot, maxSlotIndex));
                continue;
            }

            Item item = Item.load(slotsSection, slotNumber);
            if (item == null) {
                log(String.format("Item on slot '%d' couldn't be loaded!", slot));
                continue;
            }

            slots.put(slot, item);
        }

        return true;
    }


    protected void log(String message) {
        YLogger.warn(MessageFormat.format("[GUIPanel-{0}] {1}", name, message));
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public Short getRows() {
        return rows;
    }

    public GUISound getOpenSound() {
        return openSound;
    }

    public GUISound getCloseSound() {
        return closeSound;
    }

    public Item getEmptySlotItem() {
        return emptySlotItem;
    }

    public HashMap<Short, Item> getSlots() {
        return slots;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}