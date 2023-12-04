package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnull;
import lombok.Data;

@Data
public class Offer {
    @Nonnull String trainerId;

    @Nonnull String otherTrainerId;

    @Nonnull String pokemonId;

    @Nonnull String otherPokemonId;
}
