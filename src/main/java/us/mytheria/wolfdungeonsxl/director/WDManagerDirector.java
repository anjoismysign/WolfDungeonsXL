package us.mytheria.wolfdungeonsxl.director;

import de.erethon.dungeonsxl.api.dungeon.Dungeon;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import us.mytheria.bloblib.entities.BlobExecutor;
import us.mytheria.bloblib.entities.ObjectDirector;
import us.mytheria.bloblib.managers.ManagerDirector;
import us.mytheria.wolfdungeonsxl.WolfDungeonsXL;
import us.mytheria.wolfdungeonsxl.entities.DungeonLobby;

import java.util.ArrayList;
import java.util.List;

public class WDManagerDirector extends ManagerDirector {
    public WDManagerDirector(WolfDungeonsXL plugin) {
        super(plugin);
        addDirector("DungeonLobby", file -> DungeonLobby.fromFile(file,
                        getPlugin().getDungeonsAPI()),
                false);
        getDungeonLobbyDirector().addAdminChildCommand(executorData -> {
            BlobExecutor executor = executorData.executor();
            CommandSender sender = executorData.sender();
            String[] args = executorData.args();
            if (args.length != 1) {
                sender.sendMessage("Usage: /dungeonlobby <lobbyName>");
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
            executor.ifInstanceOfPlayer(sender, player -> {
                DungeonLobby lobby = new DungeonLobby(arg, dungeon, player.getLocation().getBlock().getLocation().clone().add(new Vector(0.5, 0.1, 0.5)));
                getDungeonLobbyDirector().getObjectManager().addObject(arg, lobby);
                sender.sendMessage("Dungeon lobby created.");
            });
            return true;
        });
        getDungeonLobbyDirector().addAdminChildTabCompleter(executorData -> {
            String[] args = executorData.args();
            List<String> list = new ArrayList<>();
            if (args.length == 1) {
                getPlugin().getDungeonsAPI().getDungeonRegistry().entrySet().forEach(entry -> {
                    list.add(entry.getKey());
                });
            }
            return list;
        });
        addManager("Dungeon", new DungeonManager(this));
        addManager("Listener", new ListenerManager(this));
    }

    @Override
    public WolfDungeonsXL getPlugin() {
        return (WolfDungeonsXL) super.getPlugin();
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
