package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.controller.TrainerController;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

@Slf4j
public class CatchCommand implements SlashCommandHandler, ButtonHandler {

    static final String NAME = "catch";

    @Inject PokedexController pokedexController;

    @Inject PokemonController pokemonController;

    @Inject TrainerController trainerController;

    @Inject
    public CatchCommand() {
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
        return Commands.slash(getName(), "Catch - Catch a wild Pokemon");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /catch, event: /fight");
        Pokemon pokemon = pokemonController.catchRandomPokemon();
        PokemonSpecies species =
                pokedexController.getPokemonSpeciesByNumber(pokemon.getPokedexNumber());

        // build UI
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(String.format("A wild %s appears!", species.getName()));
        embedBuilder.setDescription("Catch Pokemon with 1 Pokeball or Fight until You Win!");
        embedBuilder.setThumbnail(species.getImageUrl());

        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder =
                messageCreateBuilder.addActionRow(
                        Button.primary(getName() + ":catch:" + pokemon.getId().toString(), "Catch"),
                        Button.secondary(
                                getName() + ":fight:" + pokemon.getId().toString(), "Fight"));
        messageCreateBuilder = messageCreateBuilder.addEmbeds(embedBuilder.build());

        event.reply(messageCreateBuilder.build()).queue();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        // must mean the user clicked Catch
        String trainerDiscordId = event.getMember().getId();
        String pokemonId = event.getButton().getId().split(":")[2];
        Pokemon pokemon = pokemonController.getPokemonById(pokemonId);
        PokemonSpecies species =
                pokedexController.getPokemonSpeciesByNumber(pokemon.getPokedexNumber());
        int pokemonStatsTotal = pokemon.getTotal();

        if (event.getButton().getId().startsWith(getName() + ":catch:")) {
            catchButton(event, trainerDiscordId, pokemonId, species);
        } else {
            fightButton(event, trainerDiscordId, pokemonId, species, pokemonStatsTotal);
        }
    }

    /**
     * Handles the button interaction when the "Catch" button is clicked by a user.
     *
     * @param event The ButtonInteractionEvent triggered by the user clicking the "Catch" button.
     * @param trainerDiscordId The Discord ID of the trainer initiating the interaction.
     * @param pokemonId The ID of the wild Pokémon.
     * @param species The PokemonSpecies object representing the species of the wild Pokemon.
     */
    private void catchButton(
            @Nonnull ButtonInteractionEvent event,
            String trainerDiscordId,
            String pokemonId,
            PokemonSpecies species) {
        int pokeBallCount = trainerController.getPokeBallQuantityFromTrainer(trainerDiscordId);
        if (pokeBallCount <= 0) {
            // <@%s> creates a reference to the discordId
            event.reply(
                            String.format(
                                    "Pokemon %s run away! Player <@%s> has no PokeBall.",
                                    species.getName(), trainerDiscordId))
                    .queue();
        } else if (trainerController.getPokemonIdByPokemonName(trainerDiscordId, species.getName())
                != null) {
            event.reply(
                            String.format(
                                    "Pokemon %s already in the inventory! Try again!",
                                    species.getName(), trainerDiscordId))
                    .queue();
        } else {
            trainerController.addPokemonToTrainer(trainerDiscordId, pokemonId);
            trainerController.updatePokeBallForTrainer(trainerDiscordId, -1);
            event.reply(
                            String.format(
                                    "Congratulations! Player <@%s> caught Pokemon %s!",
                                    trainerDiscordId, species.getName()))
                    .queue();
        }
    }

    /**
     * Handles the button interaction when the "Fight" button is clicked by a user.
     *
     * @param event The ButtonInteractionEvent triggered by the user clicking the "Fight" button.
     * @param trainerDiscordId The Discord ID of the trainer initiating the interaction.
     * @param pokemonId The ID of the wild Pokemon.
     * @param species The PokémonSpecies object representing the species of the wild Pokemon.
     * @param pokemonStatsTotal The total stats of the wild Pokemon.
     */
    private void fightButton(
            @Nonnull ButtonInteractionEvent event,
            String trainerDiscordId,
            String pokemonId,
            PokemonSpecies species,
            int pokemonStatsTotal) {
        Pokemon randomPokemon = trainerController.getRandomPokemonFromTrainer(trainerDiscordId);
        if (trainerController.getPokemonIdByPokemonName(trainerDiscordId, species.getName())
                != null) {
            event.reply(
                            String.format(
                                    "Pokemon %s already in the inventory! Try again!",
                                    species.getName(), trainerDiscordId))
                    .queue();
        } else if (randomPokemon != null) {
            if (randomPokemon.getTotal() > pokemonStatsTotal) {
                trainerController.addPokemonToTrainer(trainerDiscordId, pokemonId);
                event.reply(
                                String.format(
                                        "Congratulations! Pokemon %s surrendered to player <@%s>!",
                                        species.getName(), trainerDiscordId))
                        .queue();
            } else if (randomPokemon.getTotal() < pokemonStatsTotal) {
                event.reply(
                                String.format(
                                        "Pokemon run away! Player <@%s> lost in a fight with Pokemon %s!",
                                        trainerDiscordId, species.getName()))
                        .queue();
            } else {
                event.reply(String.format("Tie! Fight Pokemon %s again!", species.getName()))
                        .queue();
            }
        } else {
            event.reply(
                            String.format(
                                    "Player <@%s> has no Pokemon available to fight with Pokemon %s.",
                                    trainerDiscordId, species.getName()))
                    .queue();
        }
    }
}
