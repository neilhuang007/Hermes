package dev.hermes.module.impl.render.clickgui;

import dev.hermes.api.Hermes;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.utils.client.file.FileUtils;
import dev.hermes.utils.interfaces.InstanceAccess;
import dev.hermes.utils.reflection.ReflectionUtil;
import dev.hermes.utils.web.Browser;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

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
