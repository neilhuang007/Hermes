package dev.hermes.module.impl.combat;


import dev.hermes.api.Hermes;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.module.impl.combat.velocity.*;
import dev.hermes.module.value.impl.BooleanValue;
import dev.hermes.module.value.impl.ModeValue;
import dev.hermes.module.value.impl.NumberValue;

@Hermes
@ModuleInfo(name = "Velocity", description = "Reduces Your Velocity" /* Sorry, Tecnio. */ /* Sorry Hazsi. */, category = Category.COMBAT)
public final class Velocity extends Module {

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new StandardVelocity("Standard", this))
            .add(new LegitVelocity("Legit", this))
            .add(new WatchdogVelocity("Watchdog", this))
            .add(new SleepVelocity("Cancel",this))
            .setDefault("Standard");


    public final BooleanValue LagBackDetection = new BooleanValue("Lagback Detections", this, true, () -> !mode.getValue().getName().contains("Standard") || mode.getValue().getName().contains("Ladder") || mode.getValue().getName().contains("Grim"));
    // bruh the clickgui does not read the things properly in the mode value so has to write like this
    public final BooleanValue retoggle = new BooleanValue("retoggle", this, true,() -> !(mode.getValue().getName().contains("Standard") || mode.getValue().getName().contains("Ladder") || mode.getValue().getName().contains("Grim") && LagBackDetection.getValue()));

    public final NumberValue RetoggleDelay = new NumberValue("Retoggle Delay(ms)", this, 0.5F,0.1,3,0.1,()-> !(mode.getValue().getName().contains("Standard") || mode.getValue().getName().contains("Ladder") || mode.getValue().getName().contains("Grim") && LagBackDetection.getValue()));


    public final NumberValue LagBacks = new NumberValue("Lagbacks Disable", this, 0, 0, 50, 1, () -> !(mode.getValue().getName().contains("Standard") || mode.getValue().getName().contains("Ladder") || mode.getValue().getName().contains("Grim") && LagBackDetection.getValue()));

    public final BooleanValue onExplosion = new BooleanValue("Explosion Reduction", this, false);

    public final BooleanValue onSwing = new BooleanValue("On Swing", this, false);
    public final BooleanValue onSprint = new BooleanValue("On Sprint", this, false);



}



