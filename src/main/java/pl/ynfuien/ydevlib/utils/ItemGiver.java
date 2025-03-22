package pl.ynfuien.ydevlib.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ItemGiver {
    /**
     * Gives provided item to player's inventory, while also dropping on the ground anything that couldn't fit in.
     * @return True if everything fit in, false if anything was dropped on the ground
     */
    public static boolean giveItems(Player p, ItemStack item) {
        return giveItems(p, new ItemStack[] {item});
    }

    /**
     * Gives provided items to player's inventory, while also dropping on the ground the ones that couldn't fit in.
     * @return True if everything fit in, false if anything was dropped on the ground
     */
    public static boolean giveItems(Player player, ItemStack[] items) {
        // Items that couldn't be added to player's inventory
        HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(items);

        if (remainingItems.isEmpty()) return true;

        World world = player.getWorld();
        Location eyeLocation = player.getEyeLocation();

        for (ItemStack remainingItem : remainingItems.values()) world.dropItem(eyeLocation, remainingItem);

        return false;
    }
}
