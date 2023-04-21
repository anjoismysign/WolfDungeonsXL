package us.mytheria.wolfdungeonsxl.events;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import us.mytheria.wolfdungeonsxl.entities.DungeonLobby;

public abstract class DungeonLobbyEvent extends PlayerEvent {
    private final DungeonLobby dungeonLobby;

    public DungeonLobbyEvent(@NotNull Player who, @NotNull DungeonLobby dungeonLobby) {
        super(who);
        this.dungeonLobby = dungeonLobby;
    }

    /**
     * @return The dungeon lobby
     */
    public DungeonLobby getDungeonLobby() {
        return dungeonLobby;
    }
}
