package dev.hermes.module;

import dev.hermes.Hermes;
import dev.hermes.event.EventManager;
import dev.hermes.utils.client.log.LogUtil;
import dev.hermes.utils.client.packet.PacketUtils;
import dev.hermes.value.Value;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Module {
    public Minecraft mc = Minecraft.getMinecraft();
    public String moduleName;
    public Catagory catagory;
    @Getter
    public String description;
    public boolean alowdisable;
    public int keybinding = 0;
    public boolean toggled;

    public Module(String moduleName, Catagory catagory) {
        this.moduleName = moduleName;
        this.catagory = catagory;
        this.toggled = false;
        this.alowdisable = true;
        this.keybinding = 0;
        this.description = "No Description";

    }

    //simplest form
    public Module(String moduleName, String description,Catagory catagory) {
        this.moduleName = moduleName;
        this.catagory = catagory;
        this.toggled = false;
        this.alowdisable = true;
        this.keybinding = 0;
        this.description = description;

    }

    // overall form

    public Module(String moduleName, String description, Catagory catagory,boolean allowdisable, boolean toggled, int keybinding) {
        this.moduleName = moduleName;
        this.catagory = catagory;
        this.toggled = toggled;
        this.alowdisable = allowdisable;
        this.keybinding = keybinding;
        this.description = description;

    }

    public Module(String moduleName, String description,Catagory catagory,boolean toggled) {
        this.moduleName = moduleName;
        this.catagory = catagory;
        this.toggled = toggled;
        this.alowdisable = true;
        this.keybinding = 0;
        this.description = description;
    }



    public void onInitialize() {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void toggle() {
        try {
            if (toggled) {
                if(this.alowdisable == false){
                    LogUtil.printLog("does not allow disable");
                }else{
                    toggled = false;
                    EventManager.unregister(this);
                    onDisable();
                }
            } else {
                toggled = true;
                EventManager.register(this);
                onEnable();
            }
            mc.thePlayer.playSound("random.click", 0.5F, 1F);
            // ClientUtils.showNotification("Module", moduleName + " was " + (toggled ? "Enabled" : "Disabled"));
        } catch (Exception exception) {
            if (Hermes.DEVELOPMENT_SWITCH) {
                exception.printStackTrace();
            }
        }
    }

    public List<Value<?>> getValues() {
        List<Value<?>> values = new ArrayList<>();
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Value.class.isAssignableFrom(field.getType())) {
                try {
                    field.setAccessible(true);
                    Value<?> value = (Value<?>) field.get(this);
                    values.add(value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return values;
    }

    public Value<?> getValueByName(String name) {
        return getValues().stream()
                .filter(value -> value.getName().equals(name))
                .findFirst()
                .orElse(null);
    }


    public String getName() {
        return this.moduleName;
    }

    public Catagory getCategory() {
        return this.catagory;
    }

    public int getBind() {
        return this.keybinding;
    }

    public boolean isToggled() {
        return this.toggled;
    }


    public boolean isallowdisabled() {
        return this.alowdisable;
    }

    public void sendPacket(Packet<?> packet) {
        PacketUtils.sendPacket(packet);
    }

    public void sendPacketNoEvent(Packet<?> packet) {
        PacketUtils.sendPacketNoEvent(packet);
    }
}
