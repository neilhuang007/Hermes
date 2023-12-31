package dev.hermes.manager;

import dev.hermes.event.EventManager;
import dev.hermes.module.Module;
import dev.hermes.module.impl.misc.Test4;
import dev.hermes.module.impl.player.Test2;
import dev.hermes.module.impl.render.ClickGui;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModuleManager {
    public MultiValuedMap<Integer, Module> keyBinds = new ArrayListValuedHashMap<>();
    ArrayList<Module> modules = new ArrayList<>();
    List<Module> moduleList = new ArrayList<>();

    public void registerModules() {
        moduleList.add(new Test2());
        moduleList.add(new Test4());
        moduleList.add(new ClickGui());

        registerModuleByList(moduleList.stream().sorted(Comparator.comparingInt(module -> module.getModuleName().length())).sorted(Comparator.comparingInt(s -> s.getModuleName().charAt(0))).sorted(Comparator.comparingInt(module -> module.getModuleType().ordinal())).collect(Collectors.toList()));
    }

    public void registerModule(Module module) {
        modules.add(module);
        if (module.getKeybinding() != 0) {
            keyBinds.put(module.getKeybinding(), module);
        }

        System.out.print("Initialize module: " + module.moduleName);
        module.onInitialize();
        System.out.print("Finished initialize module: " + module.moduleName);
    }

    public void registerModuleByList(List<Module> moduleList) {
        for (Module module : moduleList) {
            registerModule(module);
        }
    }

    public Module getModule(String name) {
        for (Module i : modules) {
            if (i.moduleName.equalsIgnoreCase(name)) {
                return i;
            }
        }
        return null;
    }

    public final Module getModule(final Class<? extends Module> cls) {
        for (Module i : modules) {
            if (i.getClass().equals(cls)) {
                return i;
            }
        }
        return null;
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public void setKeybind(Module module, int key) {
        boolean hasIt = false;
        MultiValuedMap<Integer, Module> newMap = new ArrayListValuedHashMap<>();
        for (Map.Entry<Integer, Module> entry : keyBinds.entries()) {
            if (entry.getValue().equals(module)) {
                newMap.put(key, entry.getValue());
                hasIt = true;
            } else {
                newMap.put(entry.getKey(), entry.getValue());
            }
        }
        if (!hasIt) {
            newMap.put(key, module);
        }
        module.keybinding = key;
        keyBinds = newMap;
    }

    public void EventRegister() {
        for (Module module : this.getModules()) {
            if (module.isToggled()) {
                EventManager.register(module);
                System.out.print("Event register module: " + module.moduleName);
            } else {
                EventManager.unregister(module);
            }
        }
    }
}
