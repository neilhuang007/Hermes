package dev.hermes.module.impl.render;

import dev.hermes.module.Module;
import dev.hermes.module.ModuleType;

public class Test3 extends Module {
    public Test3() {
        super("Test 3", ModuleType.Render);
    }

    @Override
    public void onInitialize() {
        System.out.print("Dev Test!");
    }
}
