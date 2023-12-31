package dev.hermes.manager.component;

import dev.hermes.event.EventManager;
import dev.hermes.manager.component.impl.KeybindComponent;

import java.util.ArrayList;

public class ComponentManager extends ArrayList<Component> {

    /**
     * Called on client start and when for some reason when we reinitialize
     */
    public void init() {
        this.add(new KeybindComponent());
        this.registerToEventBus();
    }

    public void registerToEventBus() {
        for (Component component : this) {
            EventManager.register(component);
        }
    }
}

