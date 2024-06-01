package dev.hermes.module.impl.ghost;


import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.render.EventRender2D;
import dev.hermes.event.events.impl.render.EventRender3D;
import dev.hermes.event.events.impl.world.EventTick;
import dev.hermes.event.events.impl.world.EventUpdate;
import dev.hermes.manager.MathManager;
import dev.hermes.manager.RotationManager;
import dev.hermes.manager.TargetManager;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.module.value.impl.*;
import dev.hermes.utils.RayCastUtil;
import dev.hermes.utils.player.PlayerUtil;
import dev.hermes.utils.rotation.MovementFix;
import dev.hermes.utils.vector.Vector2f;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author Alan
 * @since 29/01/2021
 */

@Hermes
@ModuleInfo(name = "AimAssist", description = "AimAssist", category = Category.GHOST)
public class AimAssist extends Module {

    private final Comparator<Entity> angleComparator = Comparator.comparingDouble(entity -> RotationManager.getRotationsNeeded(entity)[0]);

    private final NumberValue Range = new NumberValue("Aim Range",this,5,3,10,1);

    public final ModeValue TargetPrority = new ModeValue("Target Prority", this)
            .add(new SubMode("Clip"))
            .add(new SubMode("Health"))
            .add(new SubMode("Distance"))
            .add(new SubMode("Angle"))
            .setDefault("Clip");

    private final BooleanValue Weapons = new BooleanValue("Only Weapons",this,false);

    private final BooleanValue View = new BooleanValue("In View",this,true);

    private final BoundsNumberValue HorizontalStrength = new BoundsNumberValue("Horizontal Strength", this, 15, 45, 1, 100, 1);
    private final BoundsNumberValue VerticalStrength = new BoundsNumberValue("Vertical Strength", this, 15, 45, 1, 100, 1);

    private final NumberValue PitchOffset = new NumberValue("Above or below waist",this,0.15, -1.7, 0.25, 0.050D);


    private final BooleanValue Render = new BooleanValue("Render Target", this, true);

    private final ColorValue ESPcolor = new ColorValue("ESP Color",this, Color.WHITE, () -> !Render.getValue());

    Entity target;

    private float randomYaw, randomPitch;

    RotationManager rotationManager = new RotationManager();



    // do render
    @EventTarget
    public void onUpdate(EventUpdate eventUpdate){
        if(!TargetPrority.getValue().getName().contains("Clip")){
            target = getTargets();
        }
    };



    @EventTarget
    public void ontick(EventTick eventTick){
        if(TargetPrority.getValue().getName().contains("Clip")){
            try{
                target = PlayerUtil.getMouseOver(1,Range.getValue().intValue());
            }catch (NullPointerException e){
                // just catch just in case
            }
        }
    };

    @EventTarget
    public void onRender(EventRender2D event){
        Vector2f rotations;
        if(!TargetPrority.getValue().getName().contains("Clip")){
            target = getTargets();

        }
        if(target != null){
            try{
                final Vector2f targetRotations = rotationManager.calculate(target);
                double n = PlayerUtil.fovFromEntity(target);

                double complimentSpeedX = n
                        * (ThreadLocalRandom.current().nextDouble(HorizontalStrength.getSecondValue().doubleValue() - 1.47328,
                        HorizontalStrength.getSecondValue().doubleValue() + 2.48293) / 100);
                float valX = (float) (-(complimentSpeedX + (n / (101.0D - (float) ThreadLocalRandom.current()
                        .nextDouble(HorizontalStrength.getValue().doubleValue() - 4.723847, HorizontalStrength.getValue().doubleValue())))));

                double ry = mc.thePlayer.rotationYaw;
                // you want to handle a variable to smooth instead of adding in a function because that changes the yaw and becomes very weird
                //mc.thePlayer.rotationYaw += valX;
                rotations = RotationManager.smoothrotations(new Vector2f((float) ry, mc.thePlayer.rotationPitch),(new Vector2f((float) ry+valX, mc.thePlayer.rotationPitch)),MathManager.getRandom(HorizontalStrength.getValue().doubleValue(),HorizontalStrength.getSecondValue().doubleValue()));
                mc.thePlayer.rotationYaw = rotations.x;

                double complimentSpeed = PlayerUtil.PitchFromEntity(target,
                        (float) PitchOffset.getValue().doubleValue())
                        * (ThreadLocalRandom.current().nextDouble(VerticalStrength.getSecondValue().doubleValue() - 1.47328,
                        VerticalStrength.getSecondValue().doubleValue() + 2.48293) / 100);

                float val = (float) (-(complimentSpeed
                        + (n / (101.0D - (float) ThreadLocalRandom.current()
                        .nextDouble(VerticalStrength.getValue().doubleValue() - 4.723847,
                                VerticalStrength.getValue().doubleValue())))));

                mc.thePlayer.rotationPitch += val;


//
//                // rotations that bypasses checks
                randomiseTargetRotations();
                targetRotations.x += randomYaw;
                targetRotations.y += randomPitch;
                RotationManager.setRotations(targetRotations, MathManager.getRandom(HorizontalStrength.getSecondValue().doubleValue(),HorizontalStrength.getValue().doubleValue()), MovementFix.NORMAL);
                RotationManager.smooth();
            }catch (NullPointerException e){
                // this is for when joining world
            }



        }
    };

    public Entity getTargets(){
        // define targets first to eliminate any null pointer exceptions
        List<Entity> targets = TargetManager.getTargets(Range.getValue().intValue());
        if(View.getValue()){
            targets = TargetManager.getTargets(Range.getValue().intValue()).stream()
                    .filter(entity -> RayCastUtil.inView(entity))
                    .collect(Collectors.toList());
        }
        if(TargetPrority.getValue().getName().contains("Health")){
            targets = TargetManager.getTargets(Range.getValue().intValue()).stream()
                    .sorted(Comparator.comparingDouble(o -> ((AbstractClientPlayer) o).getHealth()).reversed())
                    .collect(Collectors.toList());
        }
        if(TargetPrority.getValue().getName().contains("Distance")) {
            targets = TargetManager.getTargets(Range.getValue().intValue()).stream()
                    .sorted(Comparator.comparingDouble(o -> mc.thePlayer.getDistanceToEntity((Entity) o)).reversed())
                    .collect(Collectors.toList());
        }
        if(TargetPrority.getValue().getName().contains("Angle")) {
            targets = TargetManager.getTargets(Range.getValue().intValue());
            targets.sort(this.angleComparator);
        }

        if(!targets.isEmpty()){
            return targets.get(0);
        }else{
            return null;
        }
    };

    /*
     * Randomising rotation target to simulate legit players
     */
    private void randomiseTargetRotations() {
        randomYaw += (float) (Math.random() * 0.2 - 0.1);
        randomPitch += (float) (Math.random() - 0.5f) * 2;
    }

    private float[] smoothAngle(float[] dst, float[] src) {
        float[] smoothedAngle = new float[2];
        smoothedAngle[0] = (src[0] - dst[0]);
        smoothedAngle[1] = (src[1] - dst[1]);
        smoothedAngle = MathManager.constrainAngle(smoothedAngle);
        smoothedAngle[0] = (float) (src[0] - smoothedAngle[0] / 100 * MathManager.getRandom(14, 24));
        smoothedAngle[1] = (float) (src[1] - smoothedAngle[1] / 100 * MathManager.getRandom(3, 8));
        return smoothedAngle;
    }
    private float[] getRotationsToEnt(Entity ent, EntityPlayerSP playerSP) {
        final double differenceX = ent.posX - playerSP.posX;
        final double differenceY = (ent.posY + ent.height) - (playerSP.posY + playerSP.height);
        final double differenceZ = ent.posZ - playerSP.posZ;
        final float rotationYaw = (float) (Math.atan2(differenceZ, differenceX) * 180.0D / Math.PI) - 90.0f;
        final float rotationPitch = (float) (Math.atan2(differenceY, playerSP.getDistanceToEntity(ent)) * 180.0D / Math.PI);
        final float finishedYaw = playerSP.rotationYaw + MathHelper.wrapAngleTo180_float(rotationYaw - playerSP.rotationYaw);
        final float finishedPitch = playerSP.rotationPitch + MathHelper.wrapAngleTo180_float(rotationPitch - playerSP.rotationPitch);
        return new float[]{finishedYaw, -finishedPitch};
    }
}