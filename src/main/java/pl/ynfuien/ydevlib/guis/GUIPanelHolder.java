package pl.ynfuien.ydevlib.guis;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.ydevlib.messages.Messenger;

import java.util.HashMap;

public class GUIPanelHolder implements InventoryHolder {
    private final GUIPanel guiPanel;
    private final Player player;
    private final Inventory inventory;

    public GUIPanelHolder(GUIPanel guiPanel, Player player) {
        this.guiPanel = guiPanel;
        this.player = player;

        int size = guiPanel.getRows() * 9;

        String title = guiPanel.getTitle();
        if (title == null) {
            this.inventory = Bukkit.createInventory(this, size);
            update();
            return;
        }

        Component parsed = Messenger.parseMessage(player, title, null);
        this.inventory = Bukkit.createInventory(this, size, parsed);
    }

    public GUIPanel getGuiPanel() {
        return guiPanel;
    }

    public Player getPlayer() {
        return player;
    }

    public void update() {
        inventory.clear();

        HashMap<Short, Item> slots = guiPanel.getSlots();
        Item emptySlotItem = guiPanel.getEmptySlotItem();
        ItemStack emptySlotItemStack = ItemStack.empty();
        if (emptySlotItem != null) emptySlotItemStack = emptySlotItem.getItemStack(player);

        for (int i = 0; i < inventory.getSize(); i++) {
            Item item = slots.get((short) i);
            if (item == null) {
                inventory.setItem(i, emptySlotItemStack);
                continue;
            }

            inventory.setItem(i, item.getItemStack(player));
        }

//        HashMap<Short, Item> slots = guiPanel.getSlots();
//        for (Short slotNumber : slots.keySet()) {
//            Item item = slots.get(slotNumber);
//
//            inventory.setItem(slotNumber, item.getItemStack(player));
//        }
//
//        Item emptySlotItem = guiPanel.getEmptySlotItem();
//        if (emptySlotItem != null) {
//            ItemStack emptySlotItemStack = emptySlotItem.getItemStack(player);
//
//            for (int i = 0; i < inventory.getSize(); i++) {
//                ItemStack item = inventory.getItem(i);
//                if (item != null) continue;
//
//                inventory.setItem(i, emptySlotItemStack);
//            }
//        }
    }

    public void open() {
        update();

        player.openInventory(inventory);

        GUISound openSound = guiPanel.getOpenSound();
        if (openSound != null) openSound.playSound(player);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
