package us.mytheria.wolfdungeonsxl.director;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import us.mytheria.bloblib.managers.BlobPlugin;
import us.mytheria.wolfdungeonsxl.entities.Party;

public class ConfigManager extends WDManager {
    private String partyName;

    public ConfigManager(WDManagerDirector director) {
        super(director);
        BlobPlugin main = getPlugin();
        main.reloadConfig();
        main.saveDefaultConfig();
        main.getConfig().options().copyDefaults(true);
        main.saveConfig();
    }

    @Override
    public void reload() {
        FileConfiguration config = getPlugin().getConfig();
        if (!config.isString("DungeonLobby.Party-Name"))
            throw new IllegalArgumentException("'DungeonLobby.Party-Name' is not set! (File: " + config.getName() + ")");
        this.partyName = config.getString("DungeonLobby.Party-Name");
    }

    /**
     * Will rename a party based on the config
     *
     * @param party Party to rename
     * @return The party name :)
     */
    public String nameParty(Party party) {
        Player owner = party.getLeaderPlayer();
        if (owner == null)
            throw new IllegalArgumentException("Party owner is null! Contact anjoismysign!");
        return partyName.replace("%party%", owner.getName());
    }
}
