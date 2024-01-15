package dev.hermes.module.api;


import lombok.Getter;

/**
 * @author Patrick
 * @since 10/19/2021
 */
@Getter
public enum Category {
    COMBAT("combat"),
    MOVEMENT("movement"),
    PLAYER("player"),
    RENDER("render"),
    EXPLOIT("exploit"),
    GHOST("ghost"),
    OTHER("other");
//    SCRIPT("script"),
//
//    THEME("themes"),


    // name of category (in case we don't use enum names)
    private final String name;

    Category(final String name) {
        this.name = name;
        
    }
    public String getName() {
        return name;
    }

}