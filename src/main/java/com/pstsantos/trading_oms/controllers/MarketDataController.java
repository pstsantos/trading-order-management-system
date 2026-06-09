package com.pstsantos.trading_oms.controllers;

import com.pstsantos.trading_oms.service.MarketDataService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/market")
public class MarketDataController {

    private final MarketDataService marketDataService;

    public MarketDataController(MarketDataService marketDataService) {
        this.marketDataService = MarketDataService.getInstance(marketDataService);
    }

    @GetMapping("/prices")
    public Map<String, Double> getAllPrices() {
        return marketDataService.getAllPrices();
    }

    @GetMapping("/price/{symbol}")
    public Map<String, Object> getPrice(@PathVariable String symbol) {
        double price = marketDataService.getPrice(symbol);
        return Map.of("symbol", symbol.toUpperCase(), "price", price);
    }

    @GetMapping("/live/{symbol}")
    public Map<String, Object> getLivePrice(@PathVariable String symbol) {
        double price = marketDataService.fetchLivePrice(symbol);
        return Map.of("symbol", symbol.toUpperCase(), "price", price, "live", true);
    }

    @PutMapping("/price/{symbol}")
    public Map<String, Object> updatePrice(@PathVariable String symbol, @RequestBody Map<String, Double> body) {
        double price = body.get("price");
        marketDataService.updatePrice(symbol, price);
        return Map.of("symbol", symbol.toUpperCase(), "price", price);
    }
}