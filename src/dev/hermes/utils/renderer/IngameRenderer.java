package dev.hermes.utils.renderer;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class IngameRenderer {
    /**
     * Better to use gl state manager to avoid bugs
     */
    public void start() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    /**
     * Better to use gl state manager to avoid bugs
     */
    public void stop() {
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    public static void drawBorderedRect(double x, double y, double width, double height, double lineSize, int borderColor, int color) {
        Gui.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), color);
        Gui.drawRect((int) x, (int) y, (int) (x + width), (int) (y + lineSize), borderColor);
        Gui.drawRect((int) x, (int) y, (int) (x + lineSize), (int) (y + height), borderColor);
        Gui.drawRect((int) (x + width), (int) y, (int) (x + width - lineSize), (int) (y + height), borderColor);
        Gui.drawRect((int) x, (int) (y + height), (int) (x + width), (int) (y + height - lineSize), borderColor);
    }

}
