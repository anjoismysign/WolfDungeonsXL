package us.mytheria.wolfdungeonsxl.listeners;

import de.erethon.dungeonsxl.api.event.group.GroupPlayerLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import us.mytheria.wolfdungeonsxl.director.ListenerManager;
import us.mytheria.wolfdungeonsxl.director.WDListener;
import us.mytheria.wolfdungeonsxl.director.WDManagerDirector;

public class OnLeave extends WDListener {
    public OnLeave(ListenerManager listenerManager) {
        super(listenerManager);
        Bukkit.getPluginManager().registerEvents(this,
                getListenerManager().getPlugin());
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeave(GroupPlayerLeaveEvent event) {
        Player player = event.getPlayer().getPlayer();
        WDManagerDirector director = getListenerManager().getManagerDirector();
        director.getDungeonManager().remove(player);
    }
}
