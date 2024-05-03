package dev.hermes.module.impl.devapi;

import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.world.EventUpdate;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;

@Hermes
@ModuleInfo(name = "Data Collection", description = "Collects data from the game", category = Category.API, hidden = true, autoEnabled = true, allowDisable = false)
public class DataCollection extends Module {
    @EventTarget
    public void onPreUpdate(EventUpdate eventUpdate){
//        System.out.println(mc.thePlayer.rotationYaw + " " + mc.thePlayer.rotationPitch);
    }
}
