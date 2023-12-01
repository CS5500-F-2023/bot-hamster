package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import org.junit.jupiter.api.Test;

class PokemonControllerTest {
    PokemonController getPokemonController() {
        return new PokemonController(new InMemoryRepository<>());
    }

    @Test
    void testUpdatePokemonHP() {
        PokemonController pokemonController = getPokemonController();
        Pokemon pokemon = pokemonController.catchPokemon(1);
        pokemonController.updatePokemonHP(pokemon, 1);
        assertThat(pokemon.getHp()).isEqualTo(46);
    }

    @Test
    void testUpdatePokemonMood() {
        PokemonController pokemonController = getPokemonController();
        Pokemon pokemon = pokemonController.catchPokemon(1);
        pokemonController.updatePokemonMood(pokemon, 3);
        assertThat(pokemon.getMood()).isEqualTo(3);
    }

    @Test
    void testLevelUpPokemon() {
        PokemonController pokemonController = getPokemonController();
        Pokemon pokemon = pokemonController.catchPokemon(1);
        pokemon.setMood(10);
        pokemonController.levelUpPokemon(pokemon);
        assertThat(pokemon.getLevel()).isEqualTo(1);
    }
}
