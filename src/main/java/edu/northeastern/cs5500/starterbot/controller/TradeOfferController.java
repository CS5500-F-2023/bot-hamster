package edu.northeastern.cs5500.starterbot.controller;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TradeOfferController {
    TrainerController trainerController;

    PokedexController pokedexController;

    @Inject
    public TradeOfferController(TrainerController trainerController) {
        this.trainerController = trainerController;
    }

    public void acceptOffer(
            String trainer, String pokemon, String otherTrainer, String otherPokemon) {
        trainerController.removePokemonFromTrainer(trainer, pokemon);
        trainerController.removePokemonFromTrainer(otherTrainer, otherPokemon);

        trainerController.addPokemonToTrainer(trainer, otherPokemon);
        trainerController.addPokemonToTrainer(otherTrainer, pokemon);
    }
}
