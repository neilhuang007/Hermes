package dev.hermes.module.impl.devapi;

import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.Motion.EventPreMotion;
import dev.hermes.event.events.impl.render.EventRender2D;
import dev.hermes.event.events.impl.render.EventRender3D;
import dev.hermes.event.events.impl.world.EventGuiChange;
import dev.hermes.event.events.impl.world.EventTick;
import dev.hermes.event.events.impl.world.EventUpdate;
import dev.hermes.event.events.impl.world.EventWorldChange;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.utils.projection.ProjectionUtil;
import net.minecraft.client.gui.ScaledResolution;

@Hermes
@ModuleInfo(name = "Apimodule", description = "for Api and Dev use", category = Category.API, autoEnabled = true, allowDisable = false)
public class ApiModule extends Module {

    ScaledResolution scaledResolution = new ScaledResolution(mc);
    final int factor = scaledResolution.getScaleFactor();

    @EventTarget
    public void onTick(EventTick eventTick) {
//        RenderManager.refreshrenderer();
//        RenderManager.on();
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
//        RenderManager.CheckCanvas();
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
//        System.out.println("Rendering 3D event from api center");
    }

    @EventTarget
    public void onGuiChange(EventGuiChange event) {

    }

    @EventTarget
    public void onWorldChange(EventWorldChange eventWorldChange){
//        System.out.println("World Change");
//        RenderManager.CheckWindow();
    }

    @EventTarget
    public void onPreMotion(EventPreMotion event) {
//        RotationComponent.onPreMotion(event);
    }

    @EventTarget
    public void onPreUpdate(EventUpdate event){

    }

}
