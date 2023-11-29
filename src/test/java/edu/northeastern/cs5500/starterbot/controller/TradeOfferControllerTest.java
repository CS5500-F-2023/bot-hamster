package edu.northeastern.cs5500.starterbot.controller;

import static org.junit.Assert.fail;

import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import org.junit.jupiter.api.Test;

class TradeOfferControllerTest {
    final String DISCORD_USER_ID_1 = "discordUserId1";
    final String DISCORD_USER_ID_2 = "discordUserId2";

    PokemonController getPokemonController() {
        return new PokemonController(new InMemoryRepository<>());
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

    TradeOfferController getTradeOfferController(TrainerController trainerController) {
        TradeOfferController tradeOfferController =
                new TradeOfferController(new InMemoryRepository<>(), trainerController);

        return tradeOfferController;
    }

    @Test
    void testThatTrainersCanOfferPokemonTheyHave() {
        PokemonController pokemonController = getPokemonController();
        TrainerController trainerController = getTrainerController(pokemonController);
        TradeOfferController tradeOfferController = getTradeOfferController(trainerController);

        Trainer trainer = trainerController.getTrainerForMemberId(DISCORD_USER_ID_1);
        Pokemon pokemon =
                pokemonController.getPokemonByObjectId(trainer.getPokemonInventory().get(0));
        tradeOfferController.createNewOffering(trainer, pokemon);
    }

    @Test
    void testThatTrainersCanOnlyOfferPokemonTheyHave() {
        PokemonController pokemonController = getPokemonController();
        TrainerController trainerController = getTrainerController(pokemonController);
        TradeOfferController tradeOfferController = getTradeOfferController(trainerController);

        Trainer trainer = trainerController.getTrainerForMemberId(DISCORD_USER_ID_1);
        Trainer otherTrainer = trainerController.getTrainerForMemberId(DISCORD_USER_ID_2);
        Pokemon pokemon =
                pokemonController.getPokemonById(
                        otherTrainer.getPokemonInventory().get(0).toString());
        try {
            tradeOfferController.createNewOffering(trainer, pokemon);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }
}
