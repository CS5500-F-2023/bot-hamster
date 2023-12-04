package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.TradeOffer;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TradeOfferController {
    GenericRepository<TradeOffer> tradeOfferRepository;
    TrainerController trainerController;

    @Inject
    public TradeOfferController(
            GenericRepository<TradeOffer> tradeOfferRepository,
            TrainerController trainerController) {
        this.tradeOfferRepository = tradeOfferRepository;
        this.trainerController = trainerController;
    }

    public TradeOffer createNewOffering(
            Trainer trainer, Pokemon pokemon, Trainer otherTrainer, Pokemon otherPokemon) {
        trainerController.removePokemonFromTrainer(trainer, pokemon);
        trainerController.removePokemonFromTrainer(otherTrainer, otherPokemon);
        TradeOffer tradeOffer =
                new TradeOffer(
                        trainer.getId(),
                        pokemon.getId(),
                        otherTrainer.getId(),
                        otherPokemon.getId());
        return tradeOfferRepository.add(tradeOffer);
    }

    public void acceptOffer(TradeOffer tradeOffer) {
        Trainer trainer = trainerController.getTrainerForId(tradeOffer.getTrainerId());
        Trainer otherTrainer = trainerController.getTrainerForId(tradeOffer.getOtherTrainerId());

        trainerController.addPokemonToTrainer(
                trainer.getDiscordUserId(), tradeOffer.getOtherPokemonId().toString());
        trainerController.addPokemonToTrainer(
                otherTrainer.getDiscordUserId(), tradeOffer.getPokemonId().toString());

        tradeOfferRepository.delete(tradeOffer.getId());
    }
}
