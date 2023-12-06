package edu.northeastern.cs5500.starterbot.model;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class OfferTest {
    final String trainerId = "trainer1";
    final String otherTrainerId = "trainer2";
    final String newTrainerId = "trainer3";
    final String newOtherTrainerId = "trainer4";

    final String pokemonId = "pokemon1";
    final String otherPokemonId = "pokemon2";
    final String newPokemonId = "pokemon3";
    final String newOtherPokemonId = "pokemon4";

    Offer offer = new Offer("trainer1", "trainer2", "pokemon1", "pokemon2");

    @Test
    void testOfferCreation() {
        assertThat(offer.getTrainerId()).isEqualTo(trainerId);
        assertThat(offer.getOtherTrainerId()).isEqualTo(otherTrainerId);
        assertThat(offer.getPokemonId()).isEqualTo(pokemonId);
        assertThat(offer.getOtherPokemonId()).isEqualTo(otherPokemonId);
    }

    @Test
    void testOfferSetters() {
        offer.setTrainerId(newTrainerId);
        offer.setOtherTrainerId(newOtherTrainerId);
        offer.setPokemonId(newPokemonId);
        offer.setOtherPokemonId(newOtherPokemonId);

        assertThat(offer.getTrainerId()).isEqualTo(newTrainerId);
        assertThat(offer.getOtherTrainerId()).isEqualTo(newOtherTrainerId);
        assertThat(offer.getPokemonId()).isEqualTo(newPokemonId);
        assertThat(offer.getOtherPokemonId()).isEqualTo(newOtherPokemonId);
    }

    @Test
    void testSetTrainerIdNullValue() {
        try {
            offer.setTrainerId(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("trainerId is marked non-null but is null");
        }
    }

    @Test
    void testSetOtherTrainerIdNullValue() {
        try {
            offer.setOtherTrainerId(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("otherTrainerId is marked non-null but is null");
        }
    }

    @Test
    void testSetPokemonIdNullValue() {
        try {
            offer.setPokemonId(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("pokemonId is marked non-null but is null");
        }
    }

    @Test
    void testSetOtherPokemonIdNullValue() {
        try {
            offer.setOtherPokemonId(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("otherPokemonId is marked non-null but is null");
        }
    }

    @Test
    void testEquals_comparisonToSelf() {
        assertThat(offer).isEqualTo(offer);
    }

    @Test
    void testEquals_nullComparison() {
        assertThat(offer).isNotEqualTo(null);
    }

    @Test
    void testEquals_differentDataTypes() {
        Object differentOffer = new Object();
        assertThat(offer).isNotEqualTo(differentOffer);
    }

    @Test
    void testToString() {
        String expectedString =
                "Offer(trainerId=trainer1, otherTrainerId=trainer2, pokemonId=pokemon1, otherPokemonId=pokemon2)";
        assertEquals(expectedString, offer.toString());
    }
}
