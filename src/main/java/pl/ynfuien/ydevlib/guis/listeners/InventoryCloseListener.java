package pl.ynfuien.ydevlib.guis.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import pl.ynfuien.ydevlib.guis.GUIPanelHolder;
import pl.ynfuien.ydevlib.guis.GUISound;

public class InventoryCloseListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (holder == null) return;

        if (!(holder instanceof GUIPanelHolder panelHolder)) return;

        GUISound closeSound = panelHolder.getGuiPanel().getCloseSound();
        if (closeSound == null) return;

        closeSound.playSound((Player) event.getPlayer());
    }
}
