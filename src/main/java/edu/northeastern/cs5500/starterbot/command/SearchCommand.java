package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Slf4j
public class SearchCommand implements SlashCommandHandler {

    static final String NAME = "search";

    @Inject PokedexController pokedexController;
    @Inject PokemonController pokemonController;

    @Inject
    public SearchCommand() {
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
        return Commands.slash(getName(), "Pokedex - Look up a Pokemon")
                .addOption(
                        OptionType.INTEGER,
                        "pokedex_number",
                        "Enter the Pokedex number of the Pokemon",
                        true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /search");

        var pokedexNumberOption = event.getOption("pokedex_number");
        if (pokedexNumberOption == null) {
            log.error("Received null value for mandatory parameter 'pokedex_number'");
            event.reply("Pokedex number is required for this command.").queue();
            return;
        }

        int pokedexNumber;
        try {
            pokedexNumber = (int) pokedexNumberOption.getAsLong();
        } catch (NumberFormatException e) {
            log.error("Invalid input for 'pokedex_number'");
            event.reply("Please provide a valid number for the Pokedex.").queue();
            return;
        }

        Pokemon pokemon = pokemonController.searchPokemonByPokedexNumber(pokedexNumber);

        if (pokemon == null) {
            event.reply("No Pokémon found with the provided Pokedex number.").queue();
            return;
        }

        PokemonSpecies species =
                pokedexController.getPokemonSpeciesByNumber(pokemon.getPokedexNumber());

        EmbedBuilder embedBuilder = buildPokemonEmbed(pokemon, species);

        event.replyEmbeds(embedBuilder.build()).queue();
    }

    private EmbedBuilder buildPokemonEmbed(Pokemon pokemon, PokemonSpecies species) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(String.format("%s", species.getName()));
        embedBuilder.setThumbnail(species.getImageUrl());
        embedBuilder.setDescription(
                "**Level**: "
                        + Integer.toString(pokemon.getLevel())
                        + "\n"
                        + "**HP: **"
                        + Integer.toString(pokemon.getHp())
                        + "\n"
                        + "**Attack: **"
                        + Integer.toString(pokemon.getAttack())
                        + "\n"
                        + "**Defense: **"
                        + Integer.toString(pokemon.getDefense())
                        + "\n"
                        + "**SpecialAttack: **"
                        + Integer.toString(pokemon.getSpecialAttack())
                        + "\n"
                        + "**SpecialDefense: **"
                        + Integer.toString(pokemon.getSpecialDefense())
                        + "\n"
                        + "**Speed: **"
                        + Integer.toString(pokemon.getSpeed())
                        + "\n"
                        + "**Total: **"
                        + Integer.toString(pokemon.getTotal()));
        return embedBuilder;
    }
}
