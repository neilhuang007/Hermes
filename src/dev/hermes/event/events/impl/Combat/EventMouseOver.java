package dev.hermes.event.events.impl.Combat;

import dev.hermes.event.events.callables.EventCancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventMouseOver extends EventCancellable {
    private double range;
    private float expand;
}
