package dev.hermes.module.impl.render;

import dev.hermes.api.Hermes;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import net.minecraft.crash.CrashReport;

@Hermes
@ModuleInfo(name = "Crasher", description = "Crashes the server", category = Category.RENDER)
public class Crasher extends Module {
    @Override
    public void onEnable() {
        mc.crashed(new CrashReport("Error", new Exception("Error")));
    }
}
