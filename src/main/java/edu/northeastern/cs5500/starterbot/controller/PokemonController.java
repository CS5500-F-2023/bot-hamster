package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.Pokemon.PokemonBuilder;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bson.types.ObjectId;

@Singleton
public class PokemonController {

    GenericRepository<Pokemon> pokemonRepository;
    PokedexController pokedexController;
    private Random random = new Random();
    private Map<Integer, String> pokemonDataMap = new HashMap<>();
    private final String path = "src/main/java/edu/northeastern/cs5500/starterbot/data/pokemon_dataset.csv";


    @Inject
    PokemonController(GenericRepository<Pokemon> pokemonRepository, PokedexController pokedexController) {
        this.pokemonRepository = pokemonRepository;
        this.pokedexController = pokedexController;
    }

    /**
     * Create a new Pokemon of the specified number and add it to the repository.
     *
     * @param pokedexNumber the number of the Pokemon to catch
     * @return a new Pokemon with a unique ID
     */
    @Nonnull
    public Pokemon catchPokemon(int pokedexNumber) {
        String pokemonData = getPokemonData(pokedexNumber);
        String[] pokemonDataArray = pokemonData.split(",");

        int hp = Integer.parseInt(pokemonDataArray[4]);
        int attack = Integer.parseInt(pokemonDataArray[5]);
        int defense = Integer.parseInt(pokemonDataArray[6]);
        int specialAttack = Integer.parseInt(pokemonDataArray[7]);
        int specialDefense = Integer.parseInt(pokemonDataArray[8]);
        int speed = Integer.parseInt(pokemonDataArray[9]);
        int total = Integer.parseInt(pokemonDataArray[3]);

        PokemonBuilder builder = Pokemon.builder();
        builder.pokedexNumber(pokedexNumber)
                .hp(hp)
                .attack(attack)
                .defense(defense)
                .specialAttack(specialAttack)
                .specialDefense(specialDefense)
                .speed(speed)
                .total(total);

        Pokemon pokemon = Objects.requireNonNull(builder.build());
        pokemonRepository.add(pokemon);
        return pokemon;
    }

    private String getPokemonData(int pokedexNumber) {
        if (pokemonDataMap.containsKey(pokedexNumber)) {
            return pokemonDataMap.get(pokedexNumber);
        } else {
            String pokemonData = fetchPokemonData(pokedexNumber);
            pokemonDataMap.put(pokedexNumber, pokemonData);
            return pokemonData;
        }
    }

    private String fetchPokemonData(int pokedexNumber) {
        Path filePath = Paths.get(path);

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            // Skip to the desired line in the CSV file
            for (int i = 0; i < pokedexNumber; i++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IllegalArgumentException("Invalid Pokedex number: " + pokedexNumber);
                }
            }

            // Read data from the specific line
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Error reading Pokemon data", e);
        }
    }

    /**
     * Catch a random Pokemon.
     * @return a new randomly caught Pokemon
     */
    public Pokemon catchRandomPokemon() {
        return catchPokemon(random.nextInt(721));
    }

    public Pokemon getPokemonById(String pokemonId) {
        return pokemonRepository.get(new ObjectId(pokemonId));
    }

    public Pokemon getPokemonByObjectId(@Nonnull ObjectId pokemonObjectId) {
        return pokemonRepository.get(pokemonObjectId);
    }

    public Pokemon searchPokemonByPokedexNumber(int pokedexNumber) {
        return catchPokemon(pokedexNumber);
    }

    public void updatePokemon(Pokemon pokemon) {
        Pokemon existingPokemon = getPokemonById(pokemon.getId().toString());
        existingPokemon.setHp(pokemon.getHp());
        existingPokemon.setTotal(pokemon.getTotal());
        pokemonRepository.update(existingPokemon);
    }

    public void updatePokemonHP(Pokemon pokemon, int newHP) {
        pokemon.setHp(pokemon.getHp() + newHP);
        pokemon.setTotal(pokemon.getTotal() + newHP);
        pokemonRepository.update(pokemon);
    }
}
