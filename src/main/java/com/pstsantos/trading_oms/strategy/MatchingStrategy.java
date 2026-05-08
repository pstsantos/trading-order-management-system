package com.pstsantos.trading_oms.strategy;

import com.pstsantos.trading_oms.model.Order;

import java.util.List;

/**
 * Strategy interface for order matching algorithms.
 *
 * Role: "Strategy" in the Strategy pattern.
 * Defines the contract for how a list of pending orders should be matched.
 * OrderService holds a reference to this interface — not to any concrete
 * implementation — which makes the algorithm swappable at runtime via a
 * simple setter call.
 *
 * Implementations:
 *   - FifoMatchingStrategy     — match in strict arrival order
 *   - PriceTimeMatchingStrategy — match by best price first, then by arrival time
 *
 * To add a new algorithm, implement this interface and inject it into
 * OrderService. No other class needs to change.
 */
public interface MatchingStrategy {

    /**
     * Attempts to match orders in the provided order book.
     *
     * Implementations should:
     *   - Pair BUY and SELL orders according to their algorithm's rules
     *   - Call setStatus("FILLED") on each successfully matched order
     *   - Leave unmatched orders with status "PENDING"
     *
     * @param orderBook the current list of all pending orders
     * @return the subset of orders that were matched (status set to FILLED)
     */
    List<Order> match(List<Order> orderBook);

    /**
     * A short human-readable name for this strategy.
     * Used by OrderService to report which algorithm is currently active.
     * Example return values: "FIFO", "PRICE_TIME"
     */
    String getStrategyName();
}
