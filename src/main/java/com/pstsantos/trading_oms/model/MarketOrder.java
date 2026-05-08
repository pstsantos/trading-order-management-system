package com.pstsantos.trading_oms.model;

/**
 * A Market Order executes immediately at the best available price.
 *
 * Role: Concrete "Product" in the Factory pattern.
 * Key rule: price is irrelevant — the trader accepts whatever the market offers.
 * The factory creates this when orderType == "MARKET".
 */
public class MarketOrder extends Order {

    /**
     * Constructor called only by OrderFactoryImpl.
     * No price field: market orders are intentionally price-agnostic.
     */
    public MarketOrder(String symbol, int quantity, String side) {
        super(symbol, quantity, side);
    }

    @Override
    public String getOrderType() {
        return "MARKET";
    }

    /**
     * Market orders have no price constraint, so validation only checks
     * that the base fields (symbol, quantity, side) are present — which
     * the parent constructor already enforces. Nothing extra to validate here.
     */
    @Override
    public void validate() {
        // Base class handles symbol, quantity, and side validation.
        // A market order has no additional constraints.
    }

    @Override
    public String toString() {
        return String.format("MarketOrder[id=%d, symbol=%s, qty=%d, side=%s, status=%s]",
                getId(), getSymbol(), getQuantity(), getSide(), getStatus());
    }
}
