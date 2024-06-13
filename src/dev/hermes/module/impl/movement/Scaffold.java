package dev.hermes.module.impl.movement;


import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.packet.EventReceivePacket;
import dev.hermes.event.events.impl.world.EventUpdate;
import dev.hermes.manager.MathManager;
import dev.hermes.manager.MovementManager;
import dev.hermes.manager.PacketManager;
import dev.hermes.manager.RotationManager;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.module.value.impl.BoundsNumberValue;
import dev.hermes.module.value.impl.ModeValue;
import dev.hermes.module.value.impl.SubMode;
import dev.hermes.utils.RayCastUtil;
import dev.hermes.utils.player.EnumFacingOffset;
import dev.hermes.utils.player.PlayerUtil;
import dev.hermes.utils.rotation.MovementFix;
import dev.hermes.utils.vector.Vector2f;
import dev.hermes.utils.vector.Vector3d;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * @author Alan
 * @since ??/??/21
 */

@Hermes
@ModuleInfo(name = "Scaffold", description = "aa", category = Category.MOVEMENT)
public class Scaffold extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Normal"))
            .setDefault("Normal");


    private final ModeValue sameY = new ModeValue("Same Y", this)
            .add(new SubMode("Off"))
            .add(new SubMode("On"))
            .add(new SubMode("Auto Jump"))
            .setDefault("Off");

    private final BoundsNumberValue rotationSpeed = new BoundsNumberValue("Rotation Speed", this, 5, 10, 0, 10, 1);
    private final BoundsNumberValue placeDelay = new BoundsNumberValue("Place Delay", this, 0, 0, 0, 5, 1);

    private final BoundsNumberValue sneakdelay = new BoundsNumberValue("Sneak Delay", this, 0, 0, 0, 5, 1);

    private Vec3 targetBlock;
    private EnumFacingOffset enumFacing;
    private BlockPos blockFace;
    private float targetYaw = -1;
    private float targetPitch = -1;
    private int ticksOnAir;
    private double startY;


    @Override
    public void onEnable() {
        startY = Math.floor(mc.thePlayer.posY);
        targetBlock = null;
    }

    @Override
    protected void onDisable() {

    }

    @EventTarget
    public void onPacketReceive(EventReceivePacket event) {
        final Packet<?> packet = event.getPacket();
    };

    public void calculateRotations() {
        /* Calculating target rotations */
        getRotations(-45);

        /* Smoothing rotations */
        final double minRotationSpeed = this.rotationSpeed.getValue().doubleValue();
        final double maxRotationSpeed = this.rotationSpeed.getSecondValue().doubleValue();
        float rotationSpeed = (float) MathManager.getRandom(minRotationSpeed, maxRotationSpeed);

        if (rotationSpeed != 0) {
            if(!(targetYaw == -1 || targetPitch == -1)){
                RotationManager.setRotations(new Vector2f(targetYaw, targetPitch), rotationSpeed, MovementFix.OFF);
            }
        }

    }


    @EventTarget
    public void onPreUpdate(EventUpdate event) {
        //Used to detect when to place a block, if over air, allow placement of blocks
        if (PlayerUtil.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir) {
            ticksOnAir++;
            mc.gameSettings.keyBindSneak.setPressed(true);
        } else {
            ticksOnAir = 0;
            mc.gameSettings.keyBindSneak.setPressed(false);
        }

        // Gets block to place
        targetBlock = PlayerUtil.getPlacePossibility(0, 0, 0);

        if (targetBlock == null) {
            return;
        }

        //Gets EnumFacing
        enumFacing = PlayerUtil.getEnumFacing(targetBlock);

        if (enumFacing == null) {
            return;
        }

        final BlockPos position = new BlockPos(targetBlock.xCoord, targetBlock.yCoord, targetBlock.zCoord);

        blockFace = position.add(enumFacing.getOffset().xCoord, enumFacing.getOffset().yCoord, enumFacing.getOffset().zCoord);

        if (blockFace == null || enumFacing == null) {
            return;
        }

        this.calculateRotations();

        if (targetBlock == null || enumFacing == null || blockFace == null) {
            return;
        }

        if (this.sameY.getValue().getName().equals("Auto Jump")) {
            mc.gameSettings.keyBindJump.setPressed((mc.thePlayer.onGround && MovementManager.isMoving()) || mc.gameSettings.keyBindJump.isPressed());
        }

        // Same Y
        final boolean sameY = ((!this.sameY.getValue().getName().equals("Off")) && !mc.gameSettings.keyBindJump.isKeyDown()) && MovementManager.isMoving();

        if (startY - 1 != Math.floor(targetBlock.yCoord) && sameY) {
            return;
        }

        if(ticksOnAir > MathManager.getRandom(placeDelay.getValue().intValue(), placeDelay.getSecondValue().intValue())){

            Vec3 hitVec = this.getHitVec();

            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockFace, enumFacing.getEnumFacing(), hitVec)) {
                PacketManager.send(new C0APacketAnimation());
            }

            mc.rightClickDelayTimer = 0;
            ticksOnAir = 0;
        } else if (Math.random() > 0.92 && mc.rightClickDelayTimer <= 0) {
            PacketManager.send(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            mc.rightClickDelayTimer = 0;
        }

        //For Same Y
        if (mc.thePlayer.onGround || (mc.gameSettings.keyBindJump.isKeyDown() && !MovementManager.isMoving())) {
            startY = Math.floor(mc.thePlayer.posY);
        }

        if (mc.thePlayer.posY < startY) {
            startY = mc.thePlayer.posY;
        }
    }

    public void getRotations(final float yawOffset) {
        boolean found = false;
        if(PlayerUtil.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir){
            final Vector2f rotations = dev.hermes.Hermes.rotationManager.calculate(
                    new Vector3d(blockFace.getX(), blockFace.getY(), blockFace.getZ()), enumFacing.getEnumFacing());
            final Vector2f calc = dev.hermes.Hermes.rotationManager.getRotationstoblock(new BlockPos(blockFace.getX(), blockFace.getY(), blockFace.getZ()));
            System.out.println(calc.x + " " + rotations.x);
//            targetYaw = rotations.x;
            targetYaw = calc.x;
            targetPitch = 78.1F;
        }else{
            found = true;
            // target yaw and pitch stays the same
        }

    }

    public Vec3 getHitVec() {
        /* Correct HitVec */
        Vec3 hitVec = new Vec3(blockFace.getX() + Math.random(), blockFace.getY() + Math.random(), blockFace.getZ() + Math.random());

        final MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(RotationManager.rotations, mc.playerController.getBlockReachDistance());

        switch (enumFacing.getEnumFacing()) {
            case DOWN:
                hitVec.yCoord = blockFace.getY() + 0;
                break;

            case UP:
                hitVec.yCoord = blockFace.getY() + 1;
                break;

            case NORTH:
                hitVec.zCoord = blockFace.getZ() + 0;
                break;

            case EAST:
                hitVec.xCoord = blockFace.getX() + 1;
                break;

            case SOUTH:
                hitVec.zCoord = blockFace.getZ() + 1;
                break;

            case WEST:
                hitVec.xCoord = blockFace.getX() + 0;
                break;
        }

        if (movingObjectPosition != null && movingObjectPosition.getBlockPos().equals(blockFace) &&
                movingObjectPosition.sideHit == enumFacing.getEnumFacing()) {
            hitVec = movingObjectPosition.hitVec;
        }

        return hitVec;
    }
}