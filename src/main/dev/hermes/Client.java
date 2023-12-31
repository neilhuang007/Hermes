package dev.hermes;

import lombok.Getter;

@Getter
public class Client {
    private final String name = "Hermes";
    private final String version = "dev 0.1";
    private final String title = name + " " + version + " | " + "Minecraft 1.8.9";
}
