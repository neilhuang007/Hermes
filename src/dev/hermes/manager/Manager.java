package dev.hermes.manager;

import dev.hermes.utils.interfaces.InstanceAccess;

import java.util.ArrayList;

public class Manager extends ArrayList<Manager> implements InstanceAccess {
    /**
     * Called on client start and when for some reason when we reinitialize
     */
    public void init() {
        this.registerToEventBus();
    }

    public void registerToEventBus() {
        for (final Manager component : this) {
            EventManager.register(component);
            if (component instanceof Manager) {
                ((Manager) component).registerToEventBus();
            }
        }
    }

}
