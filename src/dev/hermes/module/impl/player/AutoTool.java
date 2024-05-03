package dev.hermes.module.impl.player;

import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.world.BlockDamageEvent;
import dev.hermes.event.events.impl.world.EventUpdate;
import dev.hermes.manager.InvManager;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.module.value.impl.BooleanValue;
import dev.hermes.utils.interfaces.InstanceAccess;
import net.minecraft.util.BlockPos;

@Hermes
@ModuleInfo(name = "AutoTool", description = "Automatically selects the best tool for the job", category = Category.PLAYER)
public class AutoTool extends Module {

    private final BooleanValue OnSneak = new BooleanValue("OnSneak", this,true);

    private int slot, lastSlot = -1;
    private int blockBreak;
    private BlockPos blockPos;

    @EventTarget
    public void onBlockDamage(BlockDamageEvent event) {
        blockBreak = 3;
        blockPos = event.getBlockPos();
    };

    @EventTarget
    public void onPreUpdate(EventUpdate event) {
        if(mc.thePlayer.isSneaking() && !OnSneak.getValue()) return;
        switch (InstanceAccess.mc.objectMouseOver.typeOfHit) {
            case BLOCK:
                if (blockPos != null && blockBreak > 0) {
                    slot = InvManager.findTool(blockPos);
                } else {
                    slot = -1;
                }
                break;

            case ENTITY:
                slot = InvManager.findSword();
                break;

            default:
                slot = -1;
                break;
        }

        if (lastSlot != -1) {
            InvManager.setSlot(lastSlot);
        } else if (slot != -1) {
            InvManager.setSlot(slot);
        }

        lastSlot = slot;
        blockBreak--;
    };

}
