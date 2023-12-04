package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.Trainer;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import org.junit.jupiter.api.Test;

class TradeOfferControllerTest {
    final String DISCORD_USER_ID_1 = "discordUserId1";
    final String DISCORD_USER_ID_2 = "discordUserId2";

    PokemonController getPokemonController() {
        return new PokemonController(new InMemoryRepository<>());
    }

    TradeOfferController getTradeOfferController(TrainerController trainerController) {
        TradeOfferController tradeOfferController = new TradeOfferController(trainerController);
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

    TradeOfferController getTradeOfferController() {
        TradeOfferController tradeOfferController = new TradeOfferController(null);
        return tradeOfferController;
    }

    @Test
    void testThatTrainersCanTradePokemonTheyHave() {
        PokemonController pokemonController = getPokemonController();
        TrainerController trainerController = getTrainerController(pokemonController);
        TradeOfferController tradeOfferController = getTradeOfferController(trainerController);

        Trainer trainer = trainerController.getTrainerForMemberId(DISCORD_USER_ID_1);
        Trainer otherTrainer = trainerController.getTrainerForMemberId(DISCORD_USER_ID_2);

        String trainerPokemon = trainer.getPokemonInventory().get(0).toString();
        String otherTrainerPokemon = otherTrainer.getPokemonInventory().get(0).toString();

        tradeOfferController.acceptOffer(
                DISCORD_USER_ID_1, trainerPokemon, DISCORD_USER_ID_2, otherTrainerPokemon);
        assertThat(trainer.getPokemonInventory().get(0).toString()).isEqualTo(otherTrainerPokemon);
        assertThat(otherTrainer.getPokemonInventory().get(0).toString()).isEqualTo(trainerPokemon);
    }
}
