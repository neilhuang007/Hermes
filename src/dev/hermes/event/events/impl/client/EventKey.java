package dev.hermes.event.events.impl.client;

import dev.hermes.event.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventKey implements Event {
    private int key;
}
