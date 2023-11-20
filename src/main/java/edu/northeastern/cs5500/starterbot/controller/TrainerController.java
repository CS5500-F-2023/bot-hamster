package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.bson.types.ObjectId;

@Singleton
public class TrainerController {
    GenericRepository<Trainer> trainerRepository;

    private Random random = new Random();
    PokemonController pokemonController;

    @Inject
    TrainerController(
            GenericRepository<Trainer> trainerRepository, PokemonController pokemonController) {
        this.trainerRepository = trainerRepository;
        this.pokemonController = pokemonController;
    }

    @Nonnull
    public Trainer getTrainerForMemberId(String discordMemberId) {
        Collection<Trainer> trainers = trainerRepository.getAll();
        for (Trainer currentTrainer : trainers) {
            if (currentTrainer.getDiscordUserId().equals(discordMemberId)) {
                return currentTrainer;
            }
        }

        Trainer trainer = new Trainer();
        trainer.setDiscordUserId(discordMemberId);
        trainer.setPokeBallQuantity(3);
        trainer.setCoinBalance(100);

        return trainerRepository.add(trainer);
    }

    public Trainer addPokemonToTrainer(
            String discordMemberId, String pokemonName, String pokemonIdString) {
        ObjectId pokemonId = new ObjectId(pokemonIdString);
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        trainer.getPokemonInventory().put(pokemonName, pokemonId);
        return trainerRepository.update(trainer);
    }

    public void removePokemonFromTrainer(
            @Nonnull String discordMemberId, @Nonnull String pokemonIdString) {
        ObjectId pokemonId = new ObjectId(pokemonIdString);
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        trainer.getPokemonInventory().remove(pokemonId);
        trainerRepository.update(trainer);
    }

    public void removePokemonFromTrainer(@Nonnull Trainer trainer, @Nonnull Pokemon pokemon) {
        if (!trainer.getPokemonInventory().containsValue(pokemon.getId())) {
            throw new IllegalStateException(
                    "Cannot remove a Pokemon that is not in the user's inventory");
        }
        trainer.getPokemonInventory().remove(pokemon.getId());
        trainerRepository.update(trainer);
    }

    public Trainer getTrainerForId(ObjectId trainerId) {
        return trainerRepository.get(trainerId);
    }

    public int getPokeBallQuantityFromTrainer(String discordMemberId) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        return trainer.getPokeBallQuantity();
    }

    public void updatePokeBallForTrainer(String discordMemberId, int numOfPokeBall) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        int newPokeBallQuantity = trainer.getPokeBallQuantity() + numOfPokeBall;
        trainer.setPokeBallQuantity(newPokeBallQuantity);
        trainerRepository.update(trainer);
    }

    public int getCoinBalanceFromTrainer(String discordMemberId) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        return trainer.getCoinBalance();
    }

    public void updateCoinBalanceForTrainer(String discordMemberId, int numOfCoin) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        int newCoinQuantity = trainer.getCoinBalance() + numOfCoin;
        trainer.setCoinBalance(newCoinQuantity);
        trainerRepository.update(trainer);
    }

    public Pokemon getRandomPokemonFromTrainer(String discordMemberId) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        Map<String, ObjectId> pokemonInventory = trainer.getPokemonInventory();

        if (!pokemonInventory.isEmpty()) {
            ObjectId randomPokemonId =
                    new ArrayList<>(pokemonInventory.values())
                            .get(random.nextInt(pokemonInventory.size()));
            return pokemonController.getPokemonById(randomPokemonId.toString());
        }
        return null;
    }

    public Set<String> getPokemonNameFromTrainerInventory(String discordMemberId) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        return trainer.getPokemonInventory().keySet();
    }

    public void updatePokemonStatsForTrainer(
            String discordMemberId, String pokemonName, int plusOrMinus) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        Map<String, ObjectId> pokemonInventory = trainer.getPokemonInventory();

        for (String name : pokemonInventory.keySet()) {
            if (name.equals(pokemonName)) {
                ObjectId pokemonId = pokemonInventory.get(name);
                Pokemon pokemon = pokemonController.getPokemonById(pokemonId.toString());

                int halfPokemonHP = (int) (pokemon.getHp() / 2.0) * plusOrMinus;
                int newPokemonHP = pokemon.getHp() + halfPokemonHP;
                int newPokemonTotal = pokemon.getTotal() + halfPokemonHP;

                pokemon.setHp(newPokemonHP);
                pokemon.setTotal(newPokemonTotal);
                pokemonController.updatePokemon(pokemon);
                trainerRepository.update(trainer);
            }
        }
    }
}
