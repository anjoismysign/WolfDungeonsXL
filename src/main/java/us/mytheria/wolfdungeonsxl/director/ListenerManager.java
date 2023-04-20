package us.mytheria.wolfdungeonsxl.director;

import us.mytheria.wolfdungeonsxl.listeners.OnJoin;
import us.mytheria.wolfdungeonsxl.listeners.OnKick;
import us.mytheria.wolfdungeonsxl.listeners.OnLeave;

public class ListenerManager extends WDManager {
    public ListenerManager(WDManagerDirector managerDirector) {
        super(managerDirector);
        new OnJoin(this);
        new OnLeave(this);
        new OnKick(this);
    }
}
