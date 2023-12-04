package edu.northeastern.cs5500.starterbot.controller;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class OfferController {
    TrainerController trainerController;

    PokedexController pokedexController;

    @Inject
    public OfferController(TrainerController trainerController) {
        this.trainerController = trainerController;
    }

    public void acceptTradeOffer(
            String trainer, String pokemon, String otherTrainer, String otherPokemon) {
        trainerController.removePokemonFromTrainer(trainer, pokemon);
        trainerController.removePokemonFromTrainer(otherTrainer, otherPokemon);

        trainerController.addPokemonToTrainer(trainer, otherPokemon);
        trainerController.addPokemonToTrainer(otherTrainer, pokemon);
    }
}
