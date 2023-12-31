package dev.hermes.module.impl.render;

import dev.hermes.module.Module;
import dev.hermes.module.ModuleType;
import org.lwjgl.input.Keyboard;

public class ClickGui extends Module {
    public ClickGui() {
        super("ClickGui", ModuleType.Render, Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
