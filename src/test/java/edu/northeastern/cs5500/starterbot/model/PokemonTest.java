package edu.northeastern.cs5500.starterbot.model;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

public class PokemonTest {
    ObjectId pokemonId = new ObjectId();

    Pokemon pokemon =
            Pokemon.builder()
                    .pokedexNumber(1)
                    .hp(100)
                    .attack(50)
                    .defense(30)
                    .specialAttack(40)
                    .specialDefense(30)
                    .speed(60)
                    .total(250)
                    .build();

    @Test
    void testPokemonBuilder() {
        pokemon.setId(pokemonId);
        pokemon.setPokedexNumber(10);

        assertThat(pokemon.getId()).isEqualTo(pokemonId);
        assertThat(pokemon.getPokedexNumber()).isEqualTo(10);
        // Default Value
        assertThat(pokemon.getLevel()).isEqualTo(0);
        assertThat(pokemon.getMood()).isEqualTo(0);
        assertThat(pokemon.getHp()).isEqualTo(100);
        assertThat(pokemon.getAttack()).isEqualTo(50);
        assertThat(pokemon.getDefense()).isEqualTo(30);
        assertThat(pokemon.getSpecialAttack()).isEqualTo(40);
        assertThat(pokemon.getSpecialDefense()).isEqualTo(30);
        assertThat(pokemon.getSpeed()).isEqualTo(60);
        assertThat(pokemon.getTotal()).isEqualTo(250);
    }

    @Test
    void testSetPokedexNumberNullValue() {
        try {
            pokemon.setPokedexNumber(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("pokedexNumber is marked non-null but is null");
        }
    }

    @Test
    void testSetLevelNullValue() {
        try {
            pokemon.setLevel(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("level is marked non-null but is null");
        }
    }

    @Test
    void testSetMoodNullValue() {
        try {
            pokemon.setMood(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("mood is marked non-null but is null");
        }
    }

    @Test
    void testSetHpNullValues() {
        try {
            pokemon.setHp(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("hp is marked non-null but is null");
        }
    }

    @Test
    void testSetAttackNullValue() {
        try {
            pokemon.setAttack(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("attack is marked non-null but is null");
        }
    }

    @Test
    void testSetSpecialAttackNullValue() {
        try {
            pokemon.setSpecialAttack(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("specialAttack is marked non-null but is null");
        }
    }

    @Test
    void testSetSpecialDefenseNullValue() {
        try {
            pokemon.setSpecialDefense(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("specialDefense is marked non-null but is null");
        }
    }

    @Test
    void testSetSpeedNullValue() {
        try {
            pokemon.setSpeed(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("speed is marked non-null but is null");
        }
    }

    @Test
    void testSetTotaldNullValue() {
        try {
            pokemon.setTotal(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("total is marked non-null but is null");
        }
    }
}
