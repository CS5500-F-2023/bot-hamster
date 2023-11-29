package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.controller.TrainerController;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import java.util.Collection;
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
public class HealCommand implements SlashCommandHandler, StringSelectHandler {

    static final String NAME = "heal";

    @Inject PokedexController pokedexController;

    @Inject PokemonController pokemonController;

    @Inject TrainerController trainerController;

    @Inject
    public HealCommand() {
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
        return Commands.slash(getName(), "Heal your Pokemon at hospital");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /heal");

        String trainerDiscordId = event.getMember().getId();
        Collection<String> pokemonList =
                trainerController.getPokemonNamesFromTrainerInventory(trainerDiscordId);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("SOS Your Pokemon is injured!");
        embedBuilder.setDescription("Heal your Pokemon with 25 coins");
        embedBuilder.setImage(
                "https://attackofthefanboy.com/256x256/wp-content/uploads/2016/07/Pokemon-Go-How-to-heal-and-revive.jpg");

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
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        String trainerDiscordId = event.getMember().getId();
        final String response = event.getInteraction().getValues().get(0);
        Objects.requireNonNull(response);

        int coinBalance = trainerController.getCoinBalanceFromTrainer(trainerDiscordId);

        if (coinBalance <= 0) {
            event.reply(
                            String.format(
                                    "Cannot heal Pokemon %s. Player <@%s> has low coin balance.",
                                    response, trainerDiscordId))
                    .queue();
        } else {
            trainerController.updateCoinBalanceForTrainer(trainerDiscordId, -25);
            trainerController.updatePokemonStatsForTrainer(trainerDiscordId, response, +1);
            ObjectId pokemonId =
                    trainerController.getPokemonIdByPokemonName(trainerDiscordId, response);
            Pokemon pokemon = pokemonController.getPokemonById(pokemonId.toString());
            event.reply(
                            String.format(
                                    "Pokemon %s has recovered to hp = %s and total stats = %s! Player <@%s> current coin balance is %s.",
                                    response,
                                    pokemon.getHp(),
                                    pokemon.getTotal(),
                                    trainerDiscordId,
                                    trainerController.getCoinBalanceFromTrainer(trainerDiscordId)))
                    .queue();

            event.reply(response).queue();
        }
    }
}
