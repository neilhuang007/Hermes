//package dev.hermes.injection.classloaders;
//
//import dev.hermes.injection.MappingLoader;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.instrument.ClassFileTransformer;
//import java.lang.instrument.IllegalClassFormatException;
//import java.security.ProtectionDomain;
//import java.util.*;
//
//public class MinecraftClassLoader implements ClassFileTransformer {
//
//
//    public final Set<String> targetedClasses = new HashSet<>();
//
//    public MinecraftClassLoader() {
//        MappingLoader.loadMappings();
//        System.out.println("mappings loaded");
//
//        // Populate the set with deobfuscated names of classes you want to replace
//        targetedClasses.add("net.minecraft.client.gui.ScaledResolution");
//        targetedClasses.add("net.minecraft.client.Minecraft");
//        targetedClasses.add("net.minecraft.network.Packet");
//        targetedClasses.add("net.minecraft.client.main.Main");
//        targetedClasses.add("net.minecraft.block.BlockGrass");
//    }
//
//    // New method to check if a class name is targeted for replacement
//    public boolean isTargetedClass(String className) {
//        // Convert binary name to normal dotted class name
//        String dottedClassName = MappingLoader.getDeobfuscatedName(className).replace('/', '.');
//
//        return targetedClasses.contains(dottedClassName);
//    }
//
//    // New method to load class bytes from the agent JAR
//    public byte[] loadClassBytes(String className) throws ClassNotFoundException {
//        String resourcePath = "/" + className.replace('.', '/') + ".class";
//        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
//            if (is == null) {
//                throw new ClassNotFoundException("Class " + className + " not found in agent JAR.");
//            }
//            return is.readAllBytes();
//        } catch (IOException e) {
//            throw new ClassNotFoundException("Failed to load class bytes for " + className, e);
//        }
//    }
//
////    @Override
////    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
////        // Log the original and deobfuscated class names
////        System.out.println("Processing: " + className + " | Deobfuscated: " + MappingLoader.getDeobfuscatedName(className));
////
////        if(targetedClasses.contains(MappingLoader.getDeobfuscatedName(className).replace('/', '.'))){
////            System.out.println("Needs to modify");
////        }
////        // No transformation is applied here, just logging
////        return classfileBuffer;
////    }
//}
