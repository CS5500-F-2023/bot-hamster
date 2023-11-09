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
        // TODO: Modify the message
        return Commands.slash(getName(), "Catch a random Pokemon in wild");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /catch, event: /battle");
        Pokemon pokemon = pokemonController.catchRandomPokemon();
        PokemonSpecies species =
                pokedexController.getPokemonSpeciesByNumber(pokemon.getPokedexNumber());

        // build UI
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(String.format("A wild %s appears!", species.getName()));

        // TODO: Adjust UI design if necessary
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

        embedBuilder.setThumbnail(species.getImageUrl());

        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder =
                messageCreateBuilder.addActionRow(
                        Button.primary(getName() + ":catch:" + pokemon.getId().toString(), "Catch"),
                        Button.secondary(
                                getName() + ":battle:" + pokemon.getId().toString(), "Battle"));
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
            battleButton(event, trainerDiscordId, pokemonId, species, pokemonStatsTotal);
        }
    }

    private void catchButton(
            @Nonnull ButtonInteractionEvent event,
            String trainerDiscordId,
            String pokemonId,
            PokemonSpecies species) {
        int pokeBallCount = trainerController.getPokeBallCount(trainerDiscordId);
        if (pokeBallCount <= 0) {
            // <@%s> creates a reference to the discordId
            event.reply(
                            String.format(
                                    "Pokemon %s run away! Player <@%s> has no PokeBall.",
                                    species.getName(), trainerDiscordId))
                    .queue();
        } else {
            trainerController.addPokemonToTrainer(trainerDiscordId, pokemonId);
            trainerController.updatePokeBallToTrainer(trainerDiscordId, -1);
            event.reply(
                            String.format(
                                    "Congratulations! Player <@%s> caught Pokemon %s!",
                                    trainerDiscordId, species.getName()))
                    .queue();
        }
    }

    private void battleButton(
            @Nonnull ButtonInteractionEvent event,
            String trainerDiscordId,
            String pokemonId,
            PokemonSpecies species,
            int pokemonStatsTotal) {
        Pokemon randomPokemon = trainerController.getRandomPokemonFromTrainer(trainerDiscordId);
        if (randomPokemon != null) {
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
                                        "Pokemon run away! Player <@%s> lost in battle with Pokemon %s!",
                                        trainerDiscordId, species.getName()))
                        .queue();
            } else {
                event.reply(
                                String.format(
                                        "Tie! Battle Pokemon %s again!",
                                        trainerDiscordId, species.getName()))
                        .queue();
            }
        } else {
            event.reply(
                            String.format(
                                    "Player <@%s> has no Pokemon available to battle with Pokemon %s.",
                                    trainerDiscordId, species.getName()))
                    .queue();
        }
    }
}
