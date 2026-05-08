package com.pstsantos.trading_oms.model;

/**
 * A Limit Order executes only at the specified price or better.
 *
 * Role: Concrete "Product" in the Factory pattern.
 * Key rule: price must be set and positive — the trader will not accept a worse price.
 * The factory creates this when orderType == "LIMIT".
 */
public class LimitOrder extends Order {

    /** The maximum price the buyer will pay (or minimum the seller will accept). */
    private final double price;

    /**
     * Constructor called only by OrderFactoryImpl.
     *
     * @param symbol   ticker symbol (e.g. "AAPL")
     * @param quantity number of shares
     * @param side     "BUY" or "SELL" — use OrderSide constants
     * @param price    limit price; must be positive
     */
    public LimitOrder(String symbol, int quantity, String side, double price) {
        super(symbol, quantity, side);
        this.price = price;
    }

    @Override
    public String getOrderType() {
        return "LIMIT";
    }

    /**
     * Limit orders require a positive price.
     * Called by OrderFactoryImpl immediately after construction.
     */
    @Override
    public void validate() {
        if (price <= 0) {
            throw new IllegalArgumentException(
                "LimitOrder requires a positive price, got: " + price);
        }
    }

    public double getPrice() { return price; }

    @Override
    public String toString() {
        return String.format("LimitOrder[id=%d, symbol=%s, qty=%d, side=%s, price=%.2f, status=%s]",
                getId(), getSymbol(), getQuantity(), getSide(), price, getStatus());
    }
}
