package dev.hermes.utils.player;

import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.world.EventUpdate;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

@Hermes
@Getter
@Setter
public class VirtualEntity {
    private double posX, posY, posZ;
    private double cacheX, cacheY, cacheZ;
    private final float width, height;

    public VirtualEntity(Entity entity) {
        cacheX = this.posX = entity.posX;
        cacheY = this.posY = entity.posY;
        cacheZ = this.posZ = entity.posZ;
        this.width = entity.width;
        this.height = entity.height;
    }

    public void handleVirtualMovement(double posX, double posY, double posZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public void handleVirtualTeleport(double serverPosX, double serverPosY, double serverPosZ) {
        double d0 = serverPosX / 32.0D;
        double d1 = serverPosY / 32.0D;
        double d2 = serverPosZ / 32.0D;

        if (!(Math.abs(posX - d0) < 0.03125D && Math.abs(posY - d1) < 0.015625D && Math.abs(posZ - d2) < 0.03125D)) {
            posX = d0;
            posY = d1;
            posZ = d2;
        }
    }


    @EventTarget
    public void onUpdate(EventUpdate eventUpdate){
        cacheX += (posX - cacheX) * 0.2;
        cacheY += (posY - cacheY) * 0.2;
        cacheZ += (posZ - cacheZ) * 0.2;
    }


    public AxisAlignedBB getEntityBoundingBox() {
        float f = this.width / 2.0F;
        return new AxisAlignedBB(cacheX - (double) f, cacheY, cacheZ - (double) f, cacheX + (double) f, cacheY + (double) this.height, cacheZ + (double) f);
    }
}