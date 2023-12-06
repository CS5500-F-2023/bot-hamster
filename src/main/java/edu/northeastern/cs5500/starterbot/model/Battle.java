package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnull;
import lombok.Data;

@Data
public class Battle {
    @Nonnull String trainer;
    @Nonnull String opponent;
    @Nonnull String myPokemon;
    @Nonnull String opponentPokemon;
}
