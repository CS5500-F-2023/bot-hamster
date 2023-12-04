package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnull;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class TradeOffer {
    @Nonnull ObjectId trainerId;

    @Nonnull ObjectId pokemonId;

    @Nonnull ObjectId otherTrainerId;

    @Nonnull ObjectId otherPokemonId;
}
