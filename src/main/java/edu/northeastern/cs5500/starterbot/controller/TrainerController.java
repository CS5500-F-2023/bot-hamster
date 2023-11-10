package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.Collection;
import java.util.List;
import java.util.Random;
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
    TrainerController(GenericRepository<Trainer> trainerRepository) {
        this.trainerRepository = trainerRepository;
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

        // TODO: Modified the trainer's initial inventory items as needed.
        trainer.setPokeBallQuantity(3);
        trainer.setCoinBalance(100);

        return trainerRepository.add(trainer);
    }

    public void addPokemonToTrainer(String discordMemberId, String pokemonIdString) {
        ObjectId pokemonId = new ObjectId(pokemonIdString);
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        trainer.getPokemonInventory().add(pokemonId);
        trainerRepository.update(trainer);
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
        List<ObjectId> pokemonList = trainer.getPokemonInventory();

        if (pokemonList.isEmpty()) {
            return null;
        } else {
            int randomIndex = random.nextInt(pokemonList.size());
            String pokemonObjectId = pokemonList.get(randomIndex).toString();
            return pokemonController.getPokemonById(pokemonObjectId);
        }
    }
}
