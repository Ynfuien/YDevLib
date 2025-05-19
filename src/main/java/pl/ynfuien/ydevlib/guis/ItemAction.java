package pl.ynfuien.ydevlib.guis;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.ynfuien.ydevlib.messages.Messenger;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.ydevlib.utils.CommonPlaceholders;

import javax.annotation.Nullable;
import java.util.HashMap;

public class ItemAction {
    private final Item item;

    // Conditions
    private ClickType clickType = null;
    private boolean shift = false;

    // Results
    private String playerCommand = null;
    private String consoleCommand = null;
    private boolean closeGui = false;
    private String openGui = null;

    public ItemAction(Item item) {
        this.item = item;
    }

    public boolean load(ConfigurationSection config) {
        String click = config.getString("condition.click");
        if (click == null) {
            log("There is no click type provided!");
            return false;
        }

        shift = config.getBoolean("condition.shift");

        if (click.equalsIgnoreCase("left")) {
            clickType = shift ? ClickType.SHIFT_LEFT : ClickType.LEFT;
        } else if (click.equalsIgnoreCase("right")) {
            clickType = shift ? ClickType.SHIFT_RIGHT : ClickType.RIGHT;
        } else if (click.equalsIgnoreCase("middle")) {
            clickType = ClickType.MIDDLE;
        } else {
            log(String.format("Click type '%s' is incorrect!", click));
            return false;
        }

        ConfigurationSection resultSection = config.getConfigurationSection("result");
        playerCommand = resultSection.getString("player-command");
        consoleCommand = resultSection.getString("console-command");
        closeGui = resultSection.getBoolean("close-gui");
        openGui = resultSection.getString("open-gui");

        if (playerCommand.isBlank()) playerCommand = null;
        if (consoleCommand.isBlank()) consoleCommand = null;
        return true;
    }

    protected void log(String message) {
        YLogger.warn(String.format("[Item-Action] %s", message));
    }

    public void performAction(InventoryClickEvent event) {
        if (!event.getClick().equals(clickType)) return;
        Player player = (Player) event.getWhoClicked();

        HashMap<String, Object> placeholders = new HashMap<>();
        CommonPlaceholders.setPlayer(placeholders, player);

        if (playerCommand != null) {
            player.performCommand(Messenger.parsePluginPlaceholdersAndPAPI(player, playerCommand, placeholders));
        }

        if (consoleCommand != null) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Messenger.parsePluginPlaceholdersAndPAPI(player, consoleCommand, placeholders));
        }

        if (closeGui) player.closeInventory();
        if (openGui != null) GUIPanel.openPanel(openGui, player);
    }

    public Item getItem() {
        return item;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public boolean isShift() {
        return shift;
    }

    @Nullable
    public String getPlayerCommand() {
        return playerCommand;
    }

    @Nullable
    public String getConsoleCommand() {
        return consoleCommand;
    }

    public boolean isCloseGui() {
        return closeGui;
    }

    @Nullable
    public String getOpenGui() {
        return openGui;
    }
}