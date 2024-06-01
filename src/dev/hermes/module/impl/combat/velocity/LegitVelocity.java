package dev.hermes.module.impl.combat.velocity;


import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.Motion.EventPreMotion;
import dev.hermes.event.events.impl.Movement.EventMovementInput;
import dev.hermes.event.events.impl.packet.EventReceivePacket;
import dev.hermes.event.events.impl.world.EventTick;
import dev.hermes.module.impl.combat.Velocity;
import dev.hermes.module.value.Mode;
import dev.hermes.utils.TimerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Random;

public final class LegitVelocity extends Mode<Velocity> {

    public LegitVelocity(String name, Velocity parent) {
        super(name, parent);
    }

    private boolean jump;
    private boolean shouldjump;

    TimerUtil watch = new TimerUtil();

    Random random = new Random();


    @EventTarget
    public void eventpremotion(EventPreMotion event) {
        // disables before tick to prevent some checks
        jump = false;
    };

    @EventTarget
    public void eventMove(EventMovementInput event) {
        // now lets look at this
        if(jump && watch.finished(random.nextInt(2))){
            event.setJump(true);
        }
    };

    @EventTarget
    public void tickevent(EventTick event) {
        // this should only be a pass on
        if(shouldjump){
            jump = true;
            shouldjump = false;
        }
        else{
            jump = false;
        }
    };

    @EventTarget
    public void onPacketReceive(EventReceivePacket event) {
        // if swinging or sprinting then cancel
        if (getParent().onSwing.getValue() || getParent().onSprint.getValue() && !mc.thePlayer.isSwingInProgress) return;

        // prevents fake velocity in the air
        if (!mc.thePlayer.onGround) {
            return;
        }

        // now look at when packet is received

        final Packet<?> packet = event.getPacket();
        if (packet instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) packet;

            if (wrapper.getEntityID() == mc.thePlayer.getEntityId()) {
                shouldjump = true;
                // if it is us, wait for 2 ticks then jump to make sure it does reset velocity
                watch.resetTimer();
            }
        }

        if (packet instanceof S27PacketExplosion) {
            jump = true;
        }
    };
}
