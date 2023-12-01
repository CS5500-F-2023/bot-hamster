package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

class CatchCommandTest {
    @Test
    void testNameMatchesData() {
        CatchCommand catchCommand = new CatchCommand();
        String name = catchCommand.getName();
        CommandData commandData = catchCommand.getCommandData();

        assertThat(name).isEqualTo(commandData.getName());
    }
}
