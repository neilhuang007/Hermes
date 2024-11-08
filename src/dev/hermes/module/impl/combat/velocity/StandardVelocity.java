package dev.hermes.module.impl.combat.velocity;


import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.packet.EventReceivePacket;
import dev.hermes.module.impl.combat.Velocity;
import dev.hermes.module.value.Mode;
import dev.hermes.module.value.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public final class StandardVelocity extends Mode<Velocity> {

    private final NumberValue horizontal = new NumberValue("Horizontal", this, 0, 0, 100, 1);
    private final NumberValue vertical = new NumberValue("Vertical", this, 0, 0, 100, 1);


    public StandardVelocity(String name, Velocity parent) {
        super(name, parent);
    }

    @EventTarget
    public void onPacketReceive(EventReceivePacket event) {


        if (getParent().onSwing.getValue() || getParent().onSprint.getValue() && !mc.thePlayer.isSwingInProgress) return;
        final Packet<?> p = event.getPacket();

        final double horizontal = this.horizontal.getValue().doubleValue();
        final double vertical = this.vertical.getValue().doubleValue();

        if (p instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) p;

            if (wrapper.getEntityID() == mc.thePlayer.getEntityId()) {
                if (horizontal == 0) {
                    event.setCancelled(true);

                    if (vertical != 0) {
                        mc.thePlayer.motionY = wrapper.getMotionY() / 8000.0D;
                    }
                    return;
                }

                wrapper.motionX *= horizontal / 100;
                wrapper.motionY *= vertical / 100;
                wrapper.motionZ *= horizontal / 100;

                event.setPacket(wrapper);

            }
        } else if (p instanceof S27PacketExplosion) {
            final S27PacketExplosion wrapper = (S27PacketExplosion) p;

            if (horizontal == 0 && vertical == 0) {
                event.setCancelled(true);
                return;
            }

            wrapper.posX *= horizontal / 100;
            wrapper.posY *= vertical / 100;
            wrapper.posZ *= horizontal / 100;

            event.setPacket(wrapper);
        }
    };



}
