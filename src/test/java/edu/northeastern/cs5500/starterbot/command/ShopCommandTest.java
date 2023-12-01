package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

class ShopCommandTest {
    @Test
    void testNameMatchesData() {
        ShopCommand shopCommand = new ShopCommand();
        String name = shopCommand.getName();
        CommandData commandData = shopCommand.getCommandData();

        assertThat(name).isEqualTo(commandData.getName());
    }
}
