package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.controller.BattleController;
import edu.northeastern.cs5500.starterbot.controller.PokedexController;
import edu.northeastern.cs5500.starterbot.controller.PokemonController;
import edu.northeastern.cs5500.starterbot.controller.TrainerController;
import edu.northeastern.cs5500.starterbot.model.Trainer;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Slf4j
public class BattleCommand implements SlashCommandHandler {

    static final String NAME = "battle";

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
        Trainer myTrainer = trainerController.getTrainerForMemberId(myDiscordId);
        Trainer opponentTrainer = trainerController.getTrainerForMemberId(opponentDiscordId);

        if (myTrainer == null || opponentTrainer == null) {
            event.reply("Invalid trainers provided for battle.").queue();
            return;
        }

        String battleResult = battleController.compareTotal(myDiscordId, opponentDiscordId);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(
                String.format("<@%s> ", myDiscordId)
                        + String.format("vs <@%s>", opponentDiscordId)
                        + "\n\n"
                        + String.format("Winner is <@%s>!", battleResult));

        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
