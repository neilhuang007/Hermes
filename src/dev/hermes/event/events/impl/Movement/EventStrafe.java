package dev.hermes.event.events.impl.Movement;

import dev.hermes.event.events.callables.EventCancellable;
import dev.hermes.utils.interfaces.InstanceAccess;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public final class EventStrafe extends EventCancellable implements InstanceAccess {

    private float forward;
    private float strafe;
    private float friction;
    private float yaw;

    public void setSpeed(final double speed, final double motionMultiplier) {
        setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
        mc.thePlayer.motionX *= motionMultiplier;
        mc.thePlayer.motionZ *= motionMultiplier;
    }

}

