package dev.hermes.utils.client.packet;

import dev.hermes.utils.Utils;
import net.minecraft.network.Packet;

public class PacketUtils implements Utils {
    public static void sendPacket(Packet<?> packet) {
        mc.getNetHandler().addToSendQueue(packet);
    }

    public static void sendPacketNoEvent(Packet<?> packet) {
        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
    }
}
