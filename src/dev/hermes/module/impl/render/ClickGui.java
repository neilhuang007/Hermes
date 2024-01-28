package dev.hermes.module.impl.render;

import dev.hermes.api.Hermes;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

@Hermes
@ModuleInfo(name = "ClickGui", description = "Renders ClickGui for the client", category = Category.RENDER, keyBind = Keyboard.KEY_RSHIFT)
public class ClickGui extends Module {

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        // Define the URL to your HTML file
        String htmlFileUrl = ClickGui.class.getClassLoader().getResource("assets/hermes/ui/index.html").toString();

        // Check if the Desktop class is supported (requires Java 6 or later)
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                // Open the default web browser with the specified URL
                desktop.browse(new URI(htmlFileUrl));
            } catch (IOException | java.net.URISyntaxException e) {
                e.printStackTrace();
                // Handle any exceptions here
            }
        } else {
            // Desktop class is not supported, you can provide an alternative approach if needed
            System.err.println("Desktop class is not supported on this platform.");
        }
    }
}
