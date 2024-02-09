package dev.hermes.module.impl.combat;

import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.world.EventTick;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.utils.rotation.MovementFix;
import dev.hermes.module.value.impl.*;

@Hermes
@ModuleInfo(name = "KillAura", description = "KA", category = Category.COMBAT)
public final class KillAura extends Module {

    private final ModeValue mode = new ModeValue("Attack Mode", this)
            .add(new SubMode("Single"))
            .add(new SubMode("Switch"))
            .add(new SubMode("Multiple"))
            .setDefault("Single");


    private final BoundsNumberValue cps = new BoundsNumberValue("CPS", this, 10, 15, 1, 20, 1);

    private final StringValue runMovementFixIfNot = new StringValue("Exclude MovementCorrection if", this, "");

    private final BooleanValue attackWhilstScaffolding = new BooleanValue("Attack whilst Scaffolding", this, false);

    private final ListValue<MovementFix> movementCorrection = new ListValue<>("Movement correction", this);
    private NumberValue height = new NumberValue("Height", this, 1, 0.1, 10, 0.1);


    public KillAura() {
        for (MovementFix movementFix : MovementFix.values()) {
            movementCorrection.add(movementFix);
        }

        movementCorrection.setDefault(MovementFix.OFF);
    }

    @EventTarget
    public void onTick(EventTick eventTick) {
//        System.out.println("TICK");
//        System.out.println(dev.hermes.Hermes.moduleManager.getAll());
    }

    @Override
    public void onEnable(){
        System.out.println("KA ENABLED");
    }

    @Override
    public void onDisable() {
        System.out.println("KA DISABLE");
    }
}
