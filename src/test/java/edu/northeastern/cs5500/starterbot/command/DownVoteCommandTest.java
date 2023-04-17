package edu.northeastern.cs5500.starterbot.command;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DownVoteCommandTest {
    DownVoteCommand downVoteCommand;
    CommandData commandData;

    @BeforeEach
    void setUp() {
        downVoteCommand = new DownVoteCommand();
        commandData = downVoteCommand.getCommandData();
    }

    @Test
    void testGetName() {
        String name = new DownVoteCommand().getName();
        assertThat(name).isEqualTo(commandData.getName());
    }

    @Test
    void testGetCommandData() {
        CommandData testDownVoteCommandData = new DownVoteCommand().getCommandData();
        assertThat(testDownVoteCommandData).isNotNull();
        assertThat(testDownVoteCommandData.getName()).isEqualTo(commandData.getName());
    }

    @Test
    void testCreateDocumentWithTicker() {
        Document testCreateDocumentWithTicker =
                new DownVoteCommand().createDocumentWithTicker("aapl");
        String name = testCreateDocumentWithTicker.getString("ticker");
        assertEquals("aapl", name);
    }

    @Test
    void testCreateUpdateDocument() {
        Document testDocument = new Document("$inc", new Document("votes", -1));
        assertEquals(new DownVoteCommand().createUpDateDocument(), testDocument);
    }
}
