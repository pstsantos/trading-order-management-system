package com.pstsantos.trading_oms.model;

/**
 * A Stop Order becomes active only when the market reaches the stop price.
 * Once triggered, it executes like a market order at the best available price.
 *
 * Role: Concrete "Product" in the Factory pattern.
 * Key rule: stopPrice must be positive — it defines the trigger threshold.
 * The factory creates this when orderType == "STOP".
 */
public class StopOrder extends Order {

    /** The price level that triggers this order when the market reaches it. */
    private final double stopPrice;

    /**
     * Constructor called only by OrderFactoryImpl.
     *
     * @param symbol    ticker symbol (e.g. "TSLA")
     * @param quantity  number of shares
     * @param side      "BUY" or "SELL" — use OrderSide constants
     * @param stopPrice trigger price; must be positive
     */
    public StopOrder(String symbol, int quantity, String side, double stopPrice) {
        super(symbol, quantity, side);
        this.stopPrice = stopPrice;
    }

    @Override
    public String getOrderType() {
        return "STOP";
    }

    /**
     * Stop orders require a positive stop price.
     * Called by OrderFactoryImpl immediately after construction.
     */
    @Override
    public void validate() {
        if (stopPrice <= 0) {
            throw new IllegalArgumentException(
                "StopOrder requires a positive stopPrice, got: " + stopPrice);
        }
    }

    public double getStopPrice() { return stopPrice; }

    @Override
    public String toString() {
        return String.format("StopOrder[id=%d, symbol=%s, qty=%d, side=%s, stopPrice=%.2f, status=%s]",
                getId(), getSymbol(), getQuantity(), getSide(), stopPrice, getStatus());
    }
}
