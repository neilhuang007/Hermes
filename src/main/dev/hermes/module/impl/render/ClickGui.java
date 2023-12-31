package dev.hermes.module.impl.render;

import dev.hermes.module.Module;
import dev.hermes.module.ModuleType;
import dev.hermes.utils.log.LogUtil;
import org.lwjgl.input.Keyboard;

import java.util.logging.Logger;

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
        LogUtil.printlog("ClickGui");
        super.onEnable();

    }

    @Override
    public void onDisable() {
        System.out.println("ClickGui");
        super.onDisable();
    }
}
