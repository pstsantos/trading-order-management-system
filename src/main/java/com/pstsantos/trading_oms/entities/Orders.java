package com.pstsantos.trading_oms.entities;

import com.pstsantos.trading_oms.enums.OrderSide;
import com.pstsantos.trading_oms.enums.OrderStatus;
import com.pstsantos.trading_oms.enums.OrderType;

public class Orders {

    private Long id;
    private String symbol;
    private int quantity;
    private double price;
    private OrderType type;
    private OrderSide side;
    private String status;

    private Orders() {}

    public Long getId() { return id; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public OrderType getType() { return type; }
    public OrderSide getSide() { return side; }
    public String getStatus() { return status; }

    public static class Builder {
        private String symbol;
        private int quantity;
        private double price;
        private OrderType type;
        private OrderSide side;

        public Builder symbol(String symbol) {
            this.symbol = symbol;
            return this;
        }
        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }
        public Builder price(double price) {
            this.price = price;
            return this;
        }
        public Builder type(OrderType type) {
            this.type = type;
            return this;
        }
        public Builder side(OrderSide side) {
            this.side = side;
            return this;
        }

        public Orders build() {
            if (symbol == null || symbol.isEmpty())
                throw new IllegalArgumentException("Symbol is required");
            if (quantity <= 0)
                throw new IllegalArgumentException("Quantity must be greater than 0");
            if (type == null)
                throw new IllegalArgumentException("Order type is required");
            if (side == null)
                throw new IllegalArgumentException("Order side is required");

            Orders order = new Orders();
            order.id = System.currentTimeMillis();
            order.symbol = this.symbol;
            order.quantity = this.quantity;
            order.price = this.price;
            order.type = this.type;
            order.side = this.side;
            order.status = OrderStatus.PENDING;
            return order;
        }
    }
}