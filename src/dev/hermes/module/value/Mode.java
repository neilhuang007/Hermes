package dev.hermes.module.value;


import dev.hermes.manager.EventManager;
import dev.hermes.utils.interfaces.InstanceAccess;
import dev.hermes.utils.interfaces.Toggleable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Rewritten from Patricks old version to be less retarded
 * @author Hazsi
 * @since 10/10/2022
 */
@Getter
@RequiredArgsConstructor
public abstract class Mode<T> implements InstanceAccess, Toggleable {
    private final String name;
    private final T parent;
    private final List<Value<?>> values = new ArrayList<>();

    public final void register() {
        EventManager.register(this);
        this.onEnable();
    }

    public final void unregister() {
        EventManager.unregister(this);
        this.onDisable();
    }

    @Override
    public void toggle() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }
}