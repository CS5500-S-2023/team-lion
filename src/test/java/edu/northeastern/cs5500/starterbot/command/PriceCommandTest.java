package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PriceCommandTest {

    PriceCommand priceCommand;
    CommandData commandData;

    @BeforeEach
    void setUp() {
        priceCommand = new PriceCommand();
        commandData = priceCommand.getCommandData();
    }

    @Test
    void testGetName() {
        String name = new PriceCommand().getName();
        assertThat(name).isEqualTo(commandData.getName());
    }

    @Test
    void testGetCommandData() {
        CommandData testPriceCommandData = new PriceCommand().getCommandData();
        assertThat(testPriceCommandData).isNotNull();
        assertThat(testPriceCommandData.getName()).isEqualTo(commandData.getName());
    }

    @Test
    void testOnSlashCommandInteraction() {}
}
