import net.minecraft.client.main.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Start {
    public static void main(String[] args) {
        try {
//            // Assuming the natives are packaged inside the JAR
//            String nativesPath = "assets/hermes/natives/";
//            loadNativeLibraries(nativesPath);

            Main.main(concat(new String[] {"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}"}, args));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadNativeLibraries(String nativesPath) throws IOException {
        // Get the path to the running JAR file
        String jarPath = Start.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File jarFile = new File(jarPath);

        // Create a temporary directory to extract the DLL files
        File tempDir = Files.createTempDirectory("natives").toFile();
        tempDir.deleteOnExit();

        // Open the JAR file
        try (ZipFile jar = new ZipFile(jarFile)) {
            Enumeration<? extends ZipEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                // Check if the entry is inside the nativesPath directory and is a DLL file
                if (name.startsWith(nativesPath) && name.endsWith(".dll")) {
                    extractAndLoadLibrary(jar, entry, tempDir);
                }
            }
        }
    }

    private static void extractAndLoadLibrary(ZipFile jar, ZipEntry entry, File tempDir) throws IOException {
        File libFile = new File(tempDir, new File(entry.getName()).getName());
        // Extract the DLL file from the JAR to the temporary directory
        try (InputStream is = jar.getInputStream(entry); FileOutputStream os = new FileOutputStream(libFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
        // Load the extracted DLL file
        System.load(libFile.getAbsolutePath());
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
