package dev.hermes;

import net.minecraft.client.Minecraft;

public class Hermes {

    public static String NAME = "Razer";
    public static String VERSION = "1.0";
    public static String VERSION_FULL = "1.0"; // Used to give more detailed build info on beta builds
    public static String VERSION_DATE = "June 5, 2023";

    public static boolean DEVELOPMENT_SWITCH = true;

    public static void initHermes(){

        // Init
        Minecraft mc = Minecraft.getMinecraft();


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