package com.pstsantos.trading_oms.entities;

import com.pstsantos.trading_oms.enums.OrderSide;
import com.pstsantos.trading_oms.enums.OrderType;

public class OrdersDirector {

    private final Orders.Builder builder;

    public OrdersDirector(Orders.Builder builder) {
        this.builder = builder;
    }

    public Orders buildMarketBuy(String symbol, int quantity) {
        return builder
                .symbol(symbol)
                .quantity(quantity)
                .type(OrderType.MARKET)
                .side(OrderSide.BUY)
                .price(0.0)
                .build();
    }

    public Orders buildMarketSell(String symbol, int quantity) {
        return builder
                .symbol(symbol)
                .quantity(quantity)
                .type(OrderType.MARKET)
                .side(OrderSide.SELL)
                .price(0.0)
                .build();
    }

    public Orders buildLimitBuy(String symbol, int quantity, double price) {
        return builder
                .symbol(symbol)
                .quantity(quantity)
                .type(OrderType.LIMIT)
                .side(OrderSide.BUY)
                .price(price)
                .build();
    }

    public Orders buildLimitSell(String symbol, int quantity, double price) {
        return builder
                .symbol(symbol)
                .quantity(quantity)
                .type(OrderType.LIMIT)
                .side(OrderSide.SELL)
                .price(price)
                .build();
    }

    public Orders buildStopOrder(String symbol, int quantity, OrderSide side, double stopPrice) {
        return builder
                .symbol(symbol)
                .quantity(quantity)
                .type(OrderType.STOP)
                .side(side)
                .price(stopPrice)
                .build();
    }
}