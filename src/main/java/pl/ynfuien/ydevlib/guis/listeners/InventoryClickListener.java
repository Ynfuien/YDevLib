package pl.ynfuien.ydevlib.guis.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import pl.ynfuien.ydevlib.guis.GUIPanelHolder;

public class InventoryClickListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) return;

        Inventory topInventory = event.getInventory();
        InventoryHolder holder = topInventory.getHolder();
        if (holder == null) return;

        if (!(holder instanceof GUIPanelHolder panelHolder)) return;

        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);
            return;
        }

        InventoryAction action = event.getAction();
        if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || action.equals(InventoryAction.COLLECT_TO_CURSOR)) {
            event.setCancelled(true);
        }
    }
}
