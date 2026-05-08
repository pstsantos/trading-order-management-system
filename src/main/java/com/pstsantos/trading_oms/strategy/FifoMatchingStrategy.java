package com.pstsantos.trading_oms.strategy;

import com.pstsantos.trading_oms.model.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * First-In-First-Out matching strategy.
 *
 * Role: "Concrete Strategy" in the Strategy pattern.
 * Algorithm: for each symbol, pair the oldest pending BUY with the oldest
 * pending SELL, in strict arrival order. Price is not considered — whoever
 * submitted first gets matched first.
 *
 * This mirrors how simple exchange queues work before price-priority is added.
 * It is the baseline algorithm; PriceTimeMatchingStrategy builds on top of it.
 */
@Component("fifoStrategy")
public class FifoMatchingStrategy implements MatchingStrategy {

    @Override
    public String getStrategyName() {
        return "FIFO";
    }

    /**
     * Matching logic:
     *  1. Separate the order book into pending BUYs and pending SELLs per symbol.
     *  2. Walk both lists in insertion order (FIFO).
     *  3. Pair each BUY with the next available SELL for the same symbol.
     *  4. Mark matched pairs as FILLED and add them to the result list.
     *
     * Orders that have no counterpart remain PENDING and stay in the book.
     */
    @Override
    public List<Order> match(List<Order> orderBook) {
        List<Order> matched = new ArrayList<>();

        // Collect pending BUYs and SELLs (preserves insertion order)
        List<Order> pendingBuys  = orderBook.stream()
                .filter(o -> "PENDING".equals(o.getStatus()) && "BUY".equals(o.getSide()))
                .toList();

        List<Order> pendingSells = orderBook.stream()
                .filter(o -> "PENDING".equals(o.getStatus()) && "SELL".equals(o.getSide()))
                .toList();

        // Track which sell orders have already been matched this round
        List<Order> availableSells = new ArrayList<>(pendingSells);

        for (Order buy : pendingBuys) {
            // Find the first available SELL for the same symbol
            Order matchedSell = availableSells.stream()
                    .filter(sell -> sell.getSymbol().equals(buy.getSymbol()))
                    .findFirst()
                    .orElse(null);

            if (matchedSell != null) {
                // Mark both sides as filled
                buy.setStatus("FILLED");
                matchedSell.setStatus("FILLED");

                matched.add(buy);
                matched.add(matchedSell);

                // Remove so it cannot be matched again
                availableSells.remove(matchedSell);
            }
        }

        return matched;
    }
}
