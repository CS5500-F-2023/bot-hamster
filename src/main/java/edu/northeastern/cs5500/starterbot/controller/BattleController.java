package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Pokemon;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class BattleController {

    @Inject TrainerController trainerController;

    @Inject
    public BattleController(TrainerController trainerController) {
        this.trainerController = trainerController;
    }

    public Boolean battleWithSelf(String myDiscordId, String opponentDiscordId) {
        return myDiscordId.equals(opponentDiscordId);
    }

    public Boolean validateBattleTeam(String discordId) {
        Collection<String> pokemonTeam =
                trainerController.getPokemonNamesFromTrainerInventory(discordId);
        return !pokemonTeam.isEmpty();
    }

    public String compareTotal(String myDiscordId, String opponentDiscordId) {
        Pokemon myPokemon = trainerController.getRandomPokemonFromTrainer(myDiscordId);
        Pokemon opponentPokemon = trainerController.getRandomPokemonFromTrainer(opponentDiscordId);
        log.info("My Pokemon: {}", myPokemon);
        log.info("Opponent's Pokemon: {}", opponentPokemon);

        if (myPokemon != null && opponentPokemon != null) {
            if (myPokemon.getTotal() > opponentPokemon.getTotal()) {
                return myDiscordId;
            } else if (myPokemon.getTotal() < opponentPokemon.getTotal()) {
                return opponentDiscordId;
            } else {
                return "Tie";
            }
        } else {
            return "No Pokemon available";
        }
    }
}
