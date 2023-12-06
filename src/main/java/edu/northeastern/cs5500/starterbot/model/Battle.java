package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnull;
import lombok.Data;

@Data
public class Battle {
    @Nonnull String trainerId;
    @Nonnull String opponentId;
    @Nonnull String myPokemonId;
    @Nonnull String opponentPokemonId;
}
