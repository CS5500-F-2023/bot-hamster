package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

class SellCommandTest {
    @Test
    void testNameMatchesData() {
        SellCommand sellCommand = new SellCommand();
        String name = sellCommand.getName();
        CommandData commandData = sellCommand.getCommandData();

        assertThat(name).isEqualTo(commandData.getName());
    }
}
