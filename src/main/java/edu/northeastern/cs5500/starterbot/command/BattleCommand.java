package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.BattleController;
import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.controller.TrainerController;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies;
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
public class BattleCommand implements SlashCommandHandler, StringSelectHandler, ButtonHandler {

    static final String NAME = "battle";
    static final String IMAGE =
            "https://fc05.deviantart.net/fs71/f/2013/098/e/7/pokemon_x_y_battle_scene_by_jenske05-d5ynr9c.png";

    @Inject PokedexController pokedexController;
    @Inject PokemonController pokemonController;
    @Inject TrainerController trainerController;
    @Inject BattleController battleController;

    @Inject
    public BattleCommand() {
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
        return Commands.slash(getName(), "Engage in a battle with another trainer!")
                .addOption(OptionType.USER, "trainer", "Enter the Trainer to battle with", true);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /battle");

        String myDiscordId = event.getMember().getId();
        String opponentDiscordId = event.getOption("trainer").getAsMember().getId();
        Collection<String> curTeam =
                trainerController.getPokemonNamesFromTrainerInventory(myDiscordId);

        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (battleController.battleWithSelf(myDiscordId, opponentDiscordId)) {
            embedBuilder.setDescription("Trainer must choose someone else for battle");
            event.replyEmbeds(embedBuilder.build()).queue();
        } else if (!battleController.validateBattleTeam(myDiscordId)
                || !battleController.validateBattleTeam(opponentDiscordId)) {
            embedBuilder.setDescription("Trainers must have at least one Pokemon for battle");
            event.replyEmbeds(embedBuilder.build()).queue();
        } else {
            StringSelectMenu.Builder menu =
                    StringSelectMenu.create(NAME).setPlaceholder("Choose Pokemon");

            for (String pokemon : curTeam) {
                menu.addOption(pokemon, String.format("%s:%s", pokemon, opponentDiscordId));
            }

            embedBuilder.setTitle("Pokemon Battle");
            embedBuilder.setImage(IMAGE);
            embedBuilder.setDescription("Choose a Pokemon for Battle");

            event.replyEmbeds(embedBuilder.build())
                    .addActionRow(menu.build())
                    .setEphemeral(false)
                    .queue();
        }
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {

        String myDiscordId = event.getMember().getId();
        final String response = event.getInteraction().getValues().get(0);
        Objects.requireNonNull(response);
        String[] values = response.split(":");
        String pokemonName = values[0];
        String opponentDiscordId = values[1];

        ObjectId pokemonId = trainerController.getPokemonIdByPokemonName(myDiscordId, pokemonName);
        Pokemon pokemon = pokemonController.getPokemonById(pokemonId.toString());

        String replyMessage =
                String.format("Battle request sent. Waiting for opponent to respond.");

        event.reply(replyMessage).queue();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Attention!");
        embedBuilder.setDescription(
                String.format(
                        "<@%s> is challenging <@%s> for a 1 vs 1 Pokemon battle!",
                        myDiscordId, opponentDiscordId));

        event.getHook()
                .sendMessageEmbeds(embedBuilder.build())
                .addActionRow(
                        Button.primary(
                                getName()
                                        + ":accept:"
                                        + myDiscordId
                                        + ":"
                                        + opponentDiscordId
                                        + ":"
                                        + pokemon.getId().toString(),
                                "Accept"),
                        Button.secondary(
                                getName()
                                        + ":decline:"
                                        + myDiscordId
                                        + ":"
                                        + opponentDiscordId
                                        + ":"
                                        + pokemon.getId().toString(),
                                "Decline"))
                .queue();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {

        String buttonRespondent = event.getMember().getId();
        String myDiscordId = event.getButton().getId().split(":")[2];
        String opponentDiscordId = event.getButton().getId().split(":")[4];
        String pokemonId = event.getButton().getId().split(":")[4];
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (buttonRespondent.equals(opponentDiscordId)) {
            if (event.getButton().getId().startsWith(getName() + ":decline:")) {
                event.reply(String.format("<@%s> declined the battle.", opponentDiscordId)).queue();
            } else if (event.getButton().getId().startsWith(getName() + ":accept:")) {
                StringSelectMenu.Builder menu =
                        StringSelectMenu.create(NAME).setPlaceholder("Choose Pokemon");
                for (String pokemon :
                        trainerController.getPokemonNamesFromTrainerInventory(opponentDiscordId)) {
                    menu.addOption(pokemon, String.format("%s:%s", pokemon, opponentDiscordId));
                }

                event.replyEmbeds(embedBuilder.build())
                        .addActionRow(menu.build())
                        .setEphemeral(false)
                        .queue();

                // String battleResult = battleController.compareTotal(myDiscordId,
                // opponentDiscordId);
                // embedBuilder.setDescription(
                //         String.format("<@%s> ", myDiscordId)
                //                 + String.format("vs <@%s>", opponentDiscordId)
                //                 + "\n\n"
                //                 + String.format("Winner is <@%s>!", battleResult));

                event.reply("Battle result").queue();
            }
        } else {
            event.reply("Sorry, you cannot respond to this battle request.").queue();
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
