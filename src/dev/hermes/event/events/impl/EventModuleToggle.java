package dev.hermes.event.events.impl;

import dev.hermes.event.events.Event;
import dev.hermes.module.Module;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventModuleToggle implements Event {
    Module module;
}
