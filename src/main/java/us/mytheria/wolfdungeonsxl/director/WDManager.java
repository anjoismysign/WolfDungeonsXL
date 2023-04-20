package us.mytheria.wolfdungeonsxl.director;

import us.mytheria.bloblib.managers.Manager;
import us.mytheria.wolfdungeonsxl.WolfDungeonsXL;

public class WDManager extends Manager {
    public WDManager(WDManagerDirector managerDirector) {
        super(managerDirector);
    }

    @Override
    public WDManagerDirector getManagerDirector() {
        return (WDManagerDirector) super.getManagerDirector();
    }

    @Override
    public WolfDungeonsXL getPlugin() {
        return (WolfDungeonsXL) super.getPlugin();
    }
}
