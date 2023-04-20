package us.mytheria.wolfdungeonsxl.listeners;

import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import de.erethon.dungeonsxl.api.event.group.GroupPlayerJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import us.mytheria.wolfdungeonsxl.director.ListenerManager;
import us.mytheria.wolfdungeonsxl.director.WDListener;
import us.mytheria.wolfdungeonsxl.director.WDManagerDirector;
import us.mytheria.wolfdungeonsxl.entities.DungeonLobby;

public class OnJoin extends WDListener {
    public OnJoin(ListenerManager listenerManager) {
        super(listenerManager);
        Bukkit.getPluginManager().registerEvents(this,
                getListenerManager().getPlugin());
    }

    @EventHandler(ignoreCancelled = true)
    public void onJoin(GroupPlayerJoinEvent event) {
        Player player = event.getPlayer().getPlayer();
        WDManagerDirector director = getListenerManager().getManagerDirector();
        director.getDungeonManager().add(player);
        Dungeon dungeon = event.getGroup().getDungeon();
        DungeonLobby lobby = director.getDungeonLobbyDirector().getObjectManager()
                .getObject(dungeon.getName());
        if (lobby == null)
            return;
        lobby.teleport(player);
    }
}
