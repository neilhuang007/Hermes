package dev.hermes.utils.rotation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MovementFix {
    OFF("Off"),
    NORMAL("Razer"),
    TRADITIONAL("Traditional"),
    BACKWARDS_SPRINT("Backwards Sprint");

    @Getter
    String name;

    @Override
    public String toString() {
        return name;
    }
}
