package edu.northeastern.cs5500.starterbot.model;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class Battle {
    ObjectId trainer;
    ObjectId opponent;
    ObjectId myPokemon;
    ObjectId opponentPokemon;
}
