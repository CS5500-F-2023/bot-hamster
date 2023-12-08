package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.BattleController;
import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.controller.TrainerController;
import edu.northeastern.cs5500.starterbot.model.Battle;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.model.PokemonSpecies;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

@Slf4j
public class BattleCommand implements SlashCommandHandler, ButtonHandler {

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

        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (battleController.battleWithSelf(myDiscordId, opponentDiscordId)) {
            embedBuilder.setDescription("Trainer must choose other trainers for battle");
            event.replyEmbeds(embedBuilder.build()).queue();
        } else if (!battleController.validateBattleTeam(myDiscordId)
                || !battleController.validateBattleTeam(opponentDiscordId)) {
            embedBuilder.setDescription("Trainers must have at least one Pokemon for battle");
            event.replyEmbeds(embedBuilder.build()).queue();
        } else {

            embedBuilder.setTitle("Pokemon Battle");
            embedBuilder.setImage(IMAGE);
            embedBuilder.setDescription(
                    String.format(
                            "<@%s> is challenging <@%s> for a 1 vs 1 Pokemon battle!",
                            myDiscordId, opponentDiscordId));

            event.replyEmbeds(embedBuilder.build())
                    .addActionRow(
                            Button.primary(
                                    getName() + ":accept:" + myDiscordId + ":" + opponentDiscordId,
                                    "Accept"),
                            Button.secondary(
                                    getName() + ":decline:" + myDiscordId + ":" + opponentDiscordId,
                                    "Decline"))
                    .queue();
        }
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {

        String buttonRespondent = event.getMember().getId();
        String myDiscordId = event.getButton().getId().split(":")[2];
        String opponentDiscordId = event.getButton().getId().split(":")[3];

        if (buttonRespondent.equals(opponentDiscordId)) {
            if (event.getButton().getId().startsWith(getName() + ":decline:")) {
                event.reply(String.format("<@%s> declined the battle.", opponentDiscordId)).queue();
            } else if (event.getButton().getId().startsWith(getName() + ":accept:")) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                Battle currentBattle =
                        battleController.createBattle(myDiscordId, opponentDiscordId);
                EmbedBuilder myPokemonCard =
                        createPokemonEmbed(
                                currentBattle.getMyDiscordId(), currentBattle.getMyPokemonId());
                EmbedBuilder opponentPokemonCard =
                        createPokemonEmbed(
                                currentBattle.getOpponentDiscordId(),
                                currentBattle.getOpponentPokemonId());

                String battleResult = battleController.compareTotal(currentBattle);

                String resultDescription;
                if (battleResult.equals("Tie")) {
                    resultDescription = "The battle ended in a tie!🤝";
                } else {
                    resultDescription = String.format("🏵️<@%s> won the battle", battleResult);
                }

                embedBuilder.setTitle("Battle Result");
                embedBuilder.setDescription(resultDescription);
                embedBuilder.setImage(
                        "https://cl.buscafs.com/www.levelup.com/public/uploads/images/811608/811608.jpg");
                event.replyEmbeds(
                                myPokemonCard.build(),
                                opponentPokemonCard.build(),
                                embedBuilder.build())
                        .queue();
            }
        } else {
            event.reply("Sorry, you cannot respond to this battle request.").queue();
        }
    }

    private EmbedBuilder createPokemonEmbed(String myDiscordId, String pokemonId) {

        Pokemon pokemon = pokemonController.getPokemonById(pokemonId);
        PokemonSpecies species =
                pokedexController.getPokemonSpeciesByNumber(pokemon.getPokedexNumber());

        String pokemonInfo =
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
                        myDiscordId);

        return new EmbedBuilder().setDescription(pokemonInfo).setThumbnail(species.getImageUrl());
    }
}
