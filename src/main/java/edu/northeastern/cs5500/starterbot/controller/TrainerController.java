package edu.northeastern.cs5500.starterbot.controller;

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

    PokedexController pokedexController;

    @Inject
    TrainerController(
            GenericRepository<Trainer> trainerRepository,
            PokemonController pokemonController,
            PokedexController pokedexController) {
        this.trainerRepository = trainerRepository;
        this.pokemonController = pokemonController;
        this.pokedexController = pokedexController;
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

    public Trainer addPokemonToTrainer(String discordMemberId, String pokemonIdString) {
        ObjectId pokemonId = new ObjectId(pokemonIdString);
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        trainer.getPokemonInventory().add(pokemonId);
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
        if (!trainer.getPokemonInventory().contains(pokemon.getId())) {
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

    /**
     * Updates the PokeBall quantity for a trainer identified by their Discord member ID by the
     * specified number of PokeBalls.
     *
     * @param discordMemberId The Discord member ID of the trainer.
     * @param numOfPokeBall The number of PokeBalls to be added to the trainer's current PokeBall
     *     quantity.
     */
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

    /**
     * Updates the coin balance for a trainer identified by their Discord member ID by the specified
     * number of coins.
     *
     * @param discordMemberId The Discord member ID of the trainer.
     * @param numOfCoin The number of coins to be added to the trainer's current coin balance.
     */
    public void updateCoinBalanceForTrainer(String discordMemberId, int numOfCoin) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        int newCoinQuantity = trainer.getCoinBalance() + numOfCoin;
        trainer.setCoinBalance(newCoinQuantity);
        trainerRepository.update(trainer);
    }

    /**
     * Retrieves a random Pokemon from the inventory of the trainer associated with the provided
     * Discord member ID.
     *
     * @param discordMemberId The Discord member ID of the trainer.
     * @return A randomly selected Pokemon from the trainer's inventory, or null if the inventory is
     *     empty.
     */
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

    public List<String> getPokemonNamesFromTrainerInventory(String discordMemberId) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);
        List<String> pokemonNames = new ArrayList<>();

        for (ObjectId pokemonId : trainer.getPokemonInventory()) {
            Pokemon pokemon = pokemonController.getPokemonById(pokemonId.toString());
            int pokedexNum = pokemon.getPokedexNumber();
            String pokemonName = pokedexController.getPokemonSpeciesByNumber(pokedexNum).getName();
            pokemonNames.add(pokemonName);
        }

        return pokemonNames;
    }

    public ObjectId getPokemonIdByPokemonName(String discordMemberId, String name) {
        Trainer trainer = getTrainerForMemberId(discordMemberId);

        for (ObjectId pokemonId : trainer.getPokemonInventory()) {
            Pokemon pokemon = pokemonController.getPokemonById(pokemonId.toString());
            int pokedexNum = pokemon.getPokedexNumber();
            String pokemonName = pokedexController.getPokemonSpeciesByNumber(pokedexNum).getName();

            if (name.equals(pokemonName)) {
                return pokemonId;
            }
        }
        return null;
    }
}
