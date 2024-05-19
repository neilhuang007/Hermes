package dev.hermes.module.impl.ghost;

import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.Motion.EventPostMotion;
import dev.hermes.event.events.impl.packet.EventReceivePacket;
import dev.hermes.event.events.impl.render.EventRender3D;
import dev.hermes.manager.EventManager;
import dev.hermes.manager.RenderManager;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.module.value.impl.BooleanValue;
import dev.hermes.module.value.impl.NumberValue;
import dev.hermes.utils.player.DelayedPacket;
import dev.hermes.utils.player.PendingVelocity;
import dev.hermes.utils.player.VirtualEntity;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@ModuleInfo(name = "Backtrack", description = "Backtrack players", category = Category.GHOST)
public class Backtrack extends Module {
    private final NumberValue delay = new NumberValue("Delay", this, 500, 100, 2000, 50);
    private final BooleanValue delayPing = new BooleanValue("Delay ping", this, true);
    private final BooleanValue delayVelocity = new BooleanValue("Delay velocity", this, true, delayPing::getValue);

    private final CopyOnWriteArrayList<DelayedPacket> delayedPackets = new CopyOnWriteArrayList<>();

    private EntityLivingBase lastTarget;

    private PendingVelocity lastVelocity;

    private VirtualEntity virtualEntity = null;

    @EventTarget
    private void onPacket(EventReceivePacket event) {
        try {
            if (mc.thePlayer == null || mc.thePlayer.ticksExisted < 5) {
                if (!delayedPackets.isEmpty())
                    delayedPackets.clear();
            }

            EntityLivingBase currentTarget = getCurrentTarget();

            if (currentTarget != lastTarget) {
                clearPackets();
                if (currentTarget != null) {
                    virtualEntity = new VirtualEntity(currentTarget);
                    EventManager.register(virtualEntity);
                }
            }
            if (currentTarget == null) {
                clearPackets();
                EventManager.unregister(virtualEntity);
                virtualEntity = null;
            }else if (event.getPacket() instanceof S14PacketEntity) {
                S14PacketEntity packet = (S14PacketEntity) event.getPacket();

                NetHandlerPlayClient NetHandlerPlayClient = mc.getNetHandler();
                if (packet.getEntity(NetHandlerPlayClient.clientWorldController) == currentTarget) {
                    int x = currentTarget.serverPosX + packet.getX();
                    int y = currentTarget.serverPosY + packet.getY();
                    int z = currentTarget.serverPosZ + packet.getZ();
                    double posX = (double) x / 32.0D;
                    double posY = (double) y / 32.0D;
                    double posZ = (double) z / 32.0D;
                    if (virtualEntity != null) virtualEntity.handleVirtualMovement(posX, posY, posZ);

                }
            } else if (event.getPacket() instanceof S18PacketEntityTeleport) {
                S18PacketEntityTeleport packet = (S18PacketEntityTeleport) event.getPacket();
                if (packet.getEntityId() == currentTarget.getEntityId()) {
                    double serverX = packet.getX();
                    double serverY = packet.getY();
                    double serverZ = packet.getZ();

                    if (virtualEntity != null) virtualEntity.handleVirtualTeleport(serverX, serverY, serverZ);
                }
            } else if (event.getPacket() instanceof S32PacketConfirmTransaction || event.getPacket() instanceof S00PacketKeepAlive) {
                if (!delayedPackets.isEmpty() && delayPing.getValue()) {
                    if(currentTarget.hurtTime > 0){
//                        event.setCancelled(true);
//                        delayedPackets.add(new DelayedPacket((Packet<INetHandlerPlayClient>) event.getPacket()));
                    }
                }
            } else if (event.getPacket() instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();

                if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                    if (!delayedPackets.isEmpty() && delayPing.getValue() && delayVelocity.getValue()) {
                        if(currentTarget.hurtTime > 0) {
//                            event.setCancelled(true);
//                            lastVelocity = new PendingVelocity(packet.getMotionX() / 8000.0, packet.getMotionY() / 8000.0, packet.getMotionZ() / 8000.0);
                        }
                    }
                }
            }
            lastTarget = currentTarget;
        } catch (Throwable e) {
            System.out.println("Error in Backtrack: " + e.getMessage());
        }
    }

    @EventTarget
    private void onRender(EventRender3D e) {
        if (virtualEntity != null) {
            RenderManager.drawEntityBox(
                    virtualEntity.getEntityBoundingBox(), virtualEntity.getPosX(), virtualEntity.getPosY(), virtualEntity.getPosZ(),
                    new Color(255,255,255, 87), true, true, 1
            );
        }
    }

    @EventTarget
    public void onPostMotion(EventPostMotion event) {
        updatePackets();
    }

    public EntityLivingBase getCurrentTarget() {
        if (mc.theWorld == null) return null;
        List<Entity> entityList = new ArrayList<>(mc.theWorld.loadedEntityList);
        entityList = entityList.stream().filter(entity ->
                entity != mc.thePlayer &&
                        entity instanceof EntityLivingBase &&
                        entity.getDistanceToEntity(mc.thePlayer) <= 6
        ).sorted(
                Comparator.comparingInt(entity -> (int) (entity.getDistanceToEntity(mc.thePlayer) * 100))
        ).collect(Collectors.toList());
        if (mc.thePlayer.getAttackingEntity() != null) {
            return (EntityLivingBase) mc.thePlayer.getAttackingEntity();
        }
        if (!entityList.isEmpty()) return (EntityLivingBase) entityList.get(0);
        return null;
    }

    public void updatePackets() {
        if (!delayedPackets.isEmpty()) {
            for (int i = 0; i < delayedPackets.size(); i++) {
                DelayedPacket p = delayedPackets.get(i);
                if (p.getTimer().getTimeElapsed() >= delay.getValue().floatValue()) {
                    handlePacket(p.getPacket());
                    if (lastVelocity != null) {
                        mc.thePlayer.motionX = lastVelocity.getX();
                        mc.thePlayer.motionY = lastVelocity.getY();
                        mc.thePlayer.motionZ = lastVelocity.getZ();
                        lastVelocity = null;
                    }
                    delayedPackets.remove(i);
                    i--;
                }
            }
        }
    }

    public void clearPackets() {
        if (lastVelocity != null) {
            mc.thePlayer.motionX = lastVelocity.getX();
            mc.thePlayer.motionY = lastVelocity.getY();
            mc.thePlayer.motionZ = lastVelocity.getZ();
            lastVelocity = null;
        }

        if (!delayedPackets.isEmpty()) {
            for (DelayedPacket p : delayedPackets)
                handlePacket(p.getPacket());
            delayedPackets.clear();
        }
    }

    public void handlePacket(Packet<INetHandlerPlayClient> packet) {
        if (packet instanceof S14PacketEntity) {
            handleEntityMovement((S14PacketEntity) packet);
        } else if (packet instanceof S18PacketEntityTeleport) {
            handleEntityTeleport((S18PacketEntityTeleport) packet);
        } else if (packet instanceof S32PacketConfirmTransaction) {
            handleConfirmTransaction((S32PacketConfirmTransaction) packet);
        } else if (packet instanceof S00PacketKeepAlive) {
            mc.getNetHandler().handleKeepAlive((S00PacketKeepAlive) packet);
        }
    }

    public void handleEntityMovement(S14PacketEntity packetIn) {
        NetHandlerPlayClient NetHandlerPlayClient = mc.getNetHandler();
        Entity entity = packetIn.getEntity(NetHandlerPlayClient.clientWorldController);

        if (entity != null) {
            entity.serverPosX += packetIn.getX();
            entity.serverPosY += packetIn.getY();
            entity.serverPosZ += packetIn.getZ();
            byte yaw = packetIn.getYaw();
            byte pitch = packetIn.getPitch();
            double d0 = (double) entity.serverPosX / 32.0D;
            double d1 = (double) entity.serverPosY / 32.0D;
            double d2 = (double) entity.serverPosZ / 32.0D;
            float f = packetIn.func_149060_h() ? (float) (yaw * 360) / 256.0F : entity.rotationYaw;
            float f1 = packetIn.func_149060_h() ? (float) (pitch * 360) / 256.0F : entity.rotationPitch;
            entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3, false);
            entity.onGround = packetIn.getOnGround();
        }
    }

    public void handleEntityTeleport(S18PacketEntityTeleport packetIn) {
        WorldClient WorldClient = mc.theWorld;
        Entity entity = WorldClient.getEntityByID(packetIn.getEntityId());

        if (entity != null) {
            entity.serverPosX = packetIn.getX();
            entity.serverPosY = packetIn.getY();
            entity.serverPosZ = packetIn.getZ();
            double d0 = (double) entity.serverPosX / 32.0D;
            double d1 = (double) entity.serverPosY / 32.0D;
            double d2 = (double) entity.serverPosZ / 32.0D;
            float f = (float) (packetIn.getYaw() * 360) / 256.0F;
            float f1 = (float) (packetIn.getPitch() * 360) / 256.0F;

            if (Math.abs(entity.posX - d0) < 0.03125D && Math.abs(entity.posY - d1) < 0.015625D && Math.abs(entity.posZ - d2) < 0.03125D) {
                entity.setPositionAndRotation2(entity.posX, entity.posY, entity.posZ, f, f1, 3, true);
            } else {
                entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3, true);
            }

            entity.onGround = packetIn.getOnGround();
        }
    }

    public void handleConfirmTransaction(S32PacketConfirmTransaction packetIn) {
        Container container = null;
        EntityPlayer entityplayer = mc.thePlayer;

        if (packetIn.getWindowId() == 0) {
            container = entityplayer.inventoryContainer;
        } else if (packetIn.getWindowId() == entityplayer.openContainer.windowId) {
            container = entityplayer.openContainer;
        }

        if (container != null && !packetIn.func_148888_e()) {
            mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), true));
        }
    }

    public boolean isDelaying() {
        return this.isEnabled() && !delayedPackets.isEmpty();
    }
}
