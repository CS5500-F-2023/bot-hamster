package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.controller.TrainerController;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bson.types.ObjectId;

@Slf4j
public class HomeCommand implements SlashCommandHandler, ButtonHandler {

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

        // Build UI
        EmbedBuilder embedBuilder = new EmbedBuilder();
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

        // Build Button
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder.addActionRow(Button.primary(getName() + ":stats", "🔍 Show Stats"));
        messageCreateBuilder = messageCreateBuilder.addEmbeds(embedBuilder.build());
        event.reply(messageCreateBuilder.build()).queue();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        String trainerDiscordId = event.getMember().getId();
        Trainer trainer = trainerController.getTrainerForMemberId(trainerDiscordId);
        if (event.getButton().getId().startsWith(getName() + ":stats")) {
            statsButton(event, trainer);
        }
    }

    private void statsButton(@Nonnull ButtonInteractionEvent event, Trainer trainer) {
        // Acknowledge the interaction and prepare to send a response
        event.deferReply().queue();

        List<MessageEmbed> embeds = new ArrayList<>();
        for (ObjectId objectId : trainer.getPokemonInventory()) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            Pokemon pokemon = pokemonController.getPokemonByObjectId(objectId);
            PokemonSpecies species =
                    pokedexController.getPokemonSpeciesByNumber(pokemon.getPokedexNumber());
            getPokemonStats(pokemon, species, embedBuilder);
            embeds.add(embedBuilder.build());
        }

        // Check if there are embeds to send
        if (!embeds.isEmpty()) {
            event.getHook().sendMessageEmbeds(embeds).queue();
        } else {
            event.getHook().sendMessage("No Pokemon in your team!").queue();
        }
    }

    private void getPokemonStats(
            Pokemon pokemon, PokemonSpecies species, EmbedBuilder embedBuilder) {
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
    }

    /**
     * Generate a list of Pokemon names from the inventory of a Trainer as String.
     *
     * @param trainer The trainer whose inventory is to be processed.
     * @return A string representing the names of the Pokemon in the trainer's inventory.
     */
    private String getPokemonListAsString(Trainer trainer) {
        List<String> pokemonList = new ArrayList<>();
        for (ObjectId pokemonId : trainer.getPokemonInventory()) {
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
