package edu.northeastern.cs5500.starterbot.model;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

class TrainerTest {

    @Test
    void testTrainerCreation() {
        Trainer trainer = new Trainer();
        ObjectId id = new ObjectId();
        String discordUserId = "123456789";
        int pokeBallQuantity = 5;
        int coinBalance = 100;
        List<ObjectId> pokemonInventory =
                Arrays.asList(new ObjectId(), new ObjectId(), new ObjectId());

        trainer.setId(id);
        trainer.setDiscordUserId(discordUserId);
        trainer.setPokeBallQuantity(pokeBallQuantity);
        trainer.setCoinBalance(coinBalance);
        trainer.setPokemonInventory(pokemonInventory);

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
}
