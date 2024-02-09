package dev.hermes.module.impl.render;

import com.sun.javafx.geom.Vec3d;
import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.render.EventRender2D;
import dev.hermes.manager.RenderManager;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.utils.projection.ProjectionUtil;
import net.minecraft.tileentity.TileEntity;


import javax.vecmath.Vector4d;
import java.awt.*;

@Hermes
@ModuleInfo(name = "ChestESP", description = "Renders an ESP glow for entities", category = Category.RENDER)
public class ChestESP extends Module {

    @EventTarget
    private void onRender2D(EventRender2D event) {

        for (TileEntity entity : mc.theWorld.loadedTileEntityList) {

            Vector4d pos = ProjectionUtil.get(entity);

            if (pos == null) {
                return;
            }

            // Outer Black Border
            double outerBorderWidth = 1; // Thicker outer border
            RenderManager.drawBorderedRect(entity.getPos().toString() + "_OuterBox", pos.x - outerBorderWidth, pos.y - outerBorderWidth,
                    (pos.z - pos.x) + 2 * outerBorderWidth, (pos.w - pos.y) + 2 * outerBorderWidth,
                    outerBorderWidth, Color.BLACK.getRGB(), new Color(0, 0, 0, 0).getRGB()); // Outer border

            // Middle Cyan Border
            double middleBorderWidth = 4; // Middle cyan border
            RenderManager.drawBorderedRect(entity.getPos().toString() + "_MiddleBox", pos.x, pos.y,
                    (pos.z - pos.x), (pos.w - pos.y),
                    middleBorderWidth, new Color(180, 250, 255).getRGB(), new Color(0, 0, 0, 0).getRGB()); // Middle border

            // Inner Black Border
            double innerBorderWidth = 1; // Thinner inner border
            RenderManager.drawBorderedRect(entity.getPos().toString() + "_InnerBox", pos.x + middleBorderWidth - innerBorderWidth, pos.y + middleBorderWidth - innerBorderWidth,
                    (pos.z - pos.x) - 2 * (middleBorderWidth - innerBorderWidth), (pos.w - pos.y) - 2 * (middleBorderWidth - innerBorderWidth),
                    innerBorderWidth, Color.BLACK.getRGB(), new Color(0, 0, 0, 0).getRGB()); // Inner border


        }
    }
}
