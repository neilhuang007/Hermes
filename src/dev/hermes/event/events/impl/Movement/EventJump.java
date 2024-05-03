package dev.hermes.event.events.impl.Movement;

import dev.hermes.event.events.callables.EventCancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventJump extends EventCancellable {
    private float jumpMotion;
    private float yaw;
}
