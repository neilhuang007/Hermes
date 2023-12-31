package dev.hermes;

import lombok.Getter;
import org.lwjgl.opengl.Display;

@Getter
public class Client {
    private static final String name = "Hermes";
    private static final String version = "dev 0.1";
    private static final String title = name + " " + version + " | " + "Minecraft 1.8.9";

    public static void startClient() {
        Display.setTitle(title);
    }

    public static void stopClient() {

    }
}
