package dev.hermes.utils;


import dev.hermes.Hermes;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectionUtil {
    public static Class<?>[] getClassesInPackage(String packageName) {
        try {

            Path destinationDirectory = Paths.get(ReflectionUtil.path() + "/building/");
            Files.createDirectories(destinationDirectory); // Create directory if it doesn't exist

            // Create a new file path in the destination directory
            Path destinationJarFilePath = destinationDirectory.resolve("Hermes.jar");

            // Copy the original JAR file to the new location
            Files.copy(Paths.get(ReflectionUtil.path()), destinationJarFilePath, StandardCopyOption.REPLACE_EXISTING);

            // Use the new file path to get class names
            Set<String> classnames = getClassNamesFromJarFile(destinationJarFilePath.toFile());

            List<Class<?>> classes = new ArrayList<>();

            for (String classname : classnames) {
                try {
                    if (classname.startsWith(packageName)) {
                        Class<?> clazz = Class.forName(classname);
                        classes.add(clazz);
                    }
                } catch (NoClassDefFoundError | ClassNotFoundException |
                         UnsupportedClassVersionError noClassDefFoundError) {
                }
            }

            return classes.toArray(new Class[classes.size()]);
        } catch (Exception exception) {

            exception.printStackTrace();

            File directory = getPackageDirectory(packageName);

            if (!directory.exists()) {
                throw new IllegalArgumentException("Could not get directory resource for package " + packageName);
            }

            return getClassesInPackage(packageName, directory);
        }
    }

    public static Set<String> getClassNamesFromJarFile(File givenFile) throws IOException {
        Set<String> classNames = new HashSet<>();
        try (JarFile jarFile = new JarFile(givenFile)) {
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry jarEntry = e.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    String className = jarEntry.getName()
                            .replace("/", ".")
                            .replace(".class", "");
                    classNames.add(className);
                }
            }
            return classNames;
        }
    }

    private static Class<?>[] getClassesInPackage(String packageName, File directory) {
        List<Class<?>> classes = new ArrayList<>();

        for (String filename : Objects.requireNonNull(directory.list())) {
            if (filename.endsWith(".class")) {
                String classname = buildClassname(packageName, filename);
                try {
                    classes.add(Class.forName(classname));
                } catch (ClassNotFoundException e) {
                    System.err.println("Error creating class " + classname);
                }
            } else if (!filename.contains(".")) {
                String name = packageName + (packageName.endsWith(".") ? "" : ".") + filename;
                classes.addAll(Arrays.asList(getClassesInPackage(name, getPackageDirectory(name))));
            }
        }

        return classes.toArray(new Class[classes.size()]);
    }

    public static String buildClassname(String packageName, String filename) {
        return packageName + '.' + filename.replace(".class", "");
    }

    private static File getPackageDirectory(String packageName) {
        ClassLoader cld = Thread.currentThread().getContextClassLoader();
        if (cld == null) {
            throw new IllegalStateException("Can't get class loader.");
        }

        URL resource = cld.getResource(packageName.replace('.', '/'));

        if (resource == null) {
            throw new RuntimeException("Package " + packageName + " not found on classpath.");
        }

        return new File(resource.getFile());
    }

    public static boolean dirExist(String packageName) {
        ClassLoader cld = Thread.currentThread().getContextClassLoader();
        URL resource = cld.getResource(packageName.replace('.', '/'));
        return resource != null;
    }

    public static String path() throws URISyntaxException {
        System.out.println(Hermes.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI());
        return new File(Hermes.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();
    }
}
