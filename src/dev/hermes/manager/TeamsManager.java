package dev.hermes.manager;

import lombok.Getter;
import net.minecraft.entity.Entity;

import java.util.HashMap;

@Getter
public class TeamsManager extends Manager{

    @Getter
    public static HashMap<String, Entity> teams = new HashMap<>();


}
