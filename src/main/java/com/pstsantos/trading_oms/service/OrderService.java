package com.pstsantos.trading_oms.service;

import com.pstsantos.trading_oms.factory.OrderFactory;
import com.pstsantos.trading_oms.model.Order;
import com.pstsantos.trading_oms.observer.TradeEventListener;
import com.pstsantos.trading_oms.strategy.MatchingStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OrderService {

    private final OrderFactory factory;
    private final MatchingStrategy fifoStrategy;
    private final MatchingStrategy priceTimeStrategy;
    private final List<TradeEventListener> listeners;
    private MatchingStrategy activeStrategy;
    private final List<Order> orderBook = new ArrayList<>();

    public OrderService(
            OrderFactory factory,
            @Qualifier("fifoStrategy")      MatchingStrategy fifoStrategy,
            @Qualifier("priceTimeStrategy") MatchingStrategy priceTimeStrategy,
            List<TradeEventListener> listeners
    ) {
        this.factory           = factory;
        this.fifoStrategy      = fifoStrategy;
        this.priceTimeStrategy = priceTimeStrategy;
        this.listeners         = listeners;
        this.activeStrategy    = fifoStrategy;
    }

    public Order placeOrder(String type, String symbol, int quantity, String side, double price) {
        Order order = factory.createOrder(type, symbol, quantity, side, price);
        orderBook.add(order);
        return order;
    }

    public List<Order> matchOrders() {
        List<Order> matched = activeStrategy.match(orderBook);
        matched.forEach(order -> listeners.forEach(l -> l.onOrderFilled(order)));
        return matched;
    }

    public void setStrategy(String strategyName) {
        activeStrategy = switch (strategyName.toUpperCase()) {
            case "FIFO"       -> fifoStrategy;
            case "PRICE_TIME" -> priceTimeStrategy;
            default -> throw new IllegalArgumentException(
                    "Unknown strategy: '" + strategyName + "'. Valid values: FIFO, PRICE_TIME");
        };
    }

    public String getActiveStrategyName() {
        return activeStrategy.getStrategyName();
    }

    public List<Order> getAllOrders() {
        return Collections.unmodifiableList(orderBook);
    }

    public List<Order> getPendingOrders() {
        return orderBook.stream()
                .filter(o -> "PENDING".equals(o.getStatus()))
                .toList();
    }
}