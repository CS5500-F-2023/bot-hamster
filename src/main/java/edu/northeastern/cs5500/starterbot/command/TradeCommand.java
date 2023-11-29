package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.controller.TrainerController;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

@Slf4j
public class TradeCommand implements SlashCommandHandler, StringSelectHandler {

    static final String NAME = "trade";

    @Inject PokedexController pokedexController;

    @Inject PokemonController pokemonController;

    @Inject TrainerController trainerController;

    @Inject
    public TradeCommand() {
        // Empty and public for Dagger
    }

    @Override
    @Nonnull
    public String getName() {
        return NAME;
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Trade - Trade a Pokemon with other player")
                .addOption(
                        OptionType.USER, "player", "Enter the Player to trade Pokemon with", true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /trade");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Trade Pokemon with Friend pika pika!");
        embedBuilder.setDescription("Choose a Pokemon from your inventory to trade");
        embedBuilder.setImage("http://media3.giphy.com/media/Zqy8kRlwfwFnq/giphy.gif");

        String trainerDiscordId = event.getMember().getId();
        Collection<String> pokemonList =
                trainerController.getPokemonNamesFromTrainerInventory(trainerDiscordId);

        if (!pokemonList.isEmpty()) {
            StringSelectMenu.Builder menu =
                    StringSelectMenu.create(NAME).setPlaceholder("Choose Pokemon");

            for (String pokemon : pokemonList) {
                menu.addOption(pokemon, pokemon);
            }

            event.replyEmbeds(embedBuilder.build())
                    .addActionRow(menu.build())
                    .setEphemeral(false)
                    .queue();
        } else {
            embedBuilder.setFooter(
                    String.format("There is no Pokemon in your team 👀 Use /catch to get one 🤩"));
            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {}
}
