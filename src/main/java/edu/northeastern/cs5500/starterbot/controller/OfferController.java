package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Offer;
import javax.annotation.Nonnull;
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

    public void acceptTradeOffer(@Nonnull Offer tradeOffer) {
        trainerController.removePokemonFromTrainer(
                tradeOffer.getTrainerId(), tradeOffer.getPokemonId());
        trainerController.removePokemonFromTrainer(
                tradeOffer.getOtherTrainerId(), tradeOffer.getOtherPokemonId());

        trainerController.addPokemonToTrainer(
                tradeOffer.getTrainerId(), tradeOffer.getOtherPokemonId());
        trainerController.addPokemonToTrainer(
                tradeOffer.getOtherTrainerId(), tradeOffer.getPokemonId());
    }
}
