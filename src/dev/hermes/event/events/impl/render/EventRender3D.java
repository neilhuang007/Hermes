package dev.hermes.event.events.impl.render;

import dev.hermes.event.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventRender3D implements Event {
    private float partialTicks;
}
