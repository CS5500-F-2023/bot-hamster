package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

class BattleCommandTest {
    @Test
    void testNameMatchesData() {
        BattleCommand battleCommand = new BattleCommand();
        String name = battleCommand.getName();
        CommandData commandData = battleCommand.getCommandData();

        assertThat(name).isEqualTo(commandData.getName());
    }
}
