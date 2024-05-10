package dev.hermes.utils.player;


import lombok.Getter;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;

public class DelayedPacket {
    private final Packet<INetHandlerPlayClient> packet;

    @Getter
    private final DelayTimer timer;

    public DelayedPacket(Packet<INetHandlerPlayClient> packet) {
        this.packet = packet;
        this.timer = new DelayTimer();
    }

    public <T extends Packet<INetHandlerPlayClient>> T getPacket() {
        return (T) packet;
    }
}
