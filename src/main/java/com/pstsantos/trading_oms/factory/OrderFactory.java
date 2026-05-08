package com.pstsantos.trading_oms.factory;

import com.pstsantos.trading_oms.model.Order;

/**
 * Factory interface for creating Order objects.
 *
 * Role: "Creator" in the Factory pattern.
 * The caller provides what it knows (type, symbol, quantity, side, price) and
 * gets back a fully constructed, validated Order — without ever referencing
 * MarketOrder, LimitOrder, or StopOrder directly.
 *
 * This decoupling means:
 *   - New order types can be added by implementing this interface and updating
 *     OrderFactoryImpl, with zero changes to OrderService or OrderController.
 *   - Tests can swap in a mock factory without touching business logic.
 *
 * Implementation: OrderFactoryImpl
 */
public interface OrderFactory {

    /**
     * Creates and validates an Order of the requested type.
     *
     * @param type     "MARKET", "LIMIT", or "STOP" — use OrderType constants
     * @param symbol   ticker symbol (e.g. "AAPL")
     * @param quantity number of shares; must be positive
     * @param side     "BUY" or "SELL" — use OrderSide constants
     * @param price    limit price or stop price; ignored for MARKET orders
     * @return a validated Order subclass instance
     * @throws IllegalArgumentException if type is unknown or validation fails
     */
    Order createOrder(String type, String symbol, int quantity, String side, double price);
}
