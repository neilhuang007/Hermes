package dev.hermes.module.impl.combat.velocity;


import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.Motion.EventPreMotion;
import dev.hermes.event.events.impl.packet.EventReceivePacket;
import dev.hermes.manager.ChatManager;
import dev.hermes.manager.PacketManager;
import dev.hermes.module.impl.combat.Velocity;
import dev.hermes.module.value.Mode;
import dev.hermes.utils.TimerUtil;
import dev.hermes.utils.vector.Vector2d;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import org.apache.commons.lang3.time.StopWatch;

public final class WatchdogVelocity extends Mode<Velocity> {

    private TimerUtil stopWatch = new TimerUtil();
    private Vector2d velocity;
    private boolean ignoreTeleport;

    public WatchdogVelocity(String name, Velocity parent) {
        super(name, parent);
    }

//    @EventLink()
//    public final Listener<TeleportEvent> onTeleport = event -> {
//        if (ignoreTeleport) {
//            ignoreTeleport = false;
//            event.setCancelled(true);
//            ChatUtil.display("Cancelled Velocity Teleport");
//        }
//    };

    @EventTarget
    public void eventpremotion(EventPreMotion event) {
        if (velocity == null) {
            stopWatch.resetTimer();
            return;
        }

        if (stopWatch.finished(10000) && mc.thePlayer.onGround) {
            stopWatch.resetTimer();

            PacketManager.send(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
            PacketManager.send(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + velocity.y, mc.thePlayer.posZ, false));
            PacketManager.send(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));

            ChatManager.display("Sent Velocity");

            ignoreTeleport = true;

            velocity = null;
        }
    };

    @EventTarget
    public void onPacketReceive(EventReceivePacket event) {
        if (getParent().onSwing.getValue() || getParent().onSprint.getValue() && !mc.thePlayer.isSwingInProgress)
            return;

        final Packet<?> p = event.getPacket();

        if (p instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity wrapper = (S12PacketEntityVelocity) p;

            if (wrapper.getEntityID() == mc.thePlayer.getEntityId()) {
                event.setCancelled(true);

                velocity = new Vector2d(Math.hypot(wrapper.motionX / 8000.0D, wrapper.motionZ / 8000.0D),
                        wrapper.getMotionY() / 8000.0D);
            }
        } else if (p instanceof S27PacketExplosion) {
            event.setCancelled(true);
        }
    };
}
