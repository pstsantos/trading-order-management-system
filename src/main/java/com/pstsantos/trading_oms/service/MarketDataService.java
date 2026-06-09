package com.pstsantos.trading_oms.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class MarketDataService {

    private static MarketDataService instance;

    private final Map<String, Double> priceCache = new HashMap<>();
    private final RestTemplate restTemplate;

    public MarketDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        priceCache.put("AAPL", 189.50);
        priceCache.put("MSFT", 415.20);
        priceCache.put("TSLA", 177.80);
        priceCache.put("GOOGL", 175.30);
        priceCache.put("AMZN", 196.40);
        priceCache.put("META", 512.60);
        priceCache.put("NVDA", 875.40);
        priceCache.put("JPM", 198.20);
        priceCache.put("GS", 462.80);
        priceCache.put("NFLX", 628.90);
        priceCache.put("AMD", 162.40);
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

    public double fetchLivePrice(String symbol) {
        try {
            String url = "https://query1.finance.yahoo.com/v8/finance/chart/" + symbol.toUpperCase();
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0");
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map chart = (Map) response.getBody().get("chart");
            Map result = (Map) ((java.util.List) chart.get("result")).get(0);
            Map meta = (Map) result.get("meta");
            double price = ((Number) meta.get("regularMarketPrice")).doubleValue();
            priceCache.put(symbol.toUpperCase(), price);
            return price;
        } catch (Exception e) {
            return priceCache.getOrDefault(symbol.toUpperCase(), 0.0);
        }
    }
}