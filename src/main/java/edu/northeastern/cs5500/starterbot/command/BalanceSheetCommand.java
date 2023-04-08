package edu.northeastern.cs5500.starterbot.command;

import edu.northeastern.cs5500.starterbot.constants.LogMessages;
import edu.northeastern.cs5500.starterbot.controller.BalanceSheetController;
import edu.northeastern.cs5500.starterbot.exception.rest.RestException;
import edu.northeastern.cs5500.starterbot.service.alphavantage.AlphaVantageBalanceSheet;
import edu.northeastern.cs5500.starterbot.service.alphavantage.AlphaVantageException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Singleton
@Slf4j
public class BalanceSheetCommand implements SlashCommandHandler {

    @Inject BalanceSheetController balanceSheetController;

    @Inject
    public BalanceSheetCommand() {}

    @Override
    @Nonnull
    public String getName() {
        return "balance";
    }

    @Override
    @Nonnull
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Ask the bot to fetch the balance sheet")
                .addOption(
                        OptionType.STRING, "ticker", "The bot will return the balance sheet", true);
    }

    public List<AlphaVantageBalanceSheet> getBalanceSheet(String ticker)
            throws RestException, AlphaVantageException {
        return balanceSheetController.getBalanceSheet(ticker);
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        log.info("event: /balance");
        var option = event.getOption("ticker");

        log.info("Ticker from balanceSheetCommand.java is: " + option);

        String ticker = option.getAsString();
        log.info("event: /balance ticker:" + ticker);

        List<AlphaVantageBalanceSheet> balanceSheets = null;
        try {
            balanceSheets = getBalanceSheet(ticker);
        } catch (RestException | AlphaVantageException exp) {
            log.error(String.format(LogMessages.ERROR_ALPHAVANTAGE_API, exp.getMessage()), exp);
            event.reply(LogMessages.ERROR_ALPHAVANTAGE_API_REPLY).queue();
            return;
        }

        if (balanceSheets == null) {
            event.reply(LogMessages.EMPTY_RESPONSE).queue();
            return;
        }

        log.info("Final balanceSheets: " + balanceSheets);

        List<MessageEmbed> balanceSheetEmbeds = renderBalanceSheets(balanceSheets);
        for (MessageEmbed embed : balanceSheetEmbeds) {
            event.getChannel().sendMessageEmbeds(embed).queue();
        }
    }

    public List<MessageEmbed> renderBalanceSheets(List<AlphaVantageBalanceSheet> balanceSheets) {
        List<MessageEmbed> messageEmbeds = new ArrayList<>();
        for (AlphaVantageBalanceSheet alphaVantageBalanceSheet : balanceSheets) {
            messageEmbeds.add(renderBalanceSheet(alphaVantageBalanceSheet));
        }
        return messageEmbeds;
    }

    public MessageEmbed renderBalanceSheet(AlphaVantageBalanceSheet balanceSheet) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.addField("Reported Currency", balanceSheet.getReportedCurrency(), false);
        embed.addField("Fiscal Date Ending", balanceSheet.getFiscalDateEnding(), false);
        embed.addField("Total Assets", balanceSheet.getTotalAssets(), false);
        embed.addField("Investments", balanceSheet.getInvestments(), false);
        embed.addField("Total Current Assets", balanceSheet.getTotalCurrentAssets(), false);
        embed.addField("Current Debt", balanceSheet.getCurrentDebt(), false);
        embed.addField("Total Liabilities", balanceSheet.getTotalLiabilities(), false);

        return embed.build();
    }
}
