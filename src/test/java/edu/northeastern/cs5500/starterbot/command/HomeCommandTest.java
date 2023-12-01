package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

class HomeCommandTest {
    @Test
    void testNameMatchesData() {
        HomeCommand homeCommand = new HomeCommand();
        String name = homeCommand.getName();
        CommandData commandData = homeCommand.getCommandData();

        assertThat(name).isEqualTo(commandData.getName());
    }
}
