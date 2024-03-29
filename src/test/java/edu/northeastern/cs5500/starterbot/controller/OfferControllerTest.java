package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.Offer;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import org.junit.jupiter.api.Test;

class OfferControllerTest {
    final String DISCORD_USER_ID_1 = "discordUserId1";
    final String DISCORD_USER_ID_2 = "discordUserId2";

    PokemonController getPokemonController() {
        return new PokemonController(new InMemoryRepository<>());
    }

    OfferController getOfferController(TrainerController trainerController) {
        OfferController tradeOfferController = new OfferController(trainerController);
        return tradeOfferController;
    }

    TrainerController getTrainerController(PokemonController pokemonController) {
        TrainerController trainerController =
                new TrainerController(
                        new InMemoryRepository<>(),
                        getPokemonController(),
                        new PokedexController());

        trainerController.addPokemonToTrainer(
                DISCORD_USER_ID_1, pokemonController.catchPokemon(1).getId().toString());
        trainerController.addPokemonToTrainer(
                DISCORD_USER_ID_2, pokemonController.catchPokemon(4).getId().toString());

        return trainerController;
    }

    OfferController getOfferController() {
        OfferController tradeOfferController = new OfferController(null);
        return tradeOfferController;
    }

    @Test
    void testThatTrainersCanTradePokemonTheyHave() {
        // setup
        PokemonController pokemonController = getPokemonController();
        TrainerController trainerController = getTrainerController(pokemonController);
        OfferController tradeOfferController = getOfferController(trainerController);

        // precondition
        Trainer trainer = trainerController.getTrainerForMemberId(DISCORD_USER_ID_1);
        Trainer otherTrainer = trainerController.getTrainerForMemberId(DISCORD_USER_ID_2);

        String trainerPokemon = trainer.getPokemonInventory().get(0).toString();
        String otherTrainerPokemon = otherTrainer.getPokemonInventory().get(0).toString();

        // mutation
        Offer tradeOffer =
                new Offer(
                        DISCORD_USER_ID_1, DISCORD_USER_ID_2, trainerPokemon, otherTrainerPokemon);
        tradeOfferController.acceptTradeOffer(tradeOffer);

        // postcondition
        assertThat(trainer.getPokemonInventory().get(0).toString())
                .isEqualTo(tradeOffer.getOtherPokemonId());
        assertThat(otherTrainer.getPokemonInventory().get(0).toString())
                .isEqualTo(tradeOffer.getPokemonId());
    }
}
