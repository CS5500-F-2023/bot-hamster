package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

class TrainerControllerTest {
    final String DISCORD_USER_ID_1 = "discordUserId1";

    PokemonController getPokemonController() {
        return new PokemonController(new InMemoryRepository<>());
    }

    PokedexController getPokedexController() {
        PokedexController pokedexController = new PokedexController();
        return pokedexController;
    }

    TrainerController getTrainerController(PokemonController pokemonController) {
        TrainerController trainerController =
                new TrainerController(
                        new InMemoryRepository<>(),
                        getPokemonController(),
                        new PokedexController());

        trainerController.addPokemonToTrainer(
                DISCORD_USER_ID_1, pokemonController.catchPokemon(1).getId().toString());

        return trainerController;
    }

    @Test
    void testAddPokemonToTrainer() {
        PokemonController pokemonController = getPokemonController();
        TrainerController trainerController = getTrainerController(pokemonController);

        String caughtPokemonId = pokemonController.catchPokemon(1).getId().toString();
        trainerController.addPokemonToTrainer(DISCORD_USER_ID_1, caughtPokemonId);
        Trainer trainer = trainerController.getTrainerForMemberId(DISCORD_USER_ID_1);
        List<ObjectId> pokemonIds = trainer.getPokemonInventory();

        assertThat(pokemonIds.contains(caughtPokemonId));
    }

    @Test
    void testThatTrainersCanUpdateTheirPokeBall() {
        PokemonController pokemonController = getPokemonController();
        TrainerController trainerController = getTrainerController(pokemonController);

        trainerController.updatePokeBallForTrainer(DISCORD_USER_ID_1, -1);
        assertThat(trainerController.getPokeBallQuantityFromTrainer(DISCORD_USER_ID_1))
                .isEqualTo(2);
    }

    @Test
    void testThatTrainersCanUpdateTheirCoinBalance() {
        PokemonController pokemonController = getPokemonController();
        TrainerController trainerController = getTrainerController(pokemonController);

        trainerController.updateCoinBalanceForTrainer(DISCORD_USER_ID_1, -25);
        assertThat(trainerController.getCoinBalanceFromTrainer(DISCORD_USER_ID_1)).isEqualTo(75);
    }

    @Test
    void testGetTrainerForId() {
        PokemonController pokemonController = getPokemonController();
        TrainerController trainerController = getTrainerController(pokemonController);
        Trainer expectedTrainer = trainerController.getTrainerForMemberId(DISCORD_USER_ID_1);
        ObjectId trainerId = expectedTrainer.getId();

        assertThat(trainerController.getTrainerForId(trainerId))
                .isEqualTo(trainerController.getTrainerForMemberId(DISCORD_USER_ID_1));
    }

    @Test
    void testRemovePokemonFromTrainerWithTrainerAndPokemon() {
        PokemonController pokemonController = getPokemonController();
        TrainerController trainerController = getTrainerController(pokemonController);

        Pokemon caughtPokemon = pokemonController.catchPokemon(1);
        String caughtPokemonId = caughtPokemon.getId().toString();
        trainerController.addPokemonToTrainer(DISCORD_USER_ID_1, caughtPokemonId);

        Trainer trainer = trainerController.getTrainerForMemberId(DISCORD_USER_ID_1);
        trainerController.removePokemonFromTrainer(trainer, caughtPokemon);
        Trainer updatedTrainer = trainerController.getTrainerForMemberId(DISCORD_USER_ID_1);

        assertThat(updatedTrainer.getPokemonInventory()).doesNotContain(caughtPokemonId);
    }

    @Test
    void testRemovePokemonFromTrainerWhenPokemonNotInInventory() {
        PokemonController pokemonController = getPokemonController();
        TrainerController trainerController = getTrainerController(pokemonController);

        Pokemon caughtPokemon = pokemonController.catchPokemon(1);
        Trainer trainer = trainerController.getTrainerForMemberId(DISCORD_USER_ID_1);

        assertThrows(
                IllegalStateException.class,
                () -> trainerController.removePokemonFromTrainer(trainer, caughtPokemon));
    }
}
