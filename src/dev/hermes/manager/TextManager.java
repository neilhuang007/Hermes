package dev.hermes.manager;

import net.minecraft.client.Minecraft;

public class TextManager {

    private static Minecraft mc = Minecraft.getMinecraft();
    public static int getStringWidth(String text) {
        return mc.fontRendererObj.getStringWidth(text);
    }
}
