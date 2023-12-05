package edu.northeastern.cs5500.starterbot.model;

import static com.google.common.truth.Truth.assertThat;
import javax.annotation.Nonnull;
import org.junit.jupiter.api.Test;

class PokemonSpeciesTest {
    @Nonnull final Integer pokedexNumber = 1;
    @Nonnull final String imageUrl = "https://example.com/pokemon1.png";
    @Nonnull final String name = "Bulbasaur";

    @Test
    void testPokemonSpeciesConstructor() {
        PokemonSpecies pokemonSpecies = new PokemonSpecies(pokedexNumber, imageUrl, name);
        
        assertThat(pokemonSpecies.getPokedexNumber()).isEqualTo(pokedexNumber);
        assertThat(pokemonSpecies.getImageUrl()).isEqualTo(imageUrl);
        assertThat(pokemonSpecies.getName()).isEqualTo(name);
    }

    @Test
    void testPokemonSpeciesEquality() {

        PokemonSpecies pokemonSpecies1 = new PokemonSpecies(pokedexNumber, imageUrl, name);
        PokemonSpecies pokemonSpecies2 = new PokemonSpecies(pokedexNumber, imageUrl, name);

        assertThat(pokemonSpecies1).isEqualTo(pokemonSpecies2);
    }

    @Test
    void testPokemonSpeciesToString() {
        PokemonSpecies pokemonSpecies = new PokemonSpecies(pokedexNumber, imageUrl, name);

        String toStringResult = pokemonSpecies.toString();

        assertThat(toStringResult).contains("pokedexNumber=" + pokedexNumber);
        assertThat(toStringResult).contains("imageUrl=" + imageUrl);
        assertThat(toStringResult).contains("name=" + name);
    }

    @Test
    void testGettersAndSetters() {
        PokemonSpecies pokemonSpecies = new PokemonSpecies(pokedexNumber, imageUrl, name);
        
        assertThat(pokemonSpecies.getPokedexNumber()).isEqualTo(pokedexNumber);
        assertThat(pokemonSpecies.getImageUrl()).isEqualTo(imageUrl);
        assertThat(pokemonSpecies.getName()).isEqualTo(name);
    }
}