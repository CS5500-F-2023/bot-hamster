package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.PokemonSpecies;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies.PokemonSpeciesBuilder;
import edu.northeastern.cs5500.starterbot.model.PokemonType;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;

public class PokedexController {
    @Inject
    PokedexController() {
        // empty and defined for Dagger
    }

    @Nonnull
    public PokemonSpecies getPokemonSpeciesByNumber(int pokedexNumber) {
        PokemonSpeciesBuilder builder = PokemonSpecies.builder();
        builder.pokedexNumber(pokedexNumber);
        switch (pokedexNumber) {
            case 1:
                builder.name("Bulbasaur");
                builder.types(PokemonType.getSingleTypeArray(PokemonType.GRASS));
                builder.imageUrl("https://placehold.co/256x256/green/white.png?text=Bulbasaur");
                break;
            case 4:
                builder.name("Charmander");
                builder.types(PokemonType.getSingleTypeArray(PokemonType.FIRE));
                builder.imageUrl("https://placehold.co/256x256/red/white.png?text=Charmander");
                break;
            case 7:
                builder.name("Squirtle");
                builder.types(PokemonType.getSingleTypeArray(PokemonType.WATER));
                builder.imageUrl("https://placehold.co/256x256/blue/white.png?text=Squirtle");
                break;
            case 19:
                builder.name("Rattata");
                builder.types(PokemonType.getSingleTypeArray(PokemonType.NORMAL));
                builder.imageUrl("https://placehold.co/256x256.png?text=Rattata");
                break;
            default:
                throw new IllegalStateException();
        }
        return Objects.requireNonNull(builder.build());
    }
}
