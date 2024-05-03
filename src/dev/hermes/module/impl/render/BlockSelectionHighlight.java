package dev.hermes.module.impl.render;

import dev.hermes.api.Hermes;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.module.value.impl.ColorValue;
import dev.hermes.module.value.impl.NumberValue;
import lombok.Getter;

import java.awt.*;


@Hermes
@Getter
@ModuleInfo(name = "Block Selection Highlight", description = "Highlights the block you are looking at", category = Category.RENDER)
public class BlockSelectionHighlight extends Module {

    public ColorValue FillingColor = new ColorValue("BlockFaceColor", this, new Color(54, 53, 51, 74));

    public ColorValue lineColor = new ColorValue("BlockLineColor", this, new Color(0, 0, 0, 255));

    public NumberValue lineWidth = new NumberValue("LineWidth", this, 1.0, 0.5, 5.0, 0.1);



}