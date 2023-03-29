package edu.northeastern.cs5500.starterbot.controller;

import edu.northeastern.cs5500.starterbot.exception.rest.BadRequestException;
import edu.northeastern.cs5500.starterbot.exception.rest.RestException;
import edu.northeastern.cs5500.starterbot.service.QuoteService;
import edu.northeastern.cs5500.starterbot.service.alphavantage.AlphaVantageGlobalQuote;
import javax.inject.Inject;

public class QuoteController {

    QuoteService quoteService;

    @Inject
    QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    public AlphaVantageGlobalQuote getQuote(String tickerSymbol) throws RestException {
        if (tickerSymbol == null || tickerSymbol.isBlank()) {
            throw new BadRequestException("ticker cannot be null or empty");
        }

        tickerSymbol = tickerSymbol.strip().toUpperCase();

        if (!tickerSymbol.matches("^[A-Z]+(?:[.=\\-][A-Z]+)?$")) {
            throw new BadRequestException("ticker had invalid characters");
        }

        return quoteService.getQuote(tickerSymbol);
    }
}
