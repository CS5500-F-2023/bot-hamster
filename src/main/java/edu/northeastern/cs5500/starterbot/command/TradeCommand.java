package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.controller.TrainerController;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import java.util.Collection;
import java.util.Objects;
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
import org.bson.types.ObjectId;

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
                        OptionType.USER, "player", "Enter the user you want to trade with", true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /trade");

        var otherPlayer = event.getOption("player").getAsMember();

        String trainerDiscordId = event.getMember().getId();
        Collection<String> pokemonList =
                trainerController.getPokemonNamesFromTrainerInventory(trainerDiscordId);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Trade Pokemon with Friend pika pika!");
        embedBuilder.setDescription("Choose a Pokemon from your inventory to trade");
        embedBuilder.setImage("http://media3.giphy.com/media/Zqy8kRlwfwFnq/giphy.gif");

        if (!pokemonList.isEmpty()) {
            StringSelectMenu.Builder menu =
                    StringSelectMenu.create(NAME).setPlaceholder("Choose Pokemon");

            for (String pokemon : pokemonList) {
                menu.addOption(pokemon, String.format("%s:%s", pokemon, otherPlayer.getId()));
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
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        String trainerDiscordId = event.getMember().getId();
        final String response = event.getInteraction().getValues().get(0);
        Objects.requireNonNull(response);

        String[] values = response.split(":");
        String pokemonName = values[0];
        String otherTrainerId = values[1];

        Trainer trainer = trainerController.getTrainerForMemberId(trainerDiscordId);
        ObjectId pokemonId =
                trainerController.getPokemonIdByPokemonName(trainerDiscordId, pokemonName);
        Pokemon pokemon = pokemonController.getPokemonById(pokemonId.toString());
        pokemonName =
                pokedexController.getPokemonSpeciesByNumber(pokemon.getPokedexNumber()).getName();

        //     tradeOfferController.createNewOffering(trainer, pokemon);

        event.reply(String.format("Trade offer for Pokemon successfully created!", pokemonName))
                .queue();
    }
}
