package com.pstsantos.trading_oms.observer;

import com.pstsantos.trading_oms.model.Order;

public interface TradeEventListener {
    void onOrderFilled(Order order);
}