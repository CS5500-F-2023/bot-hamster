package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.TradeOffer;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.io.IOException;
import java.io.InputStream;
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

    public TradeOffer createNewOffering(Trainer trainer, Pokemon pokemon) {
        trainerController.removePokemonFromTrainer(trainer, pokemon);
        TradeOffer tradeOffer = new TradeOffer(trainer.getId(), pokemon.getId());
        return tradeOfferRepository.add(tradeOffer);
    }

    public TradeOffer respondToOffering(TradeOffer tradeOffer, Trainer trainer, Pokemon pokemon) {
        trainerController.removePokemonFromTrainer(trainer, pokemon);
        TradeOffer responseOffering = new TradeOffer(trainer.getId(), pokemon.getId());
        responseOffering.setParent(tradeOffer.getId());
        return tradeOfferRepository.add(tradeOffer);
    }

    public void acceptOffer(TradeOffer tradeOffer) {
        TradeOffer parentOffer = tradeOfferRepository.get(tradeOffer.getParent());

        Trainer parentTrainer = trainerController.getTrainerForId(parentOffer.getTrainerId());
        Trainer otherTrainer = trainerController.getTrainerForId(tradeOffer.getTrainerId());

        trainerController.addPokemonToTrainer(
                parentTrainer.getDiscordUserId(), tradeOffer.getPokemonId().toString());
        trainerController.addPokemonToTrainer(
                otherTrainer.getDiscordUserId(), parentOffer.getPokemonId().toString());

        tradeOfferRepository.delete(tradeOffer.getId());
        tradeOfferRepository.delete(parentOffer.getId());
    }

    public int getResources() {
        InputStream stream = this.getClass().getResourceAsStream("/pokemon.json");
        try {
            return stream.readAllBytes().length;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
