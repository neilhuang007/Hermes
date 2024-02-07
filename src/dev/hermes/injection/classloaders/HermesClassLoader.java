//package dev.hermes.injection.classloaders;
//
//import java.lang.instrument.ClassFileTransformer;
//import java.lang.instrument.IllegalClassFormatException;
//import java.security.ProtectionDomain;
//
//public class HermesClassLoader implements ClassFileTransformer {
//
//    @Override
//    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
//        if (className.startsWith("dev/hermes/")) {
//            return loadClassBytesFromJar(className);
//        }
//        return null;
//    }
//
//    private byte[] loadClassBytesFromJar(String className) {
//        String resourcePath = className + ".class";
//        try (var inputStream = HermesClassLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
//            if (inputStream == null) {
//                System.err.println("Class resource not found for: " + className);
//                return null;
//            }
//            return inputStream.readAllBytes();
//        } catch (Exception e) {
//            System.err.println("Failed to load class bytes for: " + className);
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
