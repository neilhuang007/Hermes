package dev.hermes;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import dev.hermes.api.Hidden;
import dev.hermes.manager.*;
import dev.hermes.module.Module;
import dev.hermes.server.HermesServer;
import dev.hermes.ui.alt.account.AccountManager;
import dev.hermes.utils.client.log.LogUtil;
import dev.hermes.utils.file.FileManager;
import dev.hermes.utils.file.FileType;
import dev.hermes.utils.file.config.ConfigFile;
import dev.hermes.utils.file.config.ConfigManager;
import dev.hermes.utils.file.data.DataManager;
import dev.hermes.utils.reflection.ReflectionUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;

@Getter
public class Hermes {
    public final static String NAME = "Hermes";
    public final static String VERSION = "1.0";
    public final static String VERSION_FULL = "1.0"; // Used to give more detailed build info on beta builds
    public final static String VERSION_DATE = "June 5, 2023";
    public static boolean DEVELOPMENT_SWITCH = true;

    //managers
    public static ModuleManager moduleManager = new ModuleManager();

    public static DataManager dataManager = new DataManager();

    public static FileManager fileManager = new FileManager();

    public static ConfigManager configManager = new ConfigManager();

    public static RenderManager renderManager = new RenderManager();

    public static RotationManager rotationManager = new RotationManager();

    public static ConfigFile configFile;

    public static AccountManager accountManager = new AccountManager();

    public static EventManager eventManager = new EventManager();

    public static Manager manager = new Manager();



    public static void initHermes(Instrumentation inst){
        // Init
        Minecraft mc = Minecraft.getMinecraft();
        Display.setTitle(NAME + " " + VERSION + " | " + VERSION_DATE);


        // Compatibility
        mc.gameSettings.guiScale = 2;
        mc.gameSettings.ofFastRender = false;
        mc.gameSettings.ofShowGlErrors = DEVELOPMENT_SWITCH;
        mc.gameSettings.enableVsync = true;
        mc.gameSettings.ofClouds = 3;
        mc.gameSettings.fancyGraphics = false;
        mc.gameSettings.useVbo = true;
        mc.gameSettings.particleSetting = 0;
        mc.gameSettings.ofWeather = false;
        mc.gameSettings.ofFogType = 3;
        mc.gameSettings.ofSunMoon = false;
        mc.gameSettings.ofRain = 2;
        mc.gameSettings.ofDroppedItems = 1;
        mc.gameSettings.entityShadows = false;
        mc.gameSettings.ofShowCapes = false;
        mc.gameSettings.heldItemTooltips = false;
        mc.gameSettings.ofTrees = 1;
        mc.gameSettings.ofCustomEntityModels = false;
        mc.gameSettings.ofCustomSky = false;
        mc.gameSettings.ofConnectedTextures = 1;
        mc.gameSettings.ofCustomItems = false;
        mc.gameSettings.ofRandomEntities = false;
        mc.gameSettings.ofStars = false;
        mc.gameSettings.ofSky = false;
        mc.gameSettings.viewBobbing = false;
        mc.gameSettings.ofDynamicFov = false;
        mc.gameSettings.ofSmoothFps = true;

        // Performance
        mc.gameSettings.ofSmartAnimations = true;
        mc.gameSettings.ofSmoothFps = false;
        mc.gameSettings.ofFastMath = false;


        // this is the server stuff

        try {
            HermesServer.start();
        } catch (IOException e) {
            LogUtil.printLog("Failed to initialize server");
            e.printStackTrace();
        }

        // Register
        String[] paths = {
                "dev.hermes"
        };

        // needed no spaced registration path
        // somehting like c:/aa a/hacks/razor is not going to work
        LogUtil.printLog("Please Make sure the current path does not contain spaces");

        for (String path : paths) {
            if (!ReflectionUtil.dirExist(path)) {
                continue;
            }

            Class<?>[] classes = ReflectionUtil.getClassesInPackage(path);

            System.out.println("Classes: " + classes);

            for (Class<?> clazz : classes) {
                try {
                    if (clazz.isAnnotationPresent(Hidden.class)) continue;
                    if (Module.class.isAssignableFrom(clazz) && clazz != Module.class) {
                        moduleManager.add((Module) clazz.getConstructor().newInstance());
                    }

                    EventManager.register(clazz);

                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException exception) {
                    exception.printStackTrace();
                }
            }

            break;
        }

        fileManager.init();
        configManager.init();
        moduleManager.init();
        renderManager.init();
        accountManager.init();
        manager.init();


        // read config
        final File file = new File(ConfigManager.CONFIG_DIRECTORY, "latest.json");
        configFile = new ConfigFile(file, FileType.CONFIG);
        configFile.allowKeyCodeLoading();
        configFile.read();

        renderManager.initwindow();

        EventManager.register(rotationManager);


    }



    public static void stopHermes() {
        configFile.write();
    }
}
