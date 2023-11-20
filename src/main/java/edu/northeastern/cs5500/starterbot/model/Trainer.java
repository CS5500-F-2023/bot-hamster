package edu.northeastern.cs5500.starterbot.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class Trainer implements Model {
    ObjectId id;

    // This is the "snowflake id" of the user
    // e.g. event.getUser().getId()
    String discordUserId;

    int pokeBallQuantity;

    int coinBalance;

    Map<String, ObjectId> pokemonInventory = new HashMap<>();
}
