package dev.hermes.event.events.impl.world;

import dev.hermes.event.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.World;

@Getter
@AllArgsConstructor
@Setter
public class EventWorldChange implements Event {
    World oldWorld;
    World newWorld;
}
