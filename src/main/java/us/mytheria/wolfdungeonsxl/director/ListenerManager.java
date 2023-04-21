package us.mytheria.wolfdungeonsxl.director;

import us.mytheria.wolfdungeonsxl.listeners.OnLeave;

public class ListenerManager extends WDManager {
    public ListenerManager(WDManagerDirector managerDirector) {
        super(managerDirector);
        new OnLeave(this);
    }
}
