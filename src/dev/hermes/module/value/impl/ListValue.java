package dev.hermes.module.value.impl;


import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import dev.hermes.module.Module;
import dev.hermes.module.value.Mode;
import dev.hermes.module.value.Value;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BooleanSupplier;

@Getter
public class ListValue<T> extends Value<T> {

    private final List<T> modes = new ArrayList<>();

    public ListValue(final String name, final Module parent) {
        super(name, parent, null);
    }

    public ListValue(final String name, final Mode<?> parent) {
        super(name, parent, null);
    }

    public ListValue(final String name, final Module parent, final BooleanSupplier hideIf) {
        super(name, parent, null, hideIf);
    }

    public ListValue(final String name, final Mode<?> parent, final BooleanSupplier hideIf) {
        super(name, parent, null, hideIf);
    }

    public ListValue<T> add(final T... modes) {
        if (modes == null) {
            return this;
        }

        this.modes.addAll(Arrays.asList(modes));
        return this;
    }

    public ListValue<T> setDefault(final int index) {
        setValue(modes.get(index));
        return this;
    }

    public ListValue<T> setDefault(final T mode) {
        setValue(mode);
        return this;
    }

    @Override
    public List<Value<?>> getSubValues() {
        return null;
    }


    // Method to get all mode names as a JSON array
    public JsonArray getSubValuesAsJson() {
        JsonArray subValues = new JsonArray();

        for (T mode : modes) {
            // Assuming mode.toString() returns the name of the mode
            subValues.add(new JsonPrimitive(mode.toString()));
        }

        return subValues;
    }

}