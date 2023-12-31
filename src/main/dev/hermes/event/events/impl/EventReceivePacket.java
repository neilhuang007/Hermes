package dev.hermes.event.events.impl;

import dev.hermes.event.events.callables.EventCancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

@Getter
@Setter
@AllArgsConstructor
public class EventReceivePacket extends EventCancellable {
    private Packet<?> packet;
}
