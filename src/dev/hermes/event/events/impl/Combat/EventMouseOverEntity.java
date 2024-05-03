package dev.hermes.event.events.impl.Combat;

import dev.hermes.event.events.callables.EventCancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Getter
@Setter
@AllArgsConstructor
public class EventMouseOverEntity extends EventCancellable {
    private Entity entity;
}
