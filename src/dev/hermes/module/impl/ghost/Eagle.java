package dev.hermes.module.impl.ghost;

import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.Motion.EventPreMotion;
import dev.hermes.event.events.impl.Movement.EventMovementInput;
import dev.hermes.manager.MathManager;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.module.value.impl.BooleanValue;
import dev.hermes.module.value.impl.BoundsNumberValue;
import dev.hermes.module.value.impl.NumberValue;
import dev.hermes.utils.player.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemBlock;

import java.util.Random;

@Hermes
@ModuleInfo(name = "Legit Scaffold", description = "Sneaks for You", category = Category.GHOST)
public class Eagle extends Module {
    private final BooleanValue groundOnly = new BooleanValue("Only on ground", this, false);
    private final BooleanValue blocksOnly = new BooleanValue("Only when holding blocks", this, false);
    private final BooleanValue backwardsOnly = new BooleanValue("Only when moving backwards", this, false);
    private final NumberValue delay = new NumberValue("Delay", this, 0, 0, 10, 1);

    private final BoundsNumberValue offsetRange = new BoundsNumberValue("Offset range", this, 0.1, 0.7, 0, 10,0.1);
    private final Random random = new Random();

    private double offset;

    private boolean sneaked;
    private int ticksOverEdge;

    @EventTarget
    public final void onPreMotionEvent(EventPreMotion event) {
        if (mc.thePlayer.getHeldItem() != null && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) &&
                blocksOnly.getValue()) {
            if (sneaked) {
                sneaked = false;
            }
            return;
        }

        if (sneaked) {
            offset = MathManager.randomBetween(offsetRange.getValue().floatValue(), offsetRange.getSecondValue().floatValue());
        }

        // Calculate the offset in the x and z directions based on the yaw angle
        float yaw = mc.thePlayer.rotationYaw;
        double offsetX = -Math.sin(Math.toRadians(yaw)) * offset;
        double offsetZ = Math.cos(Math.toRadians(yaw)) * offset;

        if ((mc.thePlayer.onGround || !groundOnly.getValue()) &&
                (PlayerUtil.blockRelativeToPlayer(offsetX, -1, offsetZ) instanceof BlockAir) &&
                (!mc.gameSettings.keyBindForward.isKeyDown() || !backwardsOnly.getValue())) {
            if (!sneaked) {
                sneaked = true;
            }
        } else if (sneaked) {
            sneaked = false;
        }

        if (sneaked) {
            mc.gameSettings.keyBindSprint.setPressed(false);
        }

        if (sneaked) {
            ticksOverEdge++;
        } else {
            ticksOverEdge = 0;
        }
    };

    @Override
    protected void onDisable() {
        if (sneaked) {
            sneaked = false;
        }
    }

    @EventTarget
    public final void OnMovement(EventMovementInput event) {
        event.setSneak((sneaked && ticksOverEdge > delay.getValue().doubleValue()) ||
                (mc.gameSettings.keyBindSneak.isKeyDown()));
    };
}