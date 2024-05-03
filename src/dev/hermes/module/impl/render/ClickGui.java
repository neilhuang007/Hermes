package dev.hermes.module.impl.render;

import dev.hermes.api.Hermes;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Hermes
@ModuleInfo(name = "ClickGui", description = "Renders ClickGui for the client", category = Category.RENDER, keyBind = Keyboard.KEY_RSHIFT)
public class ClickGui extends Module {

    @Override
    public void onEnable() {
        try {
            Desktop.getDesktop().browse(new URI("http://localhost:1342"));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}
