package dev.hermes.module.impl.render;

import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.client.EventKey;
import dev.hermes.event.events.impl.render.EventRender3D;
import dev.hermes.event.events.impl.world.EventTick;
import dev.hermes.manager.RenderManager;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.module.value.impl.BooleanValue;
import dev.hermes.module.value.impl.ColorValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

@Hermes
@ModuleInfo(name = "CombatSettings", description = "=Various Combat Settings", category = Category.RENDER)
public class CombatSettings extends Module {

    public boolean isSprinting = false;

    private final BooleanValue NoHurtCam = new BooleanValue("No Hurt Cam", this, true);

    private final BooleanValue IsHitColor = new BooleanValue("Render Hit Color", this, true);

    private final ColorValue HitColor = new ColorValue("Hit Color", this, new Color(30, 164, 147, 190), () -> IsHitColor.getValue());

    private final BooleanValue alwaysSharp = new BooleanValue("Always Sharp", this, true);

    private final BooleanValue ToggleSprint = new BooleanValue("Toggle Sprint", this, true);

    private final BooleanValue FullBright = new BooleanValue("Full Bright", this, true);

    @EventTarget
    public void onKey(EventKey event) {
        if (event.getKey() == Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode()) {
            isSprinting = !isSprinting;
        }
    }

    @EventTarget
    public void onTick(EventTick eventTick) {
        if (isSprinting && ToggleSprint.getValue()) {
            Minecraft.getMinecraft().gameSettings.keyBindSprint.setPressed(true);
        }else{
            Minecraft.getMinecraft().gameSettings.keyBindSprint.setPressed(false);
        }
        if(FullBright.getValue()){
            Minecraft.getMinecraft().gameSettings.gammaSetting = 100;
        }
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        if(ToggleSprint.getValue()){
            // Change the color of the rectangle to a more appealing color (e.g., dark gray)
            Color rectangleColor = new Color(50, 50, 50, 98);

            // Increase the size of the rectangle to make it more visible
            double rectangleWidth = 100;
            double rectangleHeight = 20;

            // Change the font, size, and color of the text to make it more readable
            Font textFont = new Font("Arial", Font.BOLD, 40);
            Color textColor = Color.white;



            // Draw the rectangle and the text
            RenderManager.roundedRectangle("Sprint-indecator", 2, 50, rectangleWidth, rectangleHeight, 2, 2, rectangleColor);
            RenderManager.drawString("sprint-text", isSprinting ? "Sprinting" : "NotSprinting", 4, 50, textColor, textFont);
        }
    }

}
