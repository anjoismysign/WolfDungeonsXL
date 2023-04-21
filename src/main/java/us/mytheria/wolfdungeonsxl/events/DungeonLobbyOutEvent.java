package us.mytheria.wolfdungeonsxl.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import us.mytheria.wolfdungeonsxl.entities.DungeonLobby;

public class DungeonLobbyOutEvent extends DungeonLobbyEvent {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public DungeonLobbyOutEvent(@NotNull Player who, @NotNull DungeonLobby dungeonLobby) {
        super(who, dungeonLobby);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
}
