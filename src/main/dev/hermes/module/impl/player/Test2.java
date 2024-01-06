package dev.hermes.module.impl.player;

import dev.hermes.module.Module;
import dev.hermes.module.Catagory;

public class Test2 extends Module {
    public Test2() {
        super("Test 2", Catagory.Player);
    }

    @Override
    public void onInitialize() {
        System.out.print("Dev Test!");
    }
}
