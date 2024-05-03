package dev.hermes.module.impl.other;

import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.render.EventRender2D;
import dev.hermes.event.events.impl.world.EventWorldChange;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import net.minecraft.client.entity.EntityOtherPlayerMP;

@Hermes
@ModuleInfo(name = "FakePlayer", category = Category.OTHER,autoEnabled = false)
public class FakePlayer extends Module {

    private EntityOtherPlayerMP blinkEntity;

    public void deSpawnEntity() {
        if (blinkEntity != null) {
            mc.theWorld.removeEntityFromWorld(blinkEntity.getEntityId());
            blinkEntity = null;
        }
    }

    public void spawnEntity() {
        if (blinkEntity == null) {
            blinkEntity = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            blinkEntity.setPositionAndRotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            blinkEntity.rotationYawHead = mc.thePlayer.rotationYawHead;
            blinkEntity.setSprinting(mc.thePlayer.isSprinting());
            blinkEntity.setInvisible(mc.thePlayer.isInvisible());
            blinkEntity.setSneaking(mc.thePlayer.isSneaking());
            blinkEntity.inventory = mc.thePlayer.inventory;
            blinkEntity.setHealth(mc.thePlayer.getHealth());

            mc.theWorld.addEntityToWorld(blinkEntity.getEntityId(), blinkEntity);
        }
    }

    @Override
    protected void onEnable() {
        spawnEntity();
    }

    @Override
    protected void onDisable() {
        deSpawnEntity();
    }

    @EventTarget
    public void onworldChange(EventWorldChange event) {
        this.onDisable();
    }
}
