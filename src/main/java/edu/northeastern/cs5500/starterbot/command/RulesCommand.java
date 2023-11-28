package edu.northeastern.cs5500.starterbot.command;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Slf4j
public class RulesCommand implements SlashCommandHandler {

    static final String NAME = "rules";

    @Inject
    public RulesCommand() {
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
        return Commands.slash(getName(), "Rules - Learn about PokeBot");
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.debug("event: /rules");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("📜 PokeBot Rules");
        embedBuilder.setDescription(
                "**1. The /battle command👊🏼**"
                        + "+ Trainer must have at least 1 Pokemon to battle with other trainers"
                        + "\n"
                        + "**2. The /catch command🎣**"
                        + "+ Trainer must have 1 Pokeball to use the /catch command"
                        + "\n"
                        + "+ Trainer can catch new Pokemon when winning a fight against Pokemon in the wild"
                        + "\n"
                        + "**3. The /heal command🏥**"
                        + "+ Trainer must have 25 coins to heal a Pokemon"
                        + "\n"
                        + "**4. The /home command🏠**"
                        + "+ Trainer can check current team, coins, and Pokeball"
                        + "\n"
                        + "**5. The /search command💻**"
                        + "+ Trainer can look up a Pokemon by its Pokedex number in the game"
                        + "\n"
                        + "**6. The /shop command🛍️**"
                        + "+ Must have enough coins to purchase the item"
                        + "\n"
                        + "**7. The /trade command🔄**"
                        + "+ Must have at least 1 Pokemon to use the /trade command"
                        + "\n"
                        + "**8. The /sell command💰**"
                        + "+ Must have at least 1 Pokemon to sell Pokemon for coins");
        event.replyEmbeds(embedBuilder.build()).queue();
    }
}
