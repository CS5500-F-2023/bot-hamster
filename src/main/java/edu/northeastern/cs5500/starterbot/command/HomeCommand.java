package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.controller.TrainerController;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bson.types.ObjectId;

@Slf4j
public class HomeCommand implements SlashCommandHandler {

    static final String NAME = "home";

    @Inject PokemonController pokemonController;

    @Inject TrainerController trainerController;

    @Inject PokedexController pokedexController;

    @Inject
    public HomeCommand() {
        // Defined public and empty for Dagger injection
    }

    @Override
    @Nonnull
    public String getName() {
        return NAME;
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Home - Check your inventory");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /home");

        String trainerDiscordId = event.getMember().getId();
        Trainer trainer = trainerController.getTrainerForMemberId(trainerDiscordId);

        // build UI
        EmbedBuilder embedBuilder = new EmbedBuilder();

        // UI design
        embedBuilder.setDescription(
                String.format("**Hey <@%s>! Welcome to your inventory** 🏠", trainerDiscordId)
                        + "\n\n"
                        + "🎖️ **Team** "
                        + "\n"
                        + getPokemonListAsString(trainer)
                        + "\n\n"
                        + "**Balance** 💰"
                        + "\n"
                        + String.format(
                                "%s Coins",
                                trainerController.getCoinBalanceFromTrainer(trainerDiscordId))
                        + "\n\n"
                        + "**PokeBall** 🪩"
                        + "\n"
                        + String.format(
                                "%s Balls",
                                trainerController.getPokeBallQuantityFromTrainer(
                                        trainerDiscordId)));
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    /**
     * Generate a list of Pokemon names from the inventory of a Trainer as String.
     *
     * @param trainer The trainer whose inventory is to be processed.
     * @return A string representing the names of the Pokemon in the trainer's inventory.
     */
    private String getPokemonListAsString(Trainer trainer) {
        List<String> pokemonList = new ArrayList<>();
        for (ObjectId pokemonId : trainer.getPokemonInventory().values()) {
            Integer pokedexNum =
                    pokemonController.getPokemonByObjectId(pokemonId).getPokedexNumber();
            String pokemonName = pokedexController.getPokemonSpeciesByNumber(pokedexNum).getName();
            pokemonList.add(pokemonName);
        }
        if (pokemonList.size() == 0) {
            return "No Pokemon! Use /catch to get one 🤩";
        } else return String.join(", ", pokemonList);
    }
}
