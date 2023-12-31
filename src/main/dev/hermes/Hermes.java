package dev.hermes;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;

public class Hermes {
    public final static String NAME = "Hermes";
    public final static String VERSION = "1.0";
    public final static String VERSION_FULL = "1.0"; // Used to give more detailed build info on beta builds
    public final static String VERSION_DATE = "June 5, 2023";

    public static boolean DEVELOPMENT_SWITCH = true;

    public static void initHermes(){
        // Init
        Minecraft mc = Minecraft.getMinecraft();
        Display.setTitle(NAME + " " + VERSION + " | " + VERSION_DATE);

        // Compatibility
        mc.gameSettings.guiScale = 2;
        mc.gameSettings.ofFastRender = false;
        mc.gameSettings.ofShowGlErrors = DEVELOPMENT_SWITCH;

        // Performance
        mc.gameSettings.ofSmartAnimations = true;
        mc.gameSettings.ofSmoothFps = false;
        mc.gameSettings.ofFastMath = false;

    }
}
