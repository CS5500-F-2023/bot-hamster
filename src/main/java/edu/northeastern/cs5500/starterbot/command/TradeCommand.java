package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.controller.TradeOfferController;
import edu.northeastern.cs5500.starterbot.controller.TrainerController;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies;
import edu.northeastern.cs5500.starterbot.model.TradeOffer;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.bson.types.ObjectId;

@Slf4j
public class TradeCommand implements SlashCommandHandler, StringSelectHandler, ButtonHandler {

    static final String NAME = "trade";

    @Inject PokedexController pokedexController;

    @Inject PokemonController pokemonController;

    @Inject TrainerController trainerController;

    @Inject TradeOfferController tradeOfferController;

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
        embedBuilder.setDescription("Choose a Pokemon from your inventory to trade.");
        embedBuilder.setImage("http://media3.giphy.com/media/Zqy8kRlwfwFnq/giphy.gif");

        if (otherPlayer.getId().equals(trainerDiscordId)) {
            embedBuilder.setFooter("Cannot trade with yourself!");
            event.replyEmbeds(embedBuilder.build()).queue();
        } else if (!pokemonList.isEmpty()) {
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
            embedBuilder.setFooter("There is no Pokemon in your team 👀 Use /catch to get one 🤩");
            event.replyEmbeds(embedBuilder.build()).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        String trainerDiscordId = event.getMember().getId();

        // Retrieve the selected Pokemon and other trainer's Discord ID from the interaction.
        final String response = event.getInteraction().getValues().get(0);
        Objects.requireNonNull(response);
        String[] values = response.split(":");
        String pokemonName = values[0];
        String otherTrainerId = values[1];

        ObjectId pokemonId =
                trainerController.getPokemonIdByPokemonName(trainerDiscordId, pokemonName);
        Pokemon pokemon = pokemonController.getPokemonById(pokemonId.toString());

        Collection<String> otherPlayerPokemonList =
                trainerController.getPokemonNamesFromTrainerInventory(otherTrainerId);

        String replyMessage =
                otherPlayerPokemonList.isEmpty()
                        ? "Trade failed. Your friend has no Pokemon in the inventory."
                        : String.format(
                                "Trade offer for Pokemon %s has been sent! Please wait for your friend to respond.",
                                pokemonName);

        event.reply(replyMessage).queue();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Congratulations!");
        embedBuilder.setDescription(
                String.format(
                        "<@%s> You received an trade offer from Player <@%s>! %n%n Click the button to accept or decline the offer.",
                        otherTrainerId, trainerDiscordId));

        EmbedBuilder embedBuilderForPokemon = createPokemonEmbed(trainerDiscordId, pokemon);

        Pokemon randomPokemon = trainerController.getRandomPokemonFromTrainer(otherTrainerId);
        EmbedBuilder embedBuilderForOtherPlayerPokemon =
                createPokemonEmbed(otherTrainerId, randomPokemon);

        // Send the additional embedded message
        event.getHook()
                .sendMessageEmbeds(
                        embedBuilder.build(),
                        embedBuilderForPokemon.build(),
                        embedBuilderForOtherPlayerPokemon.build())
                .addActionRow(
                        Button.primary(
                                getName()
                                        + ":accept:"
                                        + pokemon.getId().toString()
                                        + ":"
                                        + randomPokemon.getId().toString()
                                        + ":"
                                        + trainerDiscordId,
                                "Accept"),
                        Button.secondary(
                                getName()
                                        + ":decline:"
                                        + pokemon.getId().toString()
                                        + ":"
                                        + randomPokemon.getId().toString()
                                        + ":"
                                        + trainerDiscordId,
                                "Decline"))
                .queue();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        String otherTrainerId = event.getMember().getId();

        // Extract relevant information from the button ID.
        String pokemonId = event.getButton().getId().split(":")[2];
        String otherTrainerPokemonId = event.getButton().getId().split(":")[3];
        String trainerDiscordId = event.getButton().getId().split(":")[4];

        // Retrieve the trainers and Pokemon involved in the trade.
        Trainer trainer = trainerController.getTrainerForMemberId(trainerDiscordId);
        Trainer otherTrainer = trainerController.getTrainerForMemberId(otherTrainerId);
        Pokemon pokemon = pokemonController.getPokemonById(pokemonId);
        Pokemon otherTrainerPokemon = pokemonController.getPokemonById(otherTrainerPokemonId);

        // Check if the button interaction is valid based on the trainers' Discord IDs.
        if (trainerDiscordId.equals(otherTrainerId)) {
            event.reply("Sorry, you cannot respond to this trade offer.").queue();
        } else if (event.getButton().getId().startsWith(getName() + ":decline:")) {
            event.reply(String.format("Player <@%s> decline the trade offer.", otherTrainerId))
                    .queue();
        } else if (event.getButton().getId().startsWith(getName() + ":accept:")) {
            TradeOffer tradeOffer =
                    tradeOfferController.createNewOffering(
                            trainer, pokemon, otherTrainer, otherTrainerPokemon);
            tradeOfferController.acceptOffer(tradeOffer);

            event.reply("Trade success! Use /home to reveal your new Pokemon 🔍").queue();
        }
    }

    private EmbedBuilder createPokemonEmbed(String trainerDiscordId, Pokemon pokemon) {
        PokemonSpecies species =
                pokedexController.getPokemonSpeciesByNumber(pokemon.getPokedexNumber());

        String description =
                String.format(
                        "<@%s>\n\n"
                                + "**"
                                + species.getName()
                                + "**\n\n"
                                + "Level: "
                                + Integer.toString(pokemon.getLevel())
                                + "\n"
                                + "Mood: "
                                + Integer.toString(pokemon.getMood())
                                + "\n"
                                + "HP: "
                                + Integer.toString(pokemon.getHp())
                                + "\n"
                                + "Attack: "
                                + Integer.toString(pokemon.getAttack())
                                + "\n"
                                + "Defense: "
                                + Integer.toString(pokemon.getDefense())
                                + "\n"
                                + "SpecialAttack: "
                                + Integer.toString(pokemon.getSpecialAttack())
                                + "\n"
                                + "SpecialDefense: "
                                + Integer.toString(pokemon.getSpecialDefense())
                                + "\n"
                                + "Speed: "
                                + Integer.toString(pokemon.getSpeed())
                                + "\n"
                                + "Total: "
                                + Integer.toString(pokemon.getTotal()),
                        trainerDiscordId);

        return new EmbedBuilder().setDescription(description).setThumbnail(species.getImageUrl());
    }
}
