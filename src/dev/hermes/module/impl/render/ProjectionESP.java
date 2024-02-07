package dev.hermes.module.impl.render;

import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.EventRender2D;
import dev.hermes.manager.RenderManager;
import dev.hermes.module.Module;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.utils.interfaces.InstanceAccess;
import dev.hermes.utils.projection.ProjectionUtil;
import dev.hermes.utils.vector.Vector2d;
import dev.hermes.value.impl.BooleanValue;
import net.minecraft.entity.player.EntityPlayer;

import javax.vecmath.Vector4d;
import java.awt.*;

@Hermes
@ModuleInfo(name = "ProjectionESP", description = "Draws a 2D box around entities", category = dev.hermes.module.api.Category.RENDER)
public class ProjectionESP extends Module {

    public BooleanValue glow = new BooleanValue("Glow", this, true);

    @EventTarget
    public void onRender2D(EventRender2D event) {
        System.out.println("Rendering 2D");
        System.out.println("Player: " + InstanceAccess.mc.theWorld.playerEntities);
        for (EntityPlayer player : InstanceAccess.mc.theWorld.playerEntities) {
            if (InstanceAccess.mc.getRenderManager() == null || player == InstanceAccess.mc.thePlayer ||
                    !RenderManager.isInViewFrustrum(player) || player.isDead || player.isInvisible()) {
                System.out.println("Player is dead or invisible");
                continue;
            }

            Vector4d pos = ProjectionUtil.get(player);

            System.out.println(pos);

            if (pos == null) {
                return;
            }

            System.out.println("posx: " + pos.x + " posy: " + pos.y + " posz: " + pos.z + " posw: " + pos.w);

            // Black outline
            RenderManager.rectangle(pos.x, pos.y, pos.z - pos.x, 1.5, Color.BLACK); // Top
            RenderManager.rectangle(pos.x, pos.y, 1.5, pos.w - pos.y + 1.5, Color.BLACK); // Left
            RenderManager.rectangle(pos.z, pos.y, 1.5, pos.w - pos.y + 1.5, Color.BLACK); // Right
            RenderManager.rectangle(pos.x, pos.w, pos.z - pos.x, 1.5, Color.BLACK); // Bottom

            // Main ESP
            Runnable runnable = () -> {

                final Vector2d first = new Vector2d(0, 0), second = new Vector2d(0, 500);

                RenderManager.horizontalGradient(pos.x + 0.5, pos.y + 0.5, pos.z - pos.x, 0.5, // Top
                        new Color(185, 250, 255), new Color(79, 199, 200));
                RenderManager.verticalGradient(pos.x + 0.5, pos.y + 0.5, 0.5, pos.w - pos.y + 0.5, // Left
                        new Color(185, 250, 255), new Color(79, 199, 200));
                RenderManager.verticalGradient(pos.z + 0.5, pos.y + 0.5, 0.5, pos.w - pos.y + 0.5, // Right
                        new Color(79, 199, 200), new Color(185, 250, 255));
                RenderManager.horizontalGradient(pos.x + 0.5, pos.w + 0.5, pos.z - pos.x, 0.5, // Bottom
                        new Color(79, 199, 200), new Color(185, 250, 255));
            };

            runnable.run();
            if (this.glow.getValue()) {
                RenderManager.rectangle(pos.x - 1, pos.y - 1, pos.z - pos.x + 2, pos.w - pos.y + 2, new Color(0, 0, 0, 0));
                RenderManager.rectangle(pos.x - 2, pos.y - 2, pos.z - pos.x + 4, pos.w - pos.y + 4, new Color(0, 0, 0, 0));
                RenderManager.rectangle(pos.x - 3, pos.y - 3, pos.z - pos.x + 6, pos.w - pos.y + 6, new Color(0, 0, 0, 0));
                RenderManager.rectangle(pos.x - 4, pos.y - 4, pos.z - pos.x + 8, pos.w - pos.y + 8, new Color(0, 0, 0, 0));
            }
        }
    }
}
