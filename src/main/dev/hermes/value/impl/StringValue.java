package dev.hermes.value.impl;


import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.hermes.value.Function0;
import dev.hermes.value.Value;
import lombok.Getter;

@Getter
public class StringValue extends Value<String> {

    private String value;
    private final String suffix;

    public StringValue(String name, String value, String suffix, Function0<Boolean> displayable){
        super(name, value, displayable);
        this.value = value;
        this.suffix = suffix;
    }

    public StringValue(String name, String Value){
        super(name, Value, () -> true);
        this.value = Value;
        this.suffix = "";
    }

    public StringValue(String name, String Value,String suffix){
        super(name, Value, () -> true);
        this.value = Value;
        this.suffix = suffix;
    }

    public void set(String newValue) {
        set(newValue.toString());
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element.isJsonPrimitive()) {
            setValue(element.getAsString());
        }
    }
}
