package edu.northeastern.cs5500.starterbot.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

public class PokemonTest {
    @Test
    void testPokemonBuilder() {
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

        assertThat(pokemon.getPokedexNumber()).isEqualTo(1);
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
}
