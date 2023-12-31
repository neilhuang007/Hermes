package dev.hermes.manager.component.impl;

import dev.hermes.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.EventKey;
import dev.hermes.manager.component.Component;
import dev.hermes.module.Module;

import java.util.Map;

public class KeybindComponent extends Component {
    @EventTarget
    public void onKey(EventKey key) {
        if (mc.currentScreen != null) {
            return;
        }
        if (Hermes.moduleManager.keyBinds.containsKey(key.getKey())) {
            for (Map.Entry<Integer, Module> entry : Hermes.moduleManager.keyBinds.entries()) {
                if (entry.getKey().equals(key.getKey())) {
                    entry.getValue().toggle();
                }
            }
        }
    }
}