package com.pstsantos.trading_oms.factory;

import com.pstsantos.trading_oms.model.LimitOrder;
import com.pstsantos.trading_oms.model.MarketOrder;
import com.pstsantos.trading_oms.model.Order;
import com.pstsantos.trading_oms.model.StopOrder;
import org.springframework.stereotype.Component;

/**
 * Concrete implementation of the OrderFactory.
 *
 * Role: "Concrete Creator" in the Factory pattern.
 * Decides which Order subclass to instantiate based on the type string,
 * then immediately calls validate() to enforce that subclass's business rules.
 *
 * Adding a new order type only requires:
 *   1. A new Order subclass in model/
 *   2. A new case here — nothing else changes.
 */
@Component
public class OrderFactoryImpl implements OrderFactory {

    /**
     * Constructs and validates the correct Order subclass for the given type.
     *
     * Flow:
     *   1. Switch on type string to pick the subclass
     *   2. Call validate() — if the order is invalid, an exception stops it here
     *   3. Return the validated order to the caller (OrderService)
     */
    @Override
    public Order createOrder(String type, String symbol, int quantity, String side, double price) {
        if (type == null) {
            throw new IllegalArgumentException("Order type must not be null");
        }

        Order order = switch (type.toUpperCase()) {
            case "MARKET" -> new MarketOrder(symbol, quantity, side);
            case "LIMIT"  -> new LimitOrder(symbol, quantity, side, price);
            case "STOP"   -> new StopOrder(symbol, quantity, side, price);
            default -> throw new IllegalArgumentException(
                "Unknown order type: '" + type + "'. Valid types: MARKET, LIMIT, STOP");
        };

        // Validate after construction — each subclass enforces its own rules.
        // An invalid order never leaves the factory.
        order.validate();

        return order;
    }
}
