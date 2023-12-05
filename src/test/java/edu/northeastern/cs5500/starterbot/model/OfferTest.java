package edu.northeastern.cs5500.starterbot.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class OfferTest {
    @Test
    void testOfferCreation() {
        String trainerId = "trainer1";
        String otherTrainerId = "trainer2";
        String pokemonId = "pokemon1";
        String otherPokemonId = "pokemon2";

        Offer offer = new Offer(trainerId, otherTrainerId, pokemonId, otherPokemonId);

        assertThat(offer.getTrainerId()).isEqualTo(trainerId);
        assertThat(offer.getOtherTrainerId()).isEqualTo(otherTrainerId);
        assertThat(offer.getPokemonId()).isEqualTo(pokemonId);
        assertThat(offer.getOtherPokemonId()).isEqualTo(otherPokemonId);
    }
}
