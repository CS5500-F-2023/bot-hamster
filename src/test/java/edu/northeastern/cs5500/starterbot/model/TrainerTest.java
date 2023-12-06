package edu.northeastern.cs5500.starterbot.model;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrainerTest {
    private Trainer trainer;
    private String discordUserId = "123456789";
    private int pokeBallQuantity = 5;
    private int coinBalance = 100;
    private ObjectId id = new ObjectId("657015b9b3389c20f3a52d77");

    List<ObjectId> pokemonInventory =
            Arrays.asList(
                    new ObjectId("657015b9b3389c20f3a52d74"),
                    new ObjectId("657015b9b3389c20f3a52d75"),
                    new ObjectId("657015b9b3389c20f3a52d76"));

    @BeforeEach
    void setUp() {
        trainer = new Trainer();

        trainer.setId(id);
        trainer.setDiscordUserId(discordUserId);
        trainer.setPokeBallQuantity(pokeBallQuantity);
        trainer.setCoinBalance(coinBalance);
        trainer.setPokemonInventory(pokemonInventory);
    }

    @Test
    void testTrainerCreation() {
        assertThat(trainer.getId()).isEqualTo(id);
        assertThat(trainer.getDiscordUserId()).isEqualTo(discordUserId);
        assertThat(trainer.getPokeBallQuantity()).isEqualTo(pokeBallQuantity);
        assertThat(trainer.getCoinBalance()).isEqualTo(coinBalance);
        assertThat(trainer.getPokemonInventory()).isEqualTo(pokemonInventory);
    }

    @Test
    void testTrainerDefaultValues() {
        Trainer trainer = new Trainer();

        assertThat(trainer.getId()).isNull();
        assertThat(trainer.getDiscordUserId()).isNull();
        assertThat(trainer.getPokeBallQuantity()).isEqualTo(0);
        assertThat(trainer.getCoinBalance()).isEqualTo(0);
        assertThat(trainer.getPokemonInventory()).isEmpty();
    }

    @Test
    void testEquals_comparisonToSelf() {
        assertThat(trainer).isEqualTo(trainer);
    }

    @Test
    void testEquals_nullComparison() {
        assertThat(trainer).isNotEqualTo(null);
    }

    @Test
    void testEquals_differentDataTypes() {
        Object differentTrainer = new Object();
        assertThat(trainer).isNotEqualTo(differentTrainer);
    }

    @Test
    void testToString() {
        String expectedString =
                "Trainer(id=657015b9b3389c20f3a52d77, discordUserId=123456789, pokeBallQuantity=5, coinBalance=100, pokemonInventory=[657015b9b3389c20f3a52d74, 657015b9b3389c20f3a52d75, 657015b9b3389c20f3a52d76])";
        assertEquals(expectedString, trainer.toString());
    }
}
