package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnull;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class Offer {
    @Nonnull ObjectId trainerId;

    @Nonnull ObjectId otherTrainerId;

    @Nonnull ObjectId pokemonId;

    @Nonnull ObjectId otherPokemonId;
}
