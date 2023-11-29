package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.controller.TrainerController;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.bson.types.ObjectId;

@Slf4j
public class SellCommand implements SlashCommandHandler, StringSelectHandler {

    static final String NAME = "sell";

    @Inject PokemonController pokemonController;

    @Inject TrainerController trainerController;

    @Inject
    public SellCommand() {
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
        return Commands.slash(getName(), "Sell - Sell Pokemon for coins");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /sell");

        String trainerDiscordId = event.getMember().getId();
        EmbedBuilder embedBuilder = new EmbedBuilder();

        // UI design
        embedBuilder.setDescription(
                String.format(
                        "**Hey <@%s>! Please choose the Pokemon to sell** 💰", trainerDiscordId));

        // image
        embedBuilder.setImage(
                "https://static0.gamerantimages.com/wordpress/wp-content/uploads/2022/07/Pokemon-Fan-Plays-Poke-Mart-Theme-at-Their-Covenience-Store-to-Boost-Work-Ethic.jpg");

        List<String> currentTeam =
                trainerController.getPokemonNamesFromTrainerInventory(trainerDiscordId);
        if (currentTeam.size() > 0) {
            // dropdown menu
            StringSelectMenu.Builder menu =
                    StringSelectMenu.create(NAME).setPlaceholder("Pokemon Team 🎖️");

            for (String pokemon : currentTeam) {
                menu.addOption(pokemon, pokemon);
            }

            event.replyEmbeds(embedBuilder.build())
                    .addActionRow(menu.build())
                    .setEphemeral(false)
                    .queue();
        } else {
            // in case of no Pokemon in the team
            embedBuilder.setFooter(
                    String.format("There is no Pokemon in your team 👀 Use /catch to get one 🤩"));
            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        String trainerDiscordId = event.getMember().getId();
        Trainer trainer = trainerController.getTrainerForMemberId(trainerDiscordId);

        // remove pokemon from trainer's team
        final String response = event.getInteraction().getValues().get(0);
        Objects.requireNonNull(response);
        ObjectId pokemonId =
                trainerController.getPokemonIdByPokemonName(trainerDiscordId, response);
        Pokemon pokemon = pokemonController.getPokemonById(pokemonId.toString());
        trainerController.removePokemonFromTrainer(trainer, pokemon);

        // update balance
        trainerController.updateCoinBalanceForTrainer(trainerDiscordId, 50);

        event.reply(
                        String.format(
                                "Pokemon %s has been removed from your team 👋\n50 Coins has been added to your balance 💰",
                                response))
                .queue();
    }
}
