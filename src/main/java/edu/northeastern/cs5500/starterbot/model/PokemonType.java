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

    // ???
    @Nonnull
    public static PokemonType[] getSingleTypeArray(PokemonType type) {
        PokemonType[] types = new PokemonType[1];
        types[0] = type;
        return types;
    }
}
