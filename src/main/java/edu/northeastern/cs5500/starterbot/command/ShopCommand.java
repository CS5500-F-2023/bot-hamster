package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.controller.TrainerController;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bson.types.ObjectId;

@Slf4j
public class ShopCommand implements SlashCommandHandler, ButtonHandler, StringSelectHandler {

    static final String NAME = "shop";
    static final int pokeballPrice = 25;
    static final int milkteaPrice = 10;
    static final int energybarPrice = 15;

    @Inject PokemonController pokemonController;

    @Inject TrainerController trainerController;

    @Inject PokedexController pokedexController;

    @Inject
    public ShopCommand() {
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
        return Commands.slash(getName(), "Shop - Purchase items for your team");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /shop");

        EmbedBuilder embedBuilder = new EmbedBuilder();

        // UI design
        embedBuilder.setTitle("Welcome to the Pokemon Item Market 💁");
        embedBuilder.setDescription(
                "🪩 PokeBall   25 coins   -> Catch new Pokemon\n\n"
                        + "🧋 MilkTea   10 coins   -> Random Pokemon HP + 4 \n\n"
                        + "🍡 EnergyBar   15 coins   -> Selected Pokemon HP + 5\n\n\n"
                        + "More to come...🧁 🍕 🍟 🍜");

        // image
        embedBuilder.setImage(
                "https://archives.bulbagarden.net/media/upload/0/08/Pok%C3%A9_Mart_interior_HGSS.png");

        // buttons
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        messageCreateBuilder.addActionRow(
                Button.danger(getName() + ":ball", "🪩 Pokeball"),
                Button.primary(getName() + ":milktea", "🧋 Milktea"),
                Button.success(getName() + ":energybar", "🍡 Energybar"));
        messageCreateBuilder = messageCreateBuilder.addEmbeds(embedBuilder.build());
        event.reply(messageCreateBuilder.build()).queue();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        String trainerDiscordId = event.getMember().getId();
        if (event.getButton().getId().startsWith(getName() + ":ball")) {
            pokeballButton(event, trainerDiscordId);
        } else if (event.getButton().getId().startsWith(getName() + ":milktea")) {
            milkteaButton(event, trainerDiscordId);
        } else if (event.getButton().getId().startsWith(getName() + ":energy")) {
            energybarButton(event, trainerDiscordId);
        }
    }

    private void pokeballButton(@Nonnull ButtonInteractionEvent event, String trainerDiscordId) {
        int balance = trainerController.getCoinBalanceFromTrainer(trainerDiscordId);
        if (balance < pokeballPrice) {
            event.reply("Your current balance is not enough 😱").queue();
        } else {
            trainerController.updateCoinBalanceForTrainer(trainerDiscordId, -pokeballPrice);
            trainerController.updatePokeBallForTrainer(trainerDiscordId, 1);
            event.reply(
                            "👏 You have successfully purchased a Pokeball! \n Use /home to check your inventory!")
                    .queue();
        }
    }

    private void milkteaButton(@Nonnull ButtonInteractionEvent event, String trainerDiscordId) {
        int balance = trainerController.getCoinBalanceFromTrainer(trainerDiscordId);
        if (balance < milkteaPrice) {
            event.reply("Your current balance is not enough 😱").queue();
        } else {
            List<String> currentTeam =
                    trainerController.getPokemonNamesFromTrainerInventory(trainerDiscordId);
            if (currentTeam.size() == 0) {
                event.reply("Your have no Pokemon to use items...").queue();
            } else {
                trainerController.updateCoinBalanceForTrainer(trainerDiscordId, -milkteaPrice);
                Pokemon randomPokemon =
                        trainerController.getRandomPokemonFromTrainer(trainerDiscordId);
                pokemonController.updatePokemonHP(randomPokemon, 2);
                String pokemonName =
                        pokedexController
                                .getPokemonSpeciesByNumber(randomPokemon.getPokedexNumber())
                                .getName();
                event.reply(
                                String.format(
                                        "👏 You have successfully purchased a MilkTea 🧋!\n The stats of %s has been updated!",
                                        pokemonName))
                        .queue();
            }
        }
    }

    private void energybarButton(@Nonnull ButtonInteractionEvent event, String trainerDiscordId) {
        int balance = trainerController.getCoinBalanceFromTrainer(trainerDiscordId);
        if (balance < energybarPrice) {
            event.reply("Your current balance is not enough 😱").queue();
        }
        List<String> currentTeam =
                trainerController.getPokemonNamesFromTrainerInventory(trainerDiscordId);
        if (currentTeam.size() == 0) {
            event.reply("Your have no Pokemon to use items...").queue();
        } else {
            trainerController.updateCoinBalanceForTrainer(trainerDiscordId, -energybarPrice);

            // Create and send the menu
            StringSelectMenu.Builder menu =
                    StringSelectMenu.create(NAME).setPlaceholder("Choose Pokemon");
            for (String pokemon : currentTeam) {
                menu.addOption(pokemon, pokemon);
            }
            event.reply(
                            "👏 You have successfully purchased a EnergyBar 🍡! \n Choose one Pokemon to use the item!")
                    .addActionRow(menu.build())
                    .queue();
        }
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        String trainerDiscordId = event.getMember().getId();
        final String response = event.getInteraction().getValues().get(0);
        Objects.requireNonNull(response);

        ObjectId pokemonId =
                trainerController.getPokemonIdByPokemonName(trainerDiscordId, response);
        Pokemon pokemon = pokemonController.getPokemonByObjectId(pokemonId);
        pokemonController.updatePokemonHP(pokemon, 2);

        event.reply(
                        String.format(
                                "The stats of %s has been updated 🎉 \nSee you next time!",
                                response))
                .queue();
    }
}
