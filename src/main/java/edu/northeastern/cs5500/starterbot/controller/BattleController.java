package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Battle;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class BattleController {

    @Inject PokemonController pokemonController;
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

    @Nonnull
    public Battle createBattle(String myDiscordId, String opponentDiscordId) {

        Pokemon myPokemon = trainerController.getRandomPokemonFromTrainer(myDiscordId);
        Pokemon opponentPokemon = trainerController.getRandomPokemonFromTrainer(opponentDiscordId);

        String myPokemonId = myPokemon.getId().toString();
        String opponentPokemonId = opponentPokemon.getId().toString();

        Battle battle = new Battle(myDiscordId, opponentDiscordId, myPokemonId, opponentPokemonId);

        return battle;
    }

    public String compareTotal(Battle currentBattle) {
        String myDiscordId = currentBattle.getMyDiscordId();
        String opponentDiscordId = currentBattle.getOpponentDiscordId();
        String myPokemonId = currentBattle.getMyPokemonId();
        String opponentPokemonId = currentBattle.getOpponentPokemonId();

        try {
            Pokemon myPokemon = pokemonController.getPokemonById(myPokemonId);
            Pokemon opponentPokemon = pokemonController.getPokemonById(opponentPokemonId);

            if (myPokemon != null && opponentPokemon != null) {
                if (myPokemon.getTotal() > opponentPokemon.getTotal()) {
                    pokemonController.updatePokemonMood(myPokemon, 2);
                    pokemonController.updatePokemonHPByHalf(myPokemon, -1);
                    pokemonController.updatePokemonHP(opponentPokemon, -opponentPokemon.getHp());
                    return myDiscordId;
                } else if (myPokemon.getTotal() < opponentPokemon.getTotal()) {
                    pokemonController.updatePokemonMood(opponentPokemon, 2);
                    pokemonController.updatePokemonHPByHalf(opponentPokemon, -1);
                    pokemonController.updatePokemonHP(myPokemon, -myPokemon.getHp());
                    return opponentDiscordId;
                } else {
                    pokemonController.updatePokemonMood(myPokemon, 1);
                    pokemonController.updatePokemonMood(opponentPokemon, 1);
                    return "Tie";
                }
            } else {
                return "No Pokemon available";
            }
        } catch (Exception e) {
            log.error("Error retrieving Pokemon: {}", e.getMessage());
            return "Can't retrieve Pokemon";
        }
    }
}
