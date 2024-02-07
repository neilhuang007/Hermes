package dev.hermes.module.impl.devapi;

import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.EventRender2D;
import dev.hermes.event.events.impl.EventTick;
import dev.hermes.manager.RenderManager;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.utils.projection.ProjectionUtil;

@Hermes
@ModuleInfo(name = "Apimodule", description = "for Api and Dev use", category = Category.API, autoEnabled = true, allowDisable = false)
public class ApiModule extends Module {
    RenderManager renderManager = new RenderManager();

    @EventTarget
    public void onTick(EventTick eventTick) {
        renderManager.Tick();

    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        ProjectionUtil.Render2D(event);
    }
}
