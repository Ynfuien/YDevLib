package pl.ynfuien.ydevlib.guis.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import pl.ynfuien.ydevlib.guis.GUIPanelHolder;

public class InventoryDragListener implements Listener {


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryDragEvent event) {
        InventoryView view = event.getView();
        InventoryHolder holder = view.getTopInventory().getHolder();
        if (holder == null) return;

        if (!(holder instanceof GUIPanelHolder panelHolder)) return;

        int maxSlotIndex = holder.getInventory().getSize() - 1;
        for (int slotNumber : event.getRawSlots()) {
            if (slotNumber > maxSlotIndex) continue;

            event.setCancelled(true);
            return;
        }

//        event.setResult(Event.Result.DENY);
    }
}
