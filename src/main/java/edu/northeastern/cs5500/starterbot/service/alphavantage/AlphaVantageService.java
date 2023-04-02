package edu.northeastern.cs5500.starterbot.service.alphavantage;

import com.google.gson.Gson;

import edu.northeastern.cs5500.starterbot.constants.LogMessages;
import edu.northeastern.cs5500.starterbot.exception.rest.BadRequestException;
import edu.northeastern.cs5500.starterbot.exception.rest.InternalServerErrorException;
import edu.northeastern.cs5500.starterbot.exception.rest.NotFoundException;
import edu.northeastern.cs5500.starterbot.exception.rest.RestException;
import edu.northeastern.cs5500.starterbot.service.QuoteService;
import edu.northeastern.cs5500.starterbot.service.BalanceSheetService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class AlphaVantageService implements QuoteService, BalanceSheetService {
    private static final String BASE_URL = "https://www.alphavantage.co/query?";
    private final String apiKey;

    public AlphaVantageService(String alphaVantageApiKey) {
        this.apiKey = alphaVantageApiKey;
    }

    @Override
    public void register() {
        log.info("AlphaVantageService > register");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("ALPHA_VANTAGE_API_KEY is required");
        }
    }

    @Inject
    public AlphaVantageService() {
        this(new ProcessBuilder().environment().get("ALPHA_VANTAGE_API_KEY"));
    }

    @Override
    public AlphaVantageGlobalQuote getQuote(String symbol) throws RestException {
        String queryUrl = "function=GLOBAL_QUOTE&symbol=" + symbol;
        String response = getRequest(queryUrl);

        Gson gson = new Gson();
        AlphaVantageGlobalQuote quote =
                gson.fromJson(response, AlphaVantageGlobalQuoteResponse.class).getGlobalQuote();
        if (quote.getSymbol() == null) {
            throw new NotFoundException();
        }
        return quote;
    }

    @SneakyThrows({MalformedURLException.class, IOException.class})
    private String getRequest(String queryUrl) throws RestException {
        StringBuilder val = new StringBuilder();
        URL url = new URL(BASE_URL + queryUrl + "&apikey=" + apiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        switch (conn.getResponseCode()) {
            case HttpURLConnection.HTTP_OK:
                break;
            case HttpURLConnection.HTTP_BAD_REQUEST:
                throw new BadRequestException();
            case HttpURLConnection.HTTP_NOT_FOUND:
                throw new NotFoundException();
            case HttpURLConnection.HTTP_INTERNAL_ERROR:
                throw new InternalServerErrorException();
            default:
                throw new RestException("unknown", conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

        String output;
        while ((output = br.readLine()) != null) {
            val.append(output);
        }
        conn.disconnect();

        return val.toString();
    }

    @Override
    public List<AlphaVantageBalanceSheet> getBalanceSheet(String symbol) throws RestException, AlphaVantageException {
        String queryUrl = "function=BALANCE_SHEET&tickers=" + symbol;
        String response = getRequest(queryUrl);
        
        var balanceSheet = new Gson().fromJson(response, AlphaVantageBalanceSheetResponse.class).getFeed();
        if (balanceSheet == null) {
            log.error(String.format(LogMessages.EMPTY_RESPONSE, symbol), symbol);
        }

        System.out.println("In alpha vantage service: " + balanceSheet);

        return balanceSheet;
    }
}
