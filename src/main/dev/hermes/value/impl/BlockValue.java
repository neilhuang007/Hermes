package dev.hermes.value.impl;

import dev.hermes.value.Function0;

public class BlockValue extends IntValue {

    public BlockValue(String name, Integer value, Function0<Boolean> displayable) {
        super(name, value, 1, 197, displayable);
    }

    public BlockValue(String name, Integer value) {
        this(name, value, () -> true);
    }
}
