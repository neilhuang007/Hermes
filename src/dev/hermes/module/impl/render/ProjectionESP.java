package dev.hermes.module.impl.render;

import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.render.EventRender2D;
import dev.hermes.manager.RenderManager;
import dev.hermes.module.Module;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.utils.interfaces.InstanceAccess;
import dev.hermes.utils.projection.ProjectionUtil;
import dev.hermes.utils.vector.Vector2d;
import dev.hermes.module.value.impl.BooleanValue;
import net.minecraft.entity.player.EntityPlayer;

import javax.vecmath.Vector4d;
import java.awt.*;

@Hermes
@ModuleInfo(name = "ProjectionESP", description = "Draws a 2D box around entities", category = dev.hermes.module.api.Category.RENDER)
public class ProjectionESP extends Module {

    public BooleanValue glow = new BooleanValue("Glow", this, true);

    @EventTarget
    public void onRender2D(EventRender2D event) {
        for (EntityPlayer player : InstanceAccess.mc.theWorld.playerEntities) {
            if (InstanceAccess.mc.getRenderManager() == null || player == InstanceAccess.mc.thePlayer ||
                    !RenderManager.isInViewFrustrum(player) || player.isDead || player.isInvisible()) {
                continue;
            }

            Vector4d pos = ProjectionUtil.get(player);

            if (pos == null) {
                return;
            }

            // Black outline
            RenderManager.rectangle(player.getName() + "Top", pos.x, pos.y, pos.z - pos.x, 1.5, Color.BLACK); // Top
            RenderManager.rectangle(player.getName() + "Left", pos.x, pos.y, 1.5, pos.w - pos.y + 1.5, Color.BLACK); // Left
            RenderManager.rectangle(player.getName() + "Right", pos.z, pos.y, 1.5, pos.w - pos.y + 1.5, Color.BLACK); // Right
            RenderManager.rectangle(player.getName() + "Bottom", pos.x, pos.w, pos.z - pos.x, 1.5, Color.BLACK); // Bottom

            RenderManager.horizontalGradient(player.getName() + "G_TOP", pos.x + 0.5, pos.y + 0.5, pos.z - pos.x, 0.5, // Top
                    new Color(185, 250, 255), new Color(79, 199, 200));
            RenderManager.verticalGradient(player.getName() + "G_LEFT", pos.x + 0.5, pos.y + 0.5, 0.5, pos.w - pos.y + 0.5, // Left
                    new Color(185, 250, 255), new Color(79, 199, 200));
            RenderManager.verticalGradient(player.getName() + "G_RIGHT", pos.z + 0.5, pos.y + 0.5, 0.5, pos.w - pos.y + 0.5, // Right
                    new Color(79, 199, 200), new Color(185, 250, 255));
            RenderManager.horizontalGradient(player.getName() + "G_BOTTOM", pos.x + 0.5, pos.w + 0.5, pos.z - pos.x, 0.5, // Bottom
                    new Color(79, 199, 200), new Color(185, 250, 255));
        }
    }
}