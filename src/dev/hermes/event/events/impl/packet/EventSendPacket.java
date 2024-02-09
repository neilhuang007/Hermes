package dev.hermes.event.events.impl.packet;

import dev.hermes.event.events.callables.EventCancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.Packet;

@Getter
@Setter
@AllArgsConstructor
public class EventSendPacket extends EventCancellable {
    private Packet<?> packet;
}
