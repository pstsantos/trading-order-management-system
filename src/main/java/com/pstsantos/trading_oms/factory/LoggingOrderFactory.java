package com.pstsantos.trading_oms.factory;

import com.pstsantos.trading_oms.model.Order;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class LoggingOrderFactory implements OrderFactory {

    private final OrderFactory wrapped;

    public LoggingOrderFactory(OrderFactoryImpl orderFactoryImpl) {
        this.wrapped = orderFactoryImpl;
    }

    @Override
    public Order createOrder(String type, String symbol, int quantity, String side, double price) {
        System.out.println("[FACTORY] Creating order → type=" + type + " symbol=" + symbol + " qty=" + quantity + " side=" + side + " price=" + price);
        Order order = wrapped.createOrder(type, symbol, quantity, side, price);
        System.out.println("[FACTORY] Created → " + order);
        return order;
    }
}