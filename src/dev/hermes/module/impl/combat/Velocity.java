package dev.hermes.module.impl.combat;


import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.packet.EventReceivePacket;
import dev.hermes.event.events.impl.world.EventUpdate;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;

import dev.hermes.module.value.impl.BooleanValue;
import dev.hermes.module.value.impl.ModeValue;
import dev.hermes.module.value.impl.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

@Hermes
@ModuleInfo(name = "Velocity", description = "Reduces knockback for you", category = Category.COMBAT)
public class Velocity extends Module {
    private final NumberValue horizontal = new NumberValue("Horizontal", this, 0, 0, 100, 1);
    private final NumberValue vertical = new NumberValue("Vertical", this, 0, 0, 100, 1);

    public final BooleanValue LagBackDetection = new BooleanValue("Lagback Detections", this, true);
    // bruh the clickgui does not read the things properly in the mode value so has to write like this
    public final BooleanValue retoggle = new BooleanValue("retoggle", this, true, LagBackDetection::getValue);

    public final NumberValue RetoggleDelay = new NumberValue("Retoggle Delay(ms)", this, 0.5F,0.1,3,0.1, LagBackDetection::getValue);


    public final NumberValue LagBacks = new NumberValue("Lagbacks Disable", this, 0, 0, 50, 1, LagBackDetection::getValue);

    public final BooleanValue onExplosion = new BooleanValue("Explosion Reduction", this, false);

    public final BooleanValue onSwing = new BooleanValue("On Swing", this, false);
    public final BooleanValue onSprint = new BooleanValue("On Sprint", this, false);


    @EventTarget
    private void as(EventUpdate eventUpdate) {
        if(mc.thePlayer.hurtTime >= 3 && mc.thePlayer.hurtTime <= 5) {
            mc.gameSettings.keyBindJump.setPressed(true);
        }else if(mc.thePlayer.hurtTime > 0){
            mc.gameSettings.keyBindJump.setPressed(false);
        }
    }

    @EventTarget
    private void onPacket(EventReceivePacket event) {

        if (onSwing.getValue() || onSprint.getValue() && !mc.thePlayer.isSwingInProgress) return;
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



