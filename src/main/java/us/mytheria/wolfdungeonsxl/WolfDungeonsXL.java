package us.mytheria.wolfdungeonsxl;

import de.erethon.dungeonsxl.api.DungeonsAPI;
import org.bukkit.Bukkit;
import us.mytheria.bloblib.managers.BlobPlugin;
import us.mytheria.wolfdungeonsxl.director.WDManagerDirector;

public final class WolfDungeonsXL extends BlobPlugin {
    private WDManagerDirector director;
    private DungeonsAPI dungeonsAPI;

    @Override
    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("DungeonsXL")) {
            Bukkit.getLogger().severe("DungeonsXL not found! Disabling WolfDungeonsXL...");
            return;
        }
        dungeonsAPI = (DungeonsAPI) Bukkit.getPluginManager().getPlugin("DungeonsXL");
        director = new WDManagerDirector(this);
    }

    @Override
    public void onDisable() {
        director.unload();
    }

    @Override
    public WDManagerDirector getManagerDirector() {
        return director;
    }

    /**
     * Get the DungeonsAPI instance
     *
     * @return DungeonsAPI instance
     */
    public DungeonsAPI getDungeonsAPI() {
        return dungeonsAPI;
    }
}
