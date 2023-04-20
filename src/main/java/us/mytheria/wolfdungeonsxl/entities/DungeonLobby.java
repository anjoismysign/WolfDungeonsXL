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

    public DungeonLobby(String key, Dungeon dungeon, Location location) {
        this.key = key;
        this.dungeon = dungeon;
        this.location = location;
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
        return file;
    }

    public static DungeonLobby fromFile(File file, DungeonsAPI api) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
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
        String dungeonName = config.getString("Dungeon");
        Dungeon dungeon = api.getDungeonRegistry().get(dungeonName);
        if (dungeon == null) {
            Bukkit.getLogger().severe("Dungeon not valid! (File: " + file.getName());
            throw new IllegalArgumentException("Dungeon not valid! (File: " + file.getName());
        }
        return new DungeonLobby(file.getName().replace(".yml", ""), dungeon, location);
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