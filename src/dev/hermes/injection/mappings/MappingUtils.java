package dev.hermes.injection.mappings;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Getter
public class MappingUtils {
    @Getter
    public static final Set<String> targetedClasses = new HashSet<>();
    @Getter
    private static Map<String, String> classMappings = new HashMap<>();
    @Getter
    private static Map<String, String> reverseClassMappings = new HashMap<>();
    @Getter
    private static Map<String, String> fieldMappings = new HashMap<>();
    @Getter
    private static Map<String, String> reverseFieldMappings = new HashMap<>();
    @Getter
    private static Map<String, String> methodMappings = new HashMap<>();
    @Getter
    private static Map<String, String> reverseMethodMappings = new HashMap<>();
    @Getter
    private static Map<String, String> packageMappings = new HashMap<>();
    @Getter
    private static Map<String, String> reversePackageMappings = new HashMap<>();
    @Getter
    private static final Map<String, String> funcMappings = new HashMap<>();
    @Getter
    private static final Map<String, String> reversefuncMappings = new HashMap<>();

    public static void init(){

        // Populate the set with deobfuscated names of classes you want to replace
        targetedClasses.add("net.minecraft.client.gui.ScaledResolution");
        targetedClasses.add("net.minecraft.client.Minecraft");
        targetedClasses.add("net.minecraft.network.Packet");
        targetedClasses.add("net.minecraft.client.main.Main");
        targetedClasses.add("net.minecraft.block.BlockGrass");
        readMappings("/assets/hermes/mappings/mcp/client_rg.srg");
    }

    //
    public static void readMappings(String relativeFilePath) {
        try (InputStream inputStream = MappingLoader.class.getResourceAsStream(relativeFilePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            if (inputStream == null) {
                System.err.println("Mappings file not found: " + relativeFilePath);
                return;
            }
            String line;
            while ((line = reader.readLine()) != null) {
                // Process each line.
                String[] parts = line.split(" ");
                if (parts.length < 3) continue;

                String type = parts[0];
                String obfuscatedName = parts[1];
                String deobfuscatedName = parts[2];

                switch (type) {
                    case "CL:":
                        if(obfuscatedName.contains("$")){
                            funcMappings.put(obfuscatedName, deobfuscatedName);
                            reversefuncMappings.put(deobfuscatedName, obfuscatedName);
                            obfuscatedName = obfuscatedName.split("\\$")[0];
                            deobfuscatedName = deobfuscatedName.split("\\$")[0];
                        }
                        classMappings.put(obfuscatedName, deobfuscatedName);
                        reverseClassMappings.put(deobfuscatedName, obfuscatedName);
                        break;
                    case "FD:":
                        fieldMappings.put(obfuscatedName, deobfuscatedName);
                        reverseFieldMappings.put(deobfuscatedName, obfuscatedName);
                        break;
                    case "MD:":
                        methodMappings.put(obfuscatedName, deobfuscatedName);
                        reverseMethodMappings.put(deobfuscatedName, obfuscatedName);
                        break;
                    case "PK:":
                        packageMappings.put(obfuscatedName, deobfuscatedName);
                        reversePackageMappings.put(deobfuscatedName, obfuscatedName);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Boolean IsTargetedClass(String obfuscatedName) {
        try{
            if(targetedClasses.contains(classMappings.get(obfuscatedName).replace('/', '.'))){
                System.out.println("Targeted class found: " + classMappings.get(obfuscatedName));
                return targetedClasses.contains(classMappings.get(obfuscatedName).replace('/', '.'));
            }else{
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }


    public static String getDeobfuscatedName(String obfuscatedName) {
        try (InputStream inputStream = MappingLoader.class.getResourceAsStream("/assets/hermes/mappings/mcp/client_rg.srg");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            if (inputStream == null) {
                System.err.println("Mappings file not found: " + "/assets/hermes/mappings/mcp/client_rg.srg");
                return null;
            }
            String deobfname = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(obfuscatedName)) {
                    String[] parts = line.split(" ");
                    deobfname = parts[parts.length - 1].split("/")[parts[parts.length - 1].split("/").length - 1];
                }
            }
            return deobfname;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        init(); // Initialize the mappings

        System.out.println("Deobfuscated class name for net/minecraft/stats/StatBase/func_75966_h: " + getDeobfuscatedName("func_75966_h"));
    }

}

