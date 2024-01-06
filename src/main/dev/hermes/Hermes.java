package dev.hermes;

import dev.hermes.manager.FileManager;
import dev.hermes.manager.ModuleManager;
import dev.hermes.manager.component.ComponentManager;
import dev.hermes.module.ModuleConfig;
import dev.hermes.server.HermesServer;
import dev.hermes.utils.client.log.LogUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;

import java.io.IOException;

@Getter
public class Hermes {
    public final static String NAME = "Hermes";
    public final static String VERSION = "1.0";
    public final static String VERSION_FULL = "1.0"; // Used to give more detailed build info on beta builds
    public final static String VERSION_DATE = "June 5, 2023";
    public static boolean DEVELOPMENT_SWITCH = true;
    public static ModuleManager moduleManager = new ModuleManager();
    public static FileManager fileManager = new FileManager();
    public static ModuleConfig moduleConfig = new ModuleConfig();
    public static ComponentManager componentManager = new ComponentManager();

    public static void initHermes() {
        // Init
        Minecraft mc = Minecraft.getMinecraft();
        Display.setTitle(NAME + " " + VERSION + " | " + VERSION_DATE);

        componentManager.init();

        moduleManager.registerModules();
        moduleConfig.load(fileManager.getNConfigFile());
        moduleManager.EventRegister();

        // Compatibility
        mc.gameSettings.guiScale = 2;
        mc.gameSettings.ofFastRender = false;
        mc.gameSettings.ofShowGlErrors = DEVELOPMENT_SWITCH;

        // Performance
        mc.gameSettings.ofSmartAnimations = true;
        mc.gameSettings.ofSmoothFps = false;
        mc.gameSettings.ofFastMath = false;

        // this is the server stuff

        try {
            HermesServer.start();
        } catch (IOException e) {
            LogUtil.printLog("Failed to initialize server");
        }

    }

    public static void stopHermes() {
        moduleConfig.save(fileManager.getNConfigFile());
    }
}
