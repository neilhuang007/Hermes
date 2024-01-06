package dev.hermes.module.impl.misc;

import dev.hermes.module.Module;
import dev.hermes.module.Catagory;

public class Test4 extends Module {
    public Test4() {
        super("Test 4", Catagory.Misc);
    }

    @Override
    public void onInitialize() {
        System.out.print("Dev Test!");
    }
}
