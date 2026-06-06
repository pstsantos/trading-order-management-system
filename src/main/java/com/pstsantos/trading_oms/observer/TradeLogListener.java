package com.pstsantos.trading_oms.observer;

import com.pstsantos.trading_oms.model.Order;
import org.springframework.stereotype.Component;

@Component
public class TradeLogListener implements TradeEventListener {

    @Override
    public void onOrderFilled(Order order) {
        System.out.println("[TRADE] " + order.getOrderType() + " | "
                + order.getSide() + " | "
                + order.getSymbol() + " | qty="
                + order.getQuantity() + " | status="
                + order.getStatus());
    }
}