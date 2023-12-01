package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnull;

public enum PokemonType {
    FIRE("Fire", "🔥"),
    WATER("Water", "💧"),
    GRASS("Grass", "🌱"),
    NORMAL("Normal", "😓");

    @Nonnull String name;

    @Nonnull String emoji;

    PokemonType(@Nonnull String name, @Nonnull String emoji) {
        this.name = name;
        this.emoji = emoji;
    }

    /**
     * Get PokemonType from a string.
     *
     * @param typeString the string representation of the Pokemon type
     * @return the corresponding PokemonType, or null if not found
     */
    public static PokemonType getType(String typeString) {
        for (PokemonType type : values()) {
            if (type.name().equalsIgnoreCase(typeString)) {
                return type;
            }
        }
        return null;
    }
}
