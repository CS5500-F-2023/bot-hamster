package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.model.InventoryItem;
import edu.northeastern.cs5500.starterbot.model.InventoryItemType;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import edu.northeastern.cs5500.starterbot.repository.GenericRepository;
import java.util.Collection;
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
        // Initialized the trainer's inventory with 3 Pokeball
        InventoryItem pokeball = new InventoryItem(new ObjectId(), InventoryItemType.POKEBALL, 3);
        trainer.getPokemonInventory().add(pokeball);

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

    public Pokemon getRandomPokemonFromTrainer(String discordMemberId) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        if (trainer.getPokemonInventory().isEmpty()) {
            return null;
        }

        int randomInventoryItemIndex = random.nextInt(trainer.getPokemonInventory().size());
        InventoryItem randomInventoryItem =
                trainer.getPokemonInventory().get(randomInventoryItemIndex);

        if (randomInventoryItem.getInventoryItemType() == InventoryItemType.POKEMON) {
            String pokemonObjectId = randomInventoryItem.getObjectId().toString();

            // NOTE: Not sure if it is a good design to include PokemonController in this
            // TrainerController
            return pokemonController.getPokemonById(pokemonObjectId);
        } else {
            return null;
        }
    }
}
