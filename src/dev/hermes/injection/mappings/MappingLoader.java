package dev.hermes.injection.mappings;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MappingLoader {
    private static final Map<String, String> srgMappings = new HashMap<>();

    private static final Map<String, String> fieldMappings = new HashMap<>();


    public static void main(String[] args) {
        loadMappings(); // Load your mappings from the properties file

        // Testing the method with a nested class
        String testObfuscatedNestedName = "wb$b"; // Obfuscated name of a nested class
        System.out.println("Deobfuscated class name for " + testObfuscatedNestedName + ": " + getDeobfuscatedName(testObfuscatedNestedName));
        System.out.println("Deobfuscated full name for " + testObfuscatedNestedName + ": " + getDeobfuscatedNameWithClass(testObfuscatedNestedName));
    }


    public static void loadMappings() {
        String mappingsPath = "/assets/hermes/mappings/client_deobf.properties";
        try (InputStream inputStream = MappingLoader.class.getResourceAsStream(mappingsPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            if (inputStream == null) {
                System.err.println("Mappings file not found: " + mappingsPath);
                return;
            }
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("C:")) {
                    String[] parts = line.substring(2).split("=", 2); // Skip "C:" and split
                    if (parts.length == 2) {
                        String obfName = parts[0];
                        String deobfName = parts[1];
                        srgMappings.put(obfName, deobfName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load SRG mappings.");
        }
    }

    public static String getDeobfuscatedName(String obfuscatedName) {
        // Attempt to directly look up the obfuscated name including the nested class specifier
        String deobfuscatedFullName = srgMappings.getOrDefault(obfuscatedName, obfuscatedName);

        // Split the deobfuscated name to get the base class part only, ignoring the nested class specifier
        String[] parts = deobfuscatedFullName.split("\\$");
        return parts[0]; // Return only the base class part
    }


    public static String getDeobfuscatedNameWithClass(String obfuscatedName) {
        // Direct lookup including nested class specifier
        return srgMappings.getOrDefault(obfuscatedName, obfuscatedName);
    }



    private static void printMappings() {
        System.out.println("SRG Mappings:");
        srgMappings.forEach((obf, deobf) -> System.out.println(obf + " : " + deobf));
    }
}
