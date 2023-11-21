package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies;
import edu.northeastern.cs5500.starterbot.model.Pokemon.PokemonBuilder;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;

import java.util.Objects;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bson.types.ObjectId;

@Singleton
public class PokemonController {

    GenericRepository<Pokemon> pokemonRepository;

    private Random random = new Random();

    @Inject
    PokemonController(GenericRepository<Pokemon> pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    /**
     * Create a new Pokemon of the specifed number and added it to the repository
     *
     * @param pokedexNumber the number of the Pokemon to catch
     * @return a new Pokemon with a unique ID
     */
    @Nonnull
    public Pokemon catchPokemon(int pokedexNumber) {
        PokemonBuilder builder = Pokemon.builder();
        builder.pokedexNumber(pokedexNumber);
        switch (pokedexNumber) {
            case 1:
                builder.hp(19);
                builder.attack(9);
                builder.defense(9);
                builder.specialAttack(11);
                builder.specialDefense(11);
                builder.speed(9);
                builder.total(68);
                break;
            case 2:
                // case 4:
                // temporary comment out to implement random
                builder.hp(18);
                builder.attack(18);
                builder.defense(9);
                builder.specialAttack(11);
                builder.specialDefense(10);
                builder.speed(11);
                builder.total(77);
                break;
            case 3:
                // case 7:
                // temporary comment out to implement random
                builder.hp(19);
                builder.attack(9);
                builder.defense(9);
                builder.specialAttack(11);
                builder.specialDefense(11);
                builder.speed(9);
                builder.total(77);
                break;
            case 4:
                // case 19:
                // temporary comment out to implement random
                builder.hp(18);
                builder.attack(10);
                builder.defense(8);
                builder.specialAttack(7);
                builder.specialDefense(8);
                builder.speed(12);
                builder.total(63);
                break;
            default:
                throw new IllegalStateException();
        }
        Pokemon pokemon = Objects.requireNonNull(builder.build());
        pokemonRepository.add(pokemon);
        return pokemon;
    }

    public Pokemon catchRandomPokemon() {
        // TODO: Modify the random index based on the catchPokemon method or the Pokemon data
        // resource file.
        return catchPokemon(random.nextInt(4) + 1);
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

}
