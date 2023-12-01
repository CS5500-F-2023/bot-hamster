package edu.northeastern.cs5500.starterbot.model;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;

@Builder
@Data
@AllArgsConstructor
public class Pokemon implements Model {
    @Nonnull @Builder.Default ObjectId id = new ObjectId();

    @Nonnull Integer pokedexNumber;

    @Nonnull @Builder.Default Integer level = 0;

    @Nonnull Integer mood;

    @Nonnull @Builder.Default Integer mood = 0;

    @Nonnull @Nonnegative Integer hp;

    @Nonnull Integer attack;

    @Nonnull Integer defense;

    @Nonnull Integer specialAttack;

    @Nonnull Integer specialDefense;

    @Nonnull Integer speed;

    @Nonnull Integer total;

    public Pokemon() {}
}
