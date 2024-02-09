package dev.hermes.event.events.impl.world;

import dev.hermes.event.events.Event;
import lombok.Getter;

import java.util.List;

@Getter
@lombok.AllArgsConstructor
@lombok.Setter
public class EventServerKick implements Event {
    public List<String> reason;
}
