package dev.hermes.injection;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import com.sun.tools.attach.spi.AttachProvider;
import com.sun.tools.javac.Main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;


public class InjectorMain {


    public static void main(String[] args) throws IOException, InterruptedException {
        // Check for tools.jar
        try {
            Class.forName("com.sun.tools.attach.VirtualMachine");
            System.out.println("tools.jar found");
        } catch (ClassNotFoundException e) {
            System.out.println("tools.jar not found, adding dynamically");
            InjectUtils.addLibraryDynamically(System.getenv("JAVA_HOME") + "/lib/tools.jar");
        }

        // Additional check for ASM or any other library if required
        try {
            Class.forName("org.objectweb.asm.ClassVisitor");
            System.out.println("ASM library found.");
        } catch (ClassNotFoundException e) {
            System.out.println("ASM library not found, attempting to add dynamically.");
            // Specify the path to the ASM library or adjust accordingly
            InjectUtils.addLibraryDynamically("asm-9.6.jar"); // Adjust this path to your ASM library location
        }

//        System.out.println("Waiting for Minecraft process...");
//        int pid = InjectUtils.getMinecraftProcessId();
//        System.out.println("Found a Minecraft process, PID: " + pid);

        try {
            List<String> mainclasses = List.of("net.minecraft.client.Minecraft", "net.minecraft.client.main.Main", "net.minecraft.launchwrapper.Launch");
            System.out.println("Waiting for Minecraft process...");
            VirtualMachine vm = InjectUtils.getVM(mainclasses);
            System.out.println("Found a Minecraft process, VM: " + vm.id());
            if (vm != null) {
                // Path correction might be required depending on the environment

                // Change the path to the specific JAR file
                String jarFilePath = "out/artifacts/Hermes_jar/Hermes.jar"; // Specify the relative path to the JAR
                File agentJarFile = new File(jarFilePath); // Use the specified path


                if(!agentJarFile.exists()){
                    System.out.println("Agent File not Found");
                }

                try{
                    vm.loadAgent(agentJarFile.getAbsolutePath());
                    System.out.println("Agent loaded into Minecraft process.");
                }catch (Exception e){
                    System.err.println("Failed to attach: " + e.getMessage());
                    vm.detach();
                    e.printStackTrace();
                }
            } else {
                System.out.println("Minecraft process not found.");
            }
        } catch (Exception e) {
            System.err.println("Failed to attach: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
