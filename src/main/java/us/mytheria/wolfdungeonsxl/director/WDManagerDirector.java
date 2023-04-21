package us.mytheria.wolfdungeonsxl.director;

import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import us.mytheria.bloblib.entities.BlobExecutor;
import us.mytheria.bloblib.entities.ObjectDirector;
import us.mytheria.bloblib.managers.ManagerDirector;
import us.mytheria.wolfdungeonsxl.WolfDungeonsXL;
import us.mytheria.wolfdungeonsxl.entities.DungeonLobby;

public class WDManagerDirector extends ManagerDirector {
    public WDManagerDirector(WolfDungeonsXL plugin) {
        super(plugin);
        addManager("Config", new ConfigManager(this));
        addDirector("DungeonLobby", file -> DungeonLobby.fromFile(file,
                this), false);
        getDungeonLobbyDirector().addAdminChildCommand(executorData -> {
            BlobExecutor executor = executorData.executor();
            CommandSender sender = executorData.sender();
            String[] args = executorData.args();
            if (args.length != 4) {
                sender.sendMessage("Usage: /dungeonlobby <lobbyName> <lobbyTime> <gameTime> " +
                        "<maxPlayers>");
                return true;
            }
            String arg = args[0];
            Dungeon dungeon = getPlugin().getDungeonsAPI().getDungeonRegistry().get(arg);
            if (dungeon == null) {
                sender.sendMessage("Dungeon not found.");
                return true;
            }
            DungeonLobby existent = getDungeonLobbyDirector().getObjectManager().getObject(arg);
            if (existent != null) {
                sender.sendMessage("Dungeon lobby already exists.");
                return true;
            }
            String lt = args[1];
            String gt = args[2];
            String mp = args[3];
            long lobbyTime;
            long gameTime;
            int maxPlayers;
            try {
                lobbyTime = Long.parseLong(lt);
                gameTime = Long.parseLong(gt);
                maxPlayers = Integer.parseInt(mp);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid number.");
                return true;
            }
            executor.ifInstanceOfPlayer(sender, player -> {
                DungeonLobby lobby = new DungeonLobby(arg, dungeon,
                        player.getLocation().getBlock().getLocation()
                                .clone().add(new Vector(0.5, 0.1, 0.5)),
                        lobbyTime, gameTime, maxPlayers, this);
                getDungeonLobbyDirector().getObjectManager().addObject(arg, lobby);
                sender.sendMessage("Dungeon lobby created.");
            });
            return true;
        });
        addManager("Dungeon", new DungeonManager(this));
        addManager("Listener", new ListenerManager(this));
    }

    @Override
    public WolfDungeonsXL getPlugin() {
        return (WolfDungeonsXL) super.getPlugin();
    }

    /**
     * @return ConfigManager
     */
    public ConfigManager getConfigManager() {
        return getManager("Config", ConfigManager.class);
    }

    /**
     * Get the DungeonLobby director
     *
     * @return DungeonLobby director
     */
    public ObjectDirector<DungeonLobby> getDungeonLobbyDirector() {
        return getDirector("DungeonLobby", DungeonLobby.class);
    }

    /**
     * Get the DungeonManager
     *
     * @return DungeonManager
     */
    public DungeonManager getDungeonManager() {
        return getManager("Dungeon", DungeonManager.class);
    }
}
