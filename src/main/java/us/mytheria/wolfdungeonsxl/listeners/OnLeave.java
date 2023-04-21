package us.mytheria.wolfdungeonsxl.listeners;

import de.erethon.dungeonsxl.api.event.group.GroupPlayerKickEvent;
import de.erethon.dungeonsxl.api.event.group.GroupPlayerLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import us.mytheria.wolfdungeonsxl.director.ListenerManager;
import us.mytheria.wolfdungeonsxl.director.WDListener;
import us.mytheria.wolfdungeonsxl.director.WDManagerDirector;
import us.mytheria.wolfdungeonsxl.events.DungeonLobbyOutEvent;

public class OnLeave extends WDListener {
    public OnLeave(ListenerManager listenerManager) {
        super(listenerManager);
        Bukkit.getPluginManager().registerEvents(this,
                getListenerManager().getPlugin());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        WDManagerDirector director = getListenerManager().getManagerDirector();
        director.getDungeonManager().remove(player);
    }

    @EventHandler
    public void onLeave(DungeonLobbyOutEvent event) {
        Player player = event.getPlayer().getPlayer();
        WDManagerDirector director = getListenerManager().getManagerDirector();
        director.getDungeonManager().remove(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeave(GroupPlayerLeaveEvent event) {
        Player player = event.getPlayer().getPlayer();
        WDManagerDirector director = getListenerManager().getManagerDirector();
        director.getDungeonManager().remove(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onKick(GroupPlayerKickEvent event) {
        Player player = event.getPlayer().getPlayer();
        WDManagerDirector director = getListenerManager().getManagerDirector();
        director.getDungeonManager().remove(player);
    }
}
