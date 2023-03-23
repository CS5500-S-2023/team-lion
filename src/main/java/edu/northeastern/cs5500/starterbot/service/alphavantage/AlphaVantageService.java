package edu.northeastern.cs5500.starterbot.service.alphavantage;

import com.google.gson.Gson;
import edu.northeastern.cs5500.starterbot.service.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class AlphaVantageService implements Service, AlphaVantageApi {
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
    public AlphaVantageGlobalQuote getGlobalQuote(String symbol) throws AlphaVantageException {
        String queryUrl = "function=GLOBAL_QUOTE&symbol=" + symbol;
        String response = getRequest(queryUrl);

        Gson gson = new Gson();
        AlphaVantageGlobalQuote quote =
                gson.fromJson(response, AlphaVantageGlobalQuoteResponse.class).getGlobalQuote();
        if (quote.getSymbol() == null) {
            // This means the given symbol was not valid and no data was returned
            return null;
        }
        return quote;
    }

    private String getRequest(String queryUrl) throws AlphaVantageException {

        StringBuilder val = new StringBuilder();
        try {
            URL url = new URL(BASE_URL + queryUrl + "&apikey=" + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new AlphaVantageException("HTTP Failed: " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                val.append(output);
            }
            conn.disconnect();

        } catch (Exception ignored) {
            throw new AlphaVantageException(ignored);
        }
        return val.toString();
    }

    @Override
    public AlphaVantageNewsFeed[] getNewsSentiment(String symbol, String fromTime)
            throws AlphaVantageException {
        String queryUrl = "function=NEWS_SENTIMENT&tickers=" + symbol + "&time_from=" + fromTime;
        String response = getRequest(queryUrl);

        System.out.println("response length = " + response.length());

        Gson gson = new Gson();
        var newsFeed = gson.fromJson(response, AlphaVantageNewsResponse.class).getFeed();
        if (newsFeed == null) {
            // This means the given symbol was not valid and no data was returned
            return null;
        }
        return newsFeed;
    }
}
