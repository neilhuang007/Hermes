package dev.hermes;

import dev.hermes.module.api.manager.ModuleManager;
import dev.hermes.server.HermesServer;
import dev.hermes.utils.client.log.LogUtil;
import dev.hermes.utils.file.FileManager;
import dev.hermes.utils.file.FileType;
import dev.hermes.utils.file.config.ConfigFile;
import dev.hermes.utils.file.config.ConfigManager;
import dev.hermes.utils.file.data.DataManager;
import dev.hermes.utils.localization.Locale;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.io.IOException;

@Getter
public class Hermes {
    public final static String NAME = "Hermes";
    public final static String VERSION = "1.0";
    public final static String VERSION_FULL = "1.0"; // Used to give more detailed build info on beta builds
    public final static String VERSION_DATE = "June 5, 2023";
    public static boolean DEVELOPMENT_SWITCH = true;
    public static ModuleManager moduleManager = new ModuleManager();

    private static DataManager dataManager = new DataManager();

    private static FileManager fileManager = new FileManager();

    private static ConfigManager configManager = new ConfigManager();

    private static ConfigFile configFile;

    @Setter
    public static Locale locale = Locale.EN_US; // The language of the client

    public static void initHermes() {
        // Init
        Minecraft mc = Minecraft.getMinecraft();
        Display.setTitle(NAME + " " + VERSION + " | " + VERSION_DATE);

        fileManager.init();
        configManager.init();
        moduleManager.init();

        // Compatibility
        mc.gameSettings.guiScale = 2;
        mc.gameSettings.ofFastRender = false;
        mc.gameSettings.ofShowGlErrors = DEVELOPMENT_SWITCH;

        // Performance
        mc.gameSettings.ofSmartAnimations = true;
        mc.gameSettings.ofSmoothFps = false;
        mc.gameSettings.ofFastMath = false;

        // client geting and stuff


        // this is the server stuff

        try {
            HermesServer.start();
        } catch (IOException e) {
            LogUtil.printLog("Failed to initialize server");
        }




        // read config
        final File file = new File(ConfigManager.CONFIG_DIRECTORY, "latest.json");
        configFile = new ConfigFile(file, FileType.CONFIG);
        configFile.allowKeyCodeLoading();
        configFile.read();


    }

    public static void stopHermes() {
        configFile.write();
    }
}
