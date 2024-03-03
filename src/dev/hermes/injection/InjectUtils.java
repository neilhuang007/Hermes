package dev.hermes.injection;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import com.sun.tools.attach.spi.AttachProvider;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class InjectUtils {
    public static int getMinecraftProcessId() throws InterruptedException {
        try{
            User32 user32 = User32.INSTANCE;
            IntByReference pid = new IntByReference(-1);
            do {
                WinDef.HWND findWindow;
                WinDef.HWND hWnd = findWindow = user32.FindWindow("LWJGL", null);
                if (findWindow != null && findWindow.getPointer() != null) {
                    char[] buffer = new char[1024];
                    user32.GetWindowText(hWnd, buffer, buffer.length);
                    final String windowText = new String(buffer).trim();
                    if (!windowText.isEmpty()) {
                        System.out.println("Found window with title: " + windowText);
                    }
                    user32.GetWindowThreadProcessId(hWnd, pid);
                } else {
                    Thread.sleep(100L);
                }
            } while (pid.getValue() == -1);
            System.out.println("Minecraft process ID: " + pid.getValue());
            return pid.getValue();
        }catch (Exception e){
            System.err.println("Error finding Minecraft process: " + e.getMessage());
            return -1;
        }
    }
    public static void addLibraryDynamically(String libraryPath) {
        try {
            URL url = new File(libraryPath).toURI().toURL();
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
            addURL.invoke(classLoader, url);
            System.out.println("Dynamically added library: " + libraryPath);
        } catch (Exception e) {
            System.err.println("Error adding library dynamically: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static VirtualMachine getVM(List<String> MainClasses) throws IOException, AttachNotSupportedException, InterruptedException {
        VirtualMachine virtualMachine = null;
        while (virtualMachine == null){
            for(AttachProvider provider : AttachProvider.providers()){
                System.out.println("Provider: " + provider);
                for(VirtualMachineDescriptor vm: provider.listVirtualMachines()){
                    System.out.println("VM: " + vm.displayName());
                    String mainClazz = vm.displayName().split(" ")[0];
                    for(String name: MainClasses){
                        if(mainClazz.equals(name)){
                            virtualMachine = provider.attachVirtualMachine(vm);
                            return virtualMachine;
                        }
                    }
                }
            }
            Thread.sleep(1000); // Wait for 5 seconds before retrying
        }
        return null;
    };
}
