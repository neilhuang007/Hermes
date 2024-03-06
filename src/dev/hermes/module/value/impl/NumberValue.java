package dev.hermes.module.value.impl;


import dev.hermes.module.Module;
import dev.hermes.module.value.Mode;
import dev.hermes.module.value.Value;
import lombok.Getter;

import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * @author Patrick
 * @since 10/19/2021
 */
@Getter
public class NumberValue extends Value<Number> {

    private final Number min;
    private final Number max;
    private final Number decimalPlaces;
    private String suffix = "";

    public NumberValue(final String name, final Module parent, final Number defaultValue,
                       final Number min, final Number max, final Number decimalPlaces,final String suffix) {
        super(name, parent, defaultValue);
        this.decimalPlaces = decimalPlaces;
        this.min = min;
        this.max = max;
        this.suffix = suffix;
    }

    public NumberValue(final String name, final Module parent, final Number defaultValue,
                       final Number min, final Number max, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue,hideIf);
        this.decimalPlaces = 0.1;
        this.min = min;
        this.max = max;
        this.suffix = "";
    }

    public NumberValue(final String name, final Mode<?> parent, final Number defaultValue,
                       final Number min, final Number max, final Number decimalPlaces) {
        super(name, parent, defaultValue);
        this.decimalPlaces = decimalPlaces;

        this.min = min;
        this.max = max;
    }

    public NumberValue(final String name, final Module parent, final Number defaultValue,
                       final Number min, final Number max, final Number decimalPlaces, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
        this.decimalPlaces = decimalPlaces;

        this.min = min;
        this.max = max;
    }

    public NumberValue(final String name, final Module parent, final Number defaultValue,
                       final Number min, final Number max, final Number decimalPlaces) {
        super(name, parent, defaultValue);
        this.decimalPlaces = decimalPlaces;

        this.min = min;
        this.max = max;
    }

    public NumberValue(final String name, final Mode<?> parent, final Number defaultValue,
                       final Number min, final Number max, final Number decimalPlaces, final BooleanSupplier hideIf) {
        super(name, parent, defaultValue, hideIf);
        this.decimalPlaces = decimalPlaces;

        this.min = min;
        this.max = max;
    }

    @Override
    public List<Value<?>> getSubValues() {
        return null;
    }
}