//package dev.hermes.injection;
//
//import dev.hermes.injection.annotations.Inject;
//import dev.hermes.injection.annotations.Modify;
//import dev.hermes.injection.classloaders.MinecraftClassLoader;
//import dev.hermes.injection.operations.BytecodeUtil;
//import org.objectweb.asm.*;
//import java.lang.instrument.Instrumentation;
//import java.lang.instrument.ClassDefinition;
//import java.io.IOException;
//import java.io.InputStream;
//
//public class HermesAgent {
//
//    public static void agentmain(String agentArgs, Instrumentation inst) {
//        System.out.println("HermesAgent attached.");
//
//        // Assuming MinecraftClassLoader and MappingLoader are correctly implemented
//        MinecraftClassLoader minecraftClassLoader = new MinecraftClassLoader();
//
//        for (Class<?> clazz : inst.getAllLoadedClasses()) {
//            String className = clazz.getName().replace('.', '/');
//            if (minecraftClassLoader.isTargetedClass(className)) {
//                String deobfClassName = MappingLoader.getDeobfuscatedName(className).replace('/', '.');
//                System.out.println("Found targeted class: " + deobfClassName);
//
//                // Load bytecode of your version of the class
//                byte[] yourClassBytes = BytecodeUtil.loadClassBytes(deobfClassName);
//                if (yourClassBytes != null) {
//                    // Scan for annotations
//                    BytecodeUtil.scanAndPrintAnnotatedMethods(yourClassBytes);
//                }
//            }
//        }
//    }
//}
//
//
