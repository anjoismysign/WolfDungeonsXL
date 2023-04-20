package us.mytheria.wolfdungeonsxl.director;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DungeonManager extends WDManager {
    private final Map<String, Location> homes;

    public DungeonManager(WDManagerDirector managerDirector) {
        super(managerDirector);
        this.homes = new HashMap<>();
    }

    /**
     * Will add a player to the home list.
     *
     * @param player The player to add.
     */
    public void add(Player player) {
        homes.put(player.getName(), player.getLocation().clone());
    }

    /**
     * Will remove a player from the home list and teleport him to his home.
     *
     * @param player The player to remove.
     */
    public void remove(Player player) {
        if (!homes.containsKey(player.getName()))
            return;
        Location home = homes.get(player.getName());
        player.teleport(home);
        homes.remove(player.getName());
    }
}
