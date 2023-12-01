package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.PokemonSpecies;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies.PokemonSpeciesBuilder;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PokedexController {
    private final Map<Integer, PokemonSpecies> pokemonSpeciesMap;
    private final String path =
            "src/main/java/edu/northeastern/cs5500/starterbot/data/pokemon_dataset.csv";

    @Inject
    PokedexController() {
        this.pokemonSpeciesMap = loadPokemonData(path);
    }

    @Nonnull
    public PokemonSpecies getPokemonSpeciesByNumber(int pokedexNumber) {
        return Objects.requireNonNull(pokemonSpeciesMap.get(pokedexNumber));
    }

    private Map<Integer, PokemonSpecies> loadPokemonData(String filename) {
        Map<Integer, PokemonSpecies> map = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            reader.readLine(); // Skip header

            String line;
            while ((line = reader.readLine()) != null) {
                // parse and read data
                String[] data = line.split(",");
                int pokedexNumber = Integer.parseInt(data[0]);
                String name = data[1];
                String imageUrl =
                        String.format(
                                "https://assets.pokemon.com/assets/cms2/img/pokedex/full/%s.png",
                                pokedexNumber);

                // build PokemonSpecies
                PokemonSpeciesBuilder builder =
                        PokemonSpecies.builder()
                                .pokedexNumber(pokedexNumber)
                                .name(name)
                                .imageUrl(imageUrl);

                map.put(pokedexNumber, builder.build());
            }
        } catch (IOException e) {
            // Handle the exception or log it using a logging framework
            e.printStackTrace();
        }
        return map;
    }
}
