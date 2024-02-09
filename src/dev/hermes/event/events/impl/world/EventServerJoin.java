package dev.hermes.event.events.impl.world;

import dev.hermes.event.events.Event;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class EventServerJoin implements Event {
    private String ip;
    private int port;
}
