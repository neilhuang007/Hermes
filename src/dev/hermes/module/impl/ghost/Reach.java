package dev.hermes.module.impl.ghost;

import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.Combat.EventMouseOver;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.value.impl.BooleanValue;
import dev.hermes.module.value.impl.NumberValue;
import net.minecraft.item.ItemSword;

@Hermes
@dev.hermes.module.api.ModuleInfo(name = "Reach", description = "Increases reach distance", category = Category.GHOST)
public class Reach extends Module {

    public final NumberValue Reach = new NumberValue("Reach", this, 3, 3, 6, 0.01);

    public final BooleanValue OnlySword = new BooleanValue("Only Sword", this, false);

    @EventTarget
    public void onMouseOver(EventMouseOver event){
        if(OnlySword.getValue() && !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) && mc.thePlayer.getCurrentEquippedItem().getItem() == null){
            event.setRange(3);
        }else{
            event.setRange(Reach.getValue().doubleValue());
        }
    }
}
