package dev.hermes.injection;


import dev.hermes.Hermes;
import dev.hermes.injection.mappings.MappingLoader;
import dev.hermes.injection.mappings.MappingUtils;


import java.lang.annotation.Annotation;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;

public class HermesAgent {

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("HermesAgent attached.");

        MappingUtils.init();

        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            if (MappingUtils.IsTargetedClass(clazz.getName().replace('.', '/'))) {
                String deobfClassName = MappingUtils.getClassMappings().get(clazz.getName().replace('.', '/')).replace('/', '.');
                System.out.println("Targeted class found: " + deobfClassName);

                try {
                    // Get the class loader of the targeted class
                    ClassLoader classLoader = HermesAgent.class.getClassLoader();

                    ClassLoader clazzclassLoader = clazz.getClassLoader();

                    if (clazzclassLoader.equals(classLoader)) {
                        System.out.println("Classloader is the same");
                    } else {
                        System.out.println("Classloader is different");
                    }

//                    // Load the class by its deobfuscated name using the correct class loader
//                    Class<?> deobfClass = Class.forName(deobfClassName, false, classLoader);
//
//                    // Get all declared methods of the class
//                    Method[] methods = deobfClass.getDeclaredMethods();

//                    // Iterate over each method
//                    for (Method method : methods) {
//                        // Get all annotations of the method
//                        Annotation[] annotations = method.getAnnotations();
//
//                        // Iterate over each annotation
//                        for (Annotation annotation : annotations) {
//                            // Check if the annotation is of type Modified or Inject
//                            if (annotation.annotationType().getSimpleName().equals("Modify") || annotation.annotationType().getSimpleName().equals("Inject")) {
//                                // Print the method name
//                                System.out.println("Method with @Modified or @Inject found: " + method.getName());
//
//                                // Print the whole method
//                                System.out.println(method);
//                            }
//                        }
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
