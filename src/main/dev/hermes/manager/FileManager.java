package dev.hermes.manager;

import dev.hermes.utils.client.file.FileUtils;
import lombok.Getter;
import net.minecraft.client.Minecraft;

import java.io.File;

public class FileManager {
    private final Minecraft mc = Minecraft.getMinecraft();
    @Getter
    private final File cfgDir;
    @Getter
    private final File nConfigDir;
    @Getter
    private final File nConfigFile;

    public FileManager() {
        cfgDir = new File(mc.mcDataDir, "Hermes");
        nConfigDir = new File(cfgDir, "configs");
        nConfigFile = new File(nConfigDir, "default.hermes");

        FileUtils.createDir(cfgDir);
        FileUtils.createDir(nConfigDir);
        FileUtils.createFile(nConfigFile);
    }
}
