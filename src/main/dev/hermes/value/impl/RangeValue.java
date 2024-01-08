package dev.hermes.value.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.hermes.value.Function0;
import dev.hermes.value.Value;
import lombok.Getter;

@Getter
public class RangeValue extends Value<Float> {
    private final Float minimum;
    private final Float maximum;
    private final String suffix;
    private final Float step;

    public RangeValue(String name, Float value, Float minimum, Float maximum, Float step, String suffix,
                      Function0<Boolean> displayable) {
        super(name, value, displayable);
        this.minimum = minimum;
        this.maximum = maximum;
        this.suffix = suffix;
        this.step = step;
    }

    public RangeValue(String name, Float value, Float minimum, Float maximum, Float step) {
        this(name,value,minimum,maximum,step,"",() -> true);
    }

    public RangeValue(String name, Float value, Float minimum, Float maximum) {
        this(name,value,minimum,maximum, 0.1F,"",() -> true);
    }

    public RangeValue(String name, Float value, Float minimum, Float maximum, Function0<Boolean> displayable) {
        this(name,value,minimum,maximum, 0.1F,"",displayable);
    }

    public RangeValue(String name, Float value, Float minimum, Float maximum, Float step, Function0<Boolean> displayable) {
        this(name,value,minimum,maximum, step,"",displayable);
    }

//    public FloatValue(String name, Float value, Float minimum, Float maximum,
//                      Function0<Boolean> displayable) {
//        this(name, value, minimum, maximum,0.1, "", displayable);
//    }
//
//    public FloatValue(String name, Float value, Float minimum, Float maximum, String suffix) {
//        this(name, value, minimum, maximum, suffix, () -> true);
//    }
//
//    public FloatValue(String name, Float value, Float minimum, Float maximum) {
//        this(name, value, minimum, maximum, "", () -> true);
//    }

    public void set(Number newValue) {
        set(newValue.floatValue());
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element.isJsonPrimitive()) {
            setValue(element.getAsFloat());
        }
    }
}
