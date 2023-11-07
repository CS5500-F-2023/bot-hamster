package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PokemonSpecies {
    @Nonnull final Integer pokedexNumber;

    @Nonnull final String imageUrl;

    @Nonnull final String name;

    @Nonnull final PokemonType[] types;
}
