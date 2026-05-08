package com.pstsantos.trading_oms.service;

import com.pstsantos.trading_oms.factory.OrderFactory;
import com.pstsantos.trading_oms.model.Order;
import com.pstsantos.trading_oms.strategy.MatchingStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Central service that wires the Factory and Strategy patterns together.
 *
 * Responsibilities:
 *   1. Order creation  — delegates to OrderFactory (Factory pattern)
 *   2. Order matching  — delegates to the active MatchingStrategy (Strategy pattern)
 *   3. Order book      — maintains the in-memory list of all orders
 *
 * The matching strategy is swappable at runtime via setStrategy().
 * OrderController calls this method when the client sends a strategy-switch request,
 * so no restart is needed to change the matching algorithm.
 */
@Service
public class OrderService {

    private final OrderFactory factory;

    // Both strategies are injected so we can switch between them at runtime
    private final MatchingStrategy fifoStrategy;
    private final MatchingStrategy priceTimeStrategy;

    // The currently active strategy — starts as FIFO
    private MatchingStrategy activeStrategy;

    // In-memory order book — holds all orders regardless of status
    private final List<Order> orderBook = new ArrayList<>();

    public OrderService(
            OrderFactory factory,
            @Qualifier("fifoStrategy")      MatchingStrategy fifoStrategy,
            @Qualifier("priceTimeStrategy") MatchingStrategy priceTimeStrategy
    ) {
        this.factory           = factory;
        this.fifoStrategy      = fifoStrategy;
        this.priceTimeStrategy = priceTimeStrategy;
        this.activeStrategy    = fifoStrategy; // default
    }

    // -------------------------------------------------------------------------
    // Factory usage — order creation
    // -------------------------------------------------------------------------

    /**
     * Creates a validated order via the factory and adds it to the order book.
     * The caller never touches a concrete order subclass.
     *
     * @return the newly created Order
     * @throws IllegalArgumentException if type is unknown or validation fails
     */
    public Order placeOrder(String type, String symbol, int quantity, String side, double price) {
        // Factory pattern in action: returns MarketOrder, LimitOrder, or StopOrder
        // depending on `type` — caller doesn't know which
        Order order = factory.createOrder(type, symbol, quantity, side, price);
        orderBook.add(order);
        return order;
    }

    // -------------------------------------------------------------------------
    // Strategy usage — order matching
    // -------------------------------------------------------------------------

    /**
     * Runs the active matching strategy against the current order book.
     * Returns only the orders that were matched (status set to FILLED).
     *
     * Strategy pattern in action: the same call here produces different results
     * depending on which strategy is currently active — without any if/else.
     */
    public List<Order> matchOrders() {
        return activeStrategy.match(orderBook);
    }

    /**
     * Swaps the active matching strategy at runtime.
     * Accepts "FIFO" or "PRICE_TIME" (case-insensitive).
     *
     * @throws IllegalArgumentException if the name is not recognised
     */
    public void setStrategy(String strategyName) {
        activeStrategy = switch (strategyName.toUpperCase()) {
            case "FIFO"       -> fifoStrategy;
            case "PRICE_TIME" -> priceTimeStrategy;
            default -> throw new IllegalArgumentException(
                "Unknown strategy: '" + strategyName + "'. Valid values: FIFO, PRICE_TIME");
        };
    }

    /** Returns the name of the currently active strategy (e.g. "FIFO"). */
    public String getActiveStrategyName() {
        return activeStrategy.getStrategyName();
    }


    // Order book queries

    /** Returns all orders (any status) in the order book. */
    public List<Order> getAllOrders() {
        return Collections.unmodifiableList(orderBook);
    }

    /** Returns only orders still waiting to be matched. */
    public List<Order> getPendingOrders() {
        return orderBook.stream()
                .filter(o -> "PENDING".equals(o.getStatus()))
                .toList();
    }
}
