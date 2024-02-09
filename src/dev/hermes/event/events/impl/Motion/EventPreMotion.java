package dev.hermes.event.events.impl.Motion;

import dev.hermes.event.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventPreMotion implements Event {
    private double x, y, z;
    private float yaw, pitch;
    private boolean onGround;
}
