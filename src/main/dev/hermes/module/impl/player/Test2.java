package dev.hermes.module.impl.player;

import dev.hermes.module.Module;
import dev.hermes.module.ModuleType;

public class Test2 extends Module {
    public Test2() {
        super("Test 2", ModuleType.Player);
    }

    @Override
    public void onInitialize() {
        System.out.print("Dev Test!");
    }
}
