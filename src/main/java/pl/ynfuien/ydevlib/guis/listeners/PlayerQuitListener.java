package pl.ynfuien.ydevlib.guis.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.ynfuien.ydevlib.guis.GUIPanel;

public class PlayerQuitListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        GUIPanel.removePanelHolders(event.getPlayer());
    }
}
