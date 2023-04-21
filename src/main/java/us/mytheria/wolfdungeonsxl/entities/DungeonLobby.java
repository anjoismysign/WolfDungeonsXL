package us.mytheria.wolfdungeonsxl.entities;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import us.mytheria.bloblib.entities.BlobObject;
import us.mytheria.bloblib.utilities.SerializationLib;

import java.io.File;

public class DungeonLobby implements BlobObject {
    private final String key;
    private final Dungeon dungeon;
    private final Location location;
    private final long lobbyTime, gameTime;

    //Transient fields;
    private boolean isRunning;

    public DungeonLobby(String key, Dungeon dungeon, Location location,
                        long lobbyTime, long gameTime) {
        this.key = key;
        this.dungeon = dungeon;
        this.location = location;
        this.lobbyTime = lobbyTime;
        this.gameTime = gameTime;
        isRunning = false;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public File saveToFile(File directory) {
        File file = new File(directory + "/" + key + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("Dungeon", dungeon.getName());
        config.set("Location", SerializationLib.serialize(location));
        config.set("Lobby-Time", lobbyTime);
        config.set("Game-Time", gameTime);
        return file;
    }

    public static DungeonLobby fromFile(File file, DungeonsAPI api) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.isString("Location"))
            throw new IllegalArgumentException("'Location' is not set! (File: " + file.getName());
        String x = config.getString("Location");
        if (x == null)
            throw new IllegalArgumentException("Location not valid! (File: " + file.getName());
        String y = x.split(",")[0];
        World world = SerializationLib.deserializeWorld(y);
        if (world == null) {
            Bukkit.getLogger().severe("World not valid! (File: " + file.getName());
            throw new IllegalArgumentException("World not valid! (File: " + file.getName());
        }
        Location location = SerializationLib.deserializeLocation(x);
        if (!config.isString("Dungeon"))
            throw new IllegalArgumentException("'Dungeon' is not set! (File: " + file.getName());
        String dungeonName = config.getString("Dungeon");
        Dungeon dungeon = api.getDungeonRegistry().get(dungeonName);
        if (dungeon == null) {
            Bukkit.getLogger().severe("Dungeon not valid! (File: " + file.getName());
            throw new IllegalArgumentException("Dungeon not valid! (File: " + file.getName());
        }
        if (!config.isLong("Lobby-Time"))
            throw new IllegalArgumentException("'Lobby-Time' is not set! (File: " + file.getName());
        long lobbyTime = config.getLong("Lobby-Time");
        if (!config.isLong("Game-Time"))
            throw new IllegalArgumentException("'Game-Time' is not set! (File: " + file.getName());
        long gameTime = config.getLong("Game-Time");
        return new DungeonLobby(file.getName().replace(".yml", ""),
                dungeon, location, lobbyTime, gameTime);
    }

    /**
     * Teleports a player to the lobby.
     *
     * @param player The player to teleport.
     */
    public void teleport(Player player) {
        player.teleport(location);
    }

    
}