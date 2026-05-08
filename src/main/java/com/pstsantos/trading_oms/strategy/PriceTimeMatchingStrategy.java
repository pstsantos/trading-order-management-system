package com.pstsantos.trading_oms.strategy;

import com.pstsantos.trading_oms.model.LimitOrder;
import com.pstsantos.trading_oms.model.MarketOrder;
import com.pstsantos.trading_oms.model.Order;
import com.pstsantos.trading_oms.model.StopOrder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Price-Time Priority matching strategy.
 *
 * Role: "Concrete Strategy" in the Strategy pattern.
 * Algorithm:
 *   - BUYs are sorted by highest price first (most aggressive buyer gets priority).
 *     Ties are broken by earliest arrival time (createdAt).
 *   - SELLs are sorted by lowest price first (cheapest seller gets priority).
 *     Ties are broken by earliest arrival time.
 *   - A BUY matches a SELL when buyPrice >= sellPrice (prices cross).
 *   - MarketOrders are treated as infinitely aggressive:
 *       Market BUY  → effective price = Double.MAX_VALUE (matches any sell)
 *       Market SELL → effective price = 0.0             (matches any buy)
 *
 * This is the standard algorithm used by most real equity exchanges (e.g. NYSE, NASDAQ).
 */
@Component("priceTimeStrategy")
public class PriceTimeMatchingStrategy implements MatchingStrategy {

    @Override
    public String getStrategyName() {
        return "PRICE_TIME";
    }

    /**
     * Matching logic:
     *  1. Separate pending BUYs and SELLs for each symbol.
     *  2. Sort each side by price priority, then by arrival time (time priority).
     *  3. Walk the sorted BUY list; for each BUY find the best matching SELL
     *     where buyPrice >= sellPrice.
     *  4. Mark matched pairs FILLED and add to the result list.
     */
    @Override
    public List<Order> match(List<Order> orderBook) {
        List<Order> matched = new ArrayList<>();

        // Sort BUYs: highest effective price first, then earliest time
        List<Order> sortedBuys = orderBook.stream()
                .filter(o -> "PENDING".equals(o.getStatus()) && "BUY".equals(o.getSide()))
                .sorted(Comparator
                        .comparingDouble((Order o) -> effectivePrice(o, "BUY")).reversed()
                        .thenComparing(Order::getCreatedAt))
                .toList();

        // Sort SELLs: lowest effective price first, then earliest time
        List<Order> availableSells = new ArrayList<>(
                orderBook.stream()
                        .filter(o -> "PENDING".equals(o.getStatus()) && "SELL".equals(o.getSide()))
                        .sorted(Comparator
                                .comparingDouble((Order o) -> effectivePrice(o, "SELL"))
                                .thenComparing(Order::getCreatedAt))
                        .toList()
        );

        for (Order buy : sortedBuys) {
            double buyPrice = effectivePrice(buy, "BUY");

            // Find the best (lowest-priced) sell on the same symbol that the buy can afford
            Order matchedSell = availableSells.stream()
                    .filter(sell -> sell.getSymbol().equals(buy.getSymbol())
                            && buyPrice >= effectivePrice(sell, "SELL"))
                    .findFirst()
                    .orElse(null);

            if (matchedSell != null) {
                buy.setStatus("FILLED");
                matchedSell.setStatus("FILLED");

                matched.add(buy);
                matched.add(matchedSell);

                availableSells.remove(matchedSell);
            }
        }

        return matched;
    }

    /**
     * Returns the price used for priority sorting and match eligibility.
     *
     * - LimitOrder: the declared limit price
     * - StopOrder:  the stop trigger price
     * - MarketOrder: MAX_VALUE for BUY (always highest priority buyer),
     *                0.0 for SELL (always highest priority seller)
     */
    private double effectivePrice(Order order, String side) {
        if (order instanceof LimitOrder limit) {
            return limit.getPrice();
        }
        if (order instanceof StopOrder stop) {
            return stop.getStopPrice();
        }
        // MarketOrder — aggressive on both sides
        return "BUY".equals(side) ? Double.MAX_VALUE : 0.0;
    }
}
