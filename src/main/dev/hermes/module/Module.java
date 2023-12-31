package dev.hermes.module;

import dev.hermes.Hermes;
import dev.hermes.event.EventManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

@Getter
@Setter
public class Module {
    public Minecraft mc = Minecraft.getMinecraft();
    public String moduleName;
    public ModuleType moduleType;
    public int keybinding = 0;
    public boolean toggled;

    public Module(String moduleName, ModuleType moduleType) {
        this.moduleName = moduleName;
        this.moduleType = moduleType;
        this.toggled = false;
    }

    public Module(String moduleName, ModuleType moduleType, int keybinding) {
        this.moduleName = moduleName;
        this.moduleType = moduleType;
        this.toggled = false;
        this.keybinding = keybinding;
    }

    public Module(String moduleName, ModuleType moduleType, boolean toggled) {
        this.moduleName = moduleName;
        this.moduleType = moduleType;
        this.toggled = toggled;
    }

    public Module(String moduleName, ModuleType moduleType, int keybinding, boolean toggled) {
        this.moduleName = moduleName;
        this.moduleType = moduleType;
        this.toggled = toggled;
        this.keybinding = keybinding;
    }

    public void onInitialize() {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void toggle() {
        try {
            if (toggled) {
                toggled = false;
                EventManager.unregister(this);
                onDisable();
            } else {
                toggled = true;
                EventManager.register(this);
                onEnable();
            }
            mc.thePlayer.playSound("random.click", 0.5F, 1F);
            // ClientUtils.showNotification("Module", moduleName + " was " + (toggled ? "Enabled" : "Disabled"));
        } catch (Exception exception) {
            if (Hermes.DEVELOPMENT_SWITCH) {
                exception.printStackTrace();
            }
        }
    }
}
