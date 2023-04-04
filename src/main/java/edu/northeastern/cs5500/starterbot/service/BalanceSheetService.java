package edu.northeastern.cs5500.starterbot.service;

import edu.northeastern.cs5500.starterbot.exception.rest.RestException;
import edu.northeastern.cs5500.starterbot.service.alphavantage.AlphaVantageBalanceSheet;
import edu.northeastern.cs5500.starterbot.service.alphavantage.AlphaVantageException;
import java.util.List;

public interface BalanceSheetService extends Service {
    List<AlphaVantageBalanceSheet> getBalanceSheet(String symbol)
            throws RestException, AlphaVantageException;
}
