package us.mytheria.wolfdungeonsxl.director;

import org.bukkit.event.Listener;

public class WDListener implements Listener {
    private final ListenerManager listenerManager;

    public WDListener(ListenerManager listenerManager) {
        this.listenerManager = listenerManager;
    }

    /**
     * Get the ListenerManager that this listener is registered to.
     *
     * @return The ListenerManager that this listener is registered to.
     */
    public ListenerManager getListenerManager() {
        return listenerManager;
    }
}
