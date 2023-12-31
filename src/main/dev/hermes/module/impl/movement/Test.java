package dev.hermes.module.impl.movement;

import dev.hermes.module.Module;
import dev.hermes.module.ModuleType;

public class Test extends Module {
    public Test() {
        super("Test", ModuleType.Movement);
    }

    @Override
    public void onInitialize() {
        System.out.print("Dev Test!");
    }
}
