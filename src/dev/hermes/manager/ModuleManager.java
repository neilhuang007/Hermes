package dev.hermes.manager;


import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.EventKey;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.utils.interfaces.InstanceAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Patrick
 * @since 10/19/2021
 */
public final class ModuleManager extends ArrayList<Module> implements InstanceAccess {


    public static List<Module> modules = new ArrayList<>();;
    /**
     * Called on client start and when for some reason when we reinitialize (idk custom modules?)
     */
    public void init() {

        // Automatic initializations
        this.stream().filter(module -> module.getModuleInfo().autoEnabled()).forEach(module -> module.setEnabled(true));

        // Has to be a listener to handle the key presses
        EventManager.register(this);
    }

    public List<Module> getAll() {
        return new ArrayList<>(this);
    }

    public <T extends Module> T get(final String name) {
        // noinspection unchecked
        return (T) modules.stream()
                .filter(module -> module.getDisplayName().equalsIgnoreCase(name.replace(" ", "")))
                .findAny().orElse(null);
    }

    public <T extends Module> T get(final Class<T> clazz) {
        // noinspection unchecked
        return (T) this.stream()
                .filter(module -> module.getClass() == clazz)
                .findAny().orElse(null);
    }

    public List<Module> get(final Category category) {
        return this.stream()
                .filter(module -> module.getModuleInfo().category() == category)
                .collect(Collectors.toList());
    }

    @EventTarget
    public void onKey(EventKey key) {

        if (mc.currentScreen != null) {
            return;
        }

        this.stream()
                .filter(module -> module.getModuleInfo().keyBind() == key.getKey())
                .forEach(Module::toggle);
    }

    @Override
    public boolean add(final Module module) {
        final boolean result = super.add(module);
        this.updateArraylistCache();
        return result;
    }

    @Override
    public void add(final int i, final Module module) {
        super.add(i, module);
        this.updateArraylistCache();
    }

    @Override
    public Module remove(final int i) {
        final Module result = super.remove(i);
        this.updateArraylistCache();
        return result;
    }

    @Override
    public boolean remove(final Object o) {
        final boolean result = super.remove(o);
        this.updateArraylistCache();
        return result;
    }


    private void updateArraylistCache() {
        System.out.println("Update Web Interface");
    }
}