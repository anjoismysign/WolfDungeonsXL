package us.mytheria.wolfdungeonsxl.entities;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.mytheria.bloblib.entities.BlobObject;
import us.mytheria.bloblib.utilities.SerializationLib;
import us.mytheria.wolfdungeonsxl.director.WDManagerDirector;
import us.mytheria.wolfdungeonsxl.events.DungeonLobbyInEvent;
import us.mytheria.wolfdungeonsxl.events.DungeonLobbyOutEvent;

import java.io.File;

public class DungeonLobby implements BlobObject {
    private final String key;
    private final Dungeon dungeon;
    private final Location location;
    private final long lobbyTime, gameTime;
    private final int maxPlayers;
    private final WDManagerDirector director;
    private final DungeonsAPI api;

    //Transient fields;
    private boolean isRunning;
    private boolean inTransmission;
    private Party party;

    public DungeonLobby(String key, Dungeon dungeon, Location location,
                        long lobbyTime, long gameTime, int maxPlayers,
                        WDManagerDirector director) {
        this.key = key;
        this.dungeon = dungeon;
        this.location = location;
        this.lobbyTime = lobbyTime;
        this.gameTime = gameTime;
        this.isRunning = false;
        this.inTransmission = false;
        this.director = director;
        this.api = director.getPlugin().getDungeonsAPI();
        this.maxPlayers = maxPlayers;
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
        config.set("Max-Players", maxPlayers);
        return file;
    }

    public static DungeonLobby fromFile(File file, WDManagerDirector director) {
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
        Dungeon dungeon = director.getPlugin().getDungeonsAPI().getDungeonRegistry().get(dungeonName);
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
        if (!config.isInt("Max-Players"))
            throw new IllegalArgumentException("'Max-Players' is not set! (File: " + file.getName());
        int maxPlayers = config.getInt("Max-Players");
        return new DungeonLobby(file.getName().replace(".yml", ""),
                dungeon, location, lobbyTime, gameTime, maxPlayers, director);
    }

    private void teleport(Player player) {
        player.teleport(location);
    }

    private void startTimer() {
        new BukkitRunnable() {
            private long time = lobbyTime;

            @Override
            public void run() {
                if (time == 0) {
                    endTransmission();
                    cancel();
                }
                time--;
            }
        }.runTaskTimer(director.getPlugin(), 0, 20);
    }

    /**
     * Will start the lobby transmission
     *
     * @param owner The owner of the lobby
     * @return The party
     */
    public Party startTransmission(Player owner) {
        Party party = Party.of(owner);
        this.party = party;
        this.inTransmission = true;
        teleport(owner);
        startTimer();
        return party;
    }

    /**
     * Will add a new member to the lobby
     *
     * @param newMember The new member
     * @return True if the member was successfully added
     */
    public boolean add(Player newMember) {
        if (!director.getDungeonManager().add(newMember, party))
            return false;
        boolean shouldEndTransmission = party.getMembers().size() + 1 == maxPlayers;
        DungeonLobbyInEvent event = new DungeonLobbyInEvent(newMember, this);
        Bukkit.getPluginManager().callEvent(event);
        if (shouldEndTransmission)
            endTransmission();
        teleport(newMember);
        return true;
    }

    /**
     * Will remove an existent member from the lobby
     *
     * @param existentPlayer The player to remove
     */
    public void remove(Player existentPlayer) {
        DungeonLobbyOutEvent event = new DungeonLobbyOutEvent(existentPlayer, this);
        Bukkit.getPluginManager().callEvent(event);
        director.getDungeonManager().remove(existentPlayer);
    }

    /**
     * Will end the lobby transmission
     */
    public void endTransmission() {
        Player owner = party.getLeaderPlayer();
        if (owner == null)
            return;
        api.createGroup(owner, party.getAllPlayerMembers(),
                director.getConfigManager().nameParty(party), dungeon);
        this.inTransmission = false;
        this.isRunning = true;
    }
}