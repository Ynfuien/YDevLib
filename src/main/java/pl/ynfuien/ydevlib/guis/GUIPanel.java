package pl.ynfuien.ydevlib.guis;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.ydevlib.guis.listeners.InventoryClickListener;
import pl.ynfuien.ydevlib.guis.listeners.InventoryCloseListener;
import pl.ynfuien.ydevlib.guis.listeners.InventoryDragListener;
import pl.ynfuien.ydevlib.guis.listeners.PlayerQuitListener;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.ydevlib.utils.YamlHelper;

import java.util.*;

public abstract class GUIPanel {
    private final static HashMap<String, GUIPanel> guiPanels = new HashMap<>();

    protected final Plugin plugin;
    protected final String name;
    @Nullable
    protected String title = null;
    protected Short rows;
    protected int refreshRate = -1;

    @Nullable
    protected GUISound openSound = null;
    @Nullable
    protected GUISound closeSound = null;

    protected HashMap<String, Item> items = new HashMap<>();
    protected HashMap<Short, Item> slots = new HashMap<>();

    protected HashMap<UUID, GUIPanelHolder> inventories = new HashMap<>();
    protected ScheduledTask refreshTask = null;

    protected GUIPanel(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;

        guiPanels.put(name, this);
    }

    public boolean load(ConfigurationSection config) {
        if (refreshTask != null) refreshTask.cancel();

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

        // Refresh rate
        if (config.contains("refresh-rate")) refreshRate = config.getInt("refresh-rate");
        if (refreshRate < 1) refreshRate = -1;

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


                if (!loaded) log(String.format("%s couldn't be loaded!", soundName));
            }
        }

        // Items
        items.clear();
        if (config.contains("items")) {
            ConfigurationSection itemSection = config.getConfigurationSection("items");
            for (String itemName : itemSection.getKeys(false)) {
                Item item = Item.load(itemSection, itemName);
                if (item == null) {
                    log(String.format("Item '%s' couldn't be loaded!", itemName));
                    continue;
                }

                items.put(itemName.toLowerCase(), item);
            }
        }

        // Slots
        slots.clear();
        ConfigurationSection slotsSection = config.getConfigurationSection("slots");
        if (slotsSection == null) return true;

        int maxSlotIndex = (rows * 9) - 1;

        Set<String> slotEntries = slotsSection.getKeys(false);
        for (String slotEntry : slotEntries) {
            Set<Integer> slotSet = YamlHelper.getIntSetFromRangePattern(slotEntry);

            if (slotSet.isEmpty()) {
                log(String.format("Slot entry '%s' is incorrect! It has to be a number or an entry in the format '{number}-{number}'.", slotEntry));
                continue;
            }

            Iterator<Integer> iterator = slotSet.iterator();
            while (iterator.hasNext()) {
                int slot = iterator.next();

                if (slot < 0) {
                    log(String.format("Slot number '%d' is lower than 0!", slot));
                    iterator.remove();
                    continue;
                }

                if (slot > maxSlotIndex) {
                    log(String.format("Slot number '%d' is higher than %d!", slot, maxSlotIndex));
                    iterator.remove();
                }
            }


            Item item = getItemForTheSlotEntry(slotsSection, slotEntry);
            if (item == null) log(String.format("Item on slot entry '%s' couldn't be loaded!", slotEntry));

            for (int slot : slotSet) slots.put((short) slot, item);
        }

        if (refreshRate != -1) {
            refreshTask = Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, (task) -> {
                update();
            }, 1, refreshRate);
        }

        return true;
    }

    private Item getItemForTheSlotEntry(ConfigurationSection slotsSection, String slotEntry) {
        // Preset item
        if (slotsSection.isString(slotEntry)) {
            String itemName = slotsSection.getString(slotEntry);

            if (itemName.startsWith("item:")) {
                itemName = itemName.substring(5);

                Item item = items.get(itemName);
                if (item == null) log(String.format("There is no preset item with the name '%s'!", itemName));

                return item;
            }
        }

        return Item.load(slotsSection, slotEntry);
    }

    /**
     * Updates the GUI for all the players.
     */
    public void update() {
        for (GUIPanelHolder panelHolder : inventories.values()) {
            InventoryHolder holder = panelHolder.getPlayer().getOpenInventory().getTopInventory().getHolder();
            if (holder == null) continue;

            if (!holder.equals(panelHolder)) continue;

            panelHolder.update();
        }
    }

    /**
     * Updates the GUI for a specific player.
     */
    public void update(Player player) {
        UUID uuid = player.getUniqueId();

        GUIPanelHolder panelHolder = inventories.get(uuid);
        if (panelHolder == null) return;

        panelHolder.update();
    }

    public void open(Player player) {
        UUID uuid = player.getUniqueId();
        if (!inventories.containsKey(uuid)) inventories.put(uuid, new GUIPanelHolder(this, player));

        GUIPanelHolder panelHolder = inventories.get(uuid);
        panelHolder.open();
    }

    protected abstract void updateSpecialItems(Player player, Inventory inventory);

    public abstract void handleClickEvent(InventoryClickEvent event);


    protected void log(String message) {
        YLogger.warn(String.format("[GUIPanel-%s] %s", name, message));
    }

    public String getName() {
        return name;
    }

    public @Nullable String getTitle() {
        return title;
    }

    public Short getRows() {
        return rows;
    }

    public @Nullable GUISound getOpenSound() {
        return openSound;
    }

    public @Nullable GUISound getCloseSound() {
        return closeSound;
    }

    public @Nullable Item getEmptySlotItem() {
        return items.get("empty-slot");
    }

    public HashMap<Short, Item> getSlots() {
        return slots;
    }

    private static boolean registered = false;
    public static void registerListeners(Plugin plugin) {
        if (registered) return;
        registered = true;

        Listener[] listeners = new Listener[] {
                new InventoryClickListener(),
                new InventoryDragListener(),
                new InventoryCloseListener(),
                new PlayerQuitListener(),
        };

        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
    }

    public static void removePanelHolders(Player player) {
        for (GUIPanel panel : guiPanels.values()) panel.inventories.remove(player.getUniqueId());
    }

    public static boolean openPanel(String panelName, Player player) {
        GUIPanel panel = guiPanels.get(panelName);
        if (panel == null) return false;

        panel.open(player);
        return true;
    }
}