package dev.hermes.event.events.impl.world;

import dev.hermes.event.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiScreen;

@Getter
@Setter
@AllArgsConstructor
public class EventGuiChange implements Event {
    GuiScreen guiScreen;
}
