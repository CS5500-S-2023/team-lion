package edu.northeastern.cs5500.starterbot.service;

import edu.northeastern.cs5500.starterbot.service.alphavantage.AlphaVantageBalanceSheet;

import java.util.List;

import edu.northeastern.cs5500.starterbot.exception.rest.RestException;
import edu.northeastern.cs5500.starterbot.service.alphavantage.AlphaVantageException;

public interface BalanceSheetService extends Service {
    List<AlphaVantageBalanceSheet> getBalanceSheet(String symbol) throws RestException, AlphaVantageException;
}
