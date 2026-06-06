package com.pstsantos.trading_oms.observer;

import com.pstsantos.trading_oms.model.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TradeHistoryListener implements TradeEventListener {

    private final List<Order> history = new ArrayList<>();

    @Override
    public void onOrderFilled(Order order) {
        history.add(order);
    }

    public List<Order> getHistory() {
        return Collections.unmodifiableList(history);
    }
}