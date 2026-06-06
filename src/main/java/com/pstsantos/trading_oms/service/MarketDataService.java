package com.pstsantos.trading_oms.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MarketDataService {

    private static MarketDataService instance;

    private final Map<String, Double> priceCache = new HashMap<>();

    public MarketDataService() {
        priceCache.put("AAPL", 189.50);
        priceCache.put("MSFT", 415.20);
        priceCache.put("TSLA", 177.80);
        priceCache.put("GOOGL", 175.30);
        priceCache.put("AMZN", 196.40);
    }

    public static MarketDataService getInstance(MarketDataService service) {
        if (instance == null) {
            instance = service;
        }
        return instance;
    }

    public double getPrice(String symbol) {
        return priceCache.getOrDefault(symbol.toUpperCase(), 0.0);
    }

    public Map<String, Double> getAllPrices() {
        return Map.copyOf(priceCache);
    }

    public void updatePrice(String symbol, double price) {
        priceCache.put(symbol.toUpperCase(), price);
    }
}