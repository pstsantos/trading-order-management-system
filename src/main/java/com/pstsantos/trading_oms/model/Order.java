package com.pstsantos.trading_oms.model;

import java.time.Instant;

/**
 * Abstract base class for all order types in the Factory pattern.
 *
 * Role: "Product" — defines the contract that every concrete order must fulfill.
 * The OrderFactory produces instances of these subclasses without the caller
 * needing to know how each type is constructed.
 *
 * Subclasses: MarketOrder, LimitOrder, StopOrder
 */
public abstract class Order {

    private final Long id;
    private final String symbol;
    private final int quantity;
    private final String side;   // "BUY" or "SELL" — matches OrderSide constants
    private String status;       // mutable: PENDING → FILLED / CANCELLED
    private final Instant createdAt;

    /**
     * Protected constructor — only subclasses (and by extension the factory) can
     * instantiate an Order. Callers must go through OrderFactory.
     */
    protected Order(String symbol, int quantity, String side) {
        if (symbol == null || symbol.isBlank())
            throw new IllegalArgumentException("Symbol is required");
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive");
        if (side == null || side.isBlank())
            throw new IllegalArgumentException("Side is required");

        this.id        = System.currentTimeMillis();
        this.symbol    = symbol;
        this.quantity  = quantity;
        this.side      = side;
        this.status    = "PENDING";
        this.createdAt = Instant.now();
    }

    /**
     * Each subclass must declare its order type string (e.g. "MARKET", "LIMIT").
     * The factory reads this to determine which concrete class to create.
     */
    public abstract String getOrderType();

    /**
     * Each subclass enforces its own field-level business rules.
     * Called by the factory after construction to guarantee a valid order.
     * Throws IllegalArgumentException if the order is not valid.
     */
    public abstract void validate();


    // Getters


    public Long    getId()        { return id; }
    public String  getSymbol()    { return symbol; }
    public int     getQuantity()  { return quantity; }
    public String  getSide()      { return side; }
    public String  getStatus()    { return status; }
    public Instant getCreatedAt() { return createdAt; }

    /** Called by OrderService when a match occurs or an order is cancelled. */
    public void setStatus(String status) { this.status = status; }
}
