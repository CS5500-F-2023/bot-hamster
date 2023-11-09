package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.InventoryItem;
import edu.northeastern.cs5500.starterbot.model.InventoryItemType;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.ArrayList;
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
        InventoryItem initialPokeBall =
                new InventoryItem(new ObjectId(), InventoryItemType.POKEBALL, 3);
        InventoryItem initialCoinBalance =
                new InventoryItem(new ObjectId(), InventoryItemType.COIN, 100);
        trainer.getPokemonInventory().add(initialPokeBall);
        trainer.getPokemonInventory().add(initialCoinBalance);

        return trainerRepository.add(trainer);
    }

    public void addPokemonToTrainer(String discordMemberId, String pokemonIdString) {
        ObjectId pokemonId = new ObjectId(pokemonIdString);
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        InventoryItem pokemon = new InventoryItem(pokemonId, InventoryItemType.POKEMON, 1);
        trainer.getPokemonInventory().add(pokemon);
        trainerRepository.update(trainer);
    }

    public void updatePokeBallToTrainer(String discordMemberId, int numOfPokeBall) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);

        for (InventoryItem item : trainer.getPokemonInventory()) {
            if (item.getInventoryItemType() == InventoryItemType.POKEBALL) {
                int newQuantity = item.getQuantity() + numOfPokeBall;
                item.setQuantity(newQuantity);
            }
        }
        trainerRepository.update(trainer);
    }

    public int getPokeBallCount(String discordMemberId) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);

        for (InventoryItem item : trainer.getPokemonInventory()) {
            if (item.getInventoryItemType() == InventoryItemType.POKEBALL) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    public void updateCoinBalance(String discordMemberId, int numOfCoin) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);

        for (InventoryItem item : trainer.getPokemonInventory()) {
            if (item.getInventoryItemType() == InventoryItemType.COIN) {
                int newQuantity = item.getQuantity() + numOfCoin;
                item.setQuantity(newQuantity);
            }
        }
        trainerRepository.update(trainer);
    }

    public int getCoinBalance(String discordMemberId) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);

        for (InventoryItem item : trainer.getPokemonInventory()) {
            if (item.getInventoryItemType() == InventoryItemType.COIN) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    public Pokemon getRandomPokemonFromTrainer(String discordMemberId) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        List<InventoryItem> pokemonList = new ArrayList<>();
        for (InventoryItem item : trainer.getPokemonInventory()) {
            if (item.getInventoryItemType() == InventoryItemType.POKEMON) {
                pokemonList.add(item);
            }
        }

        if (pokemonList.isEmpty()) {
            return null;
        } else {
            int randomIndex = random.nextInt(pokemonList.size());
            String pokemonObjectId = pokemonList.get(randomIndex).getObjectId().toString();
            return pokemonController.getPokemonById(pokemonObjectId);
        }
    }
}
