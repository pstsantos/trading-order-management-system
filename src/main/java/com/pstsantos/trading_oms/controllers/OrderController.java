package com.pstsantos.trading_oms.controllers;

import com.pstsantos.trading_oms.model.Order;
import com.pstsantos.trading_oms.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller that exposes the order book and matching engine.
 *
 * All business logic lives in OrderService — this class only handles HTTP
 * concerns (request parsing, response codes, error formatting).
 *
 * Base path: /orders
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // -------------------------------------------------------------------------
    // Order placement — Factory pattern is triggered here
    // -------------------------------------------------------------------------

    /**
     * Place a new order.
     *
     * POST /orders
     * Body (JSON):
     * {
     *   "type":     "MARKET" | "LIMIT" | "STOP",
     *   "symbol":   "AAPL",
     *   "quantity": 100,
     *   "side":     "BUY" | "SELL",
     *   "price":    185.50          // required for LIMIT and STOP; ignored for MARKET
     * }
     *
     * Returns 200 with the created Order, or 400 with an error message.
     */
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> body) {
        try {
            String type     = (String)  body.get("type");
            String symbol   = (String)  body.get("symbol");
            int    quantity = (Integer) body.get("quantity");
            String side     = (String)  body.get("side");
            double price    = body.containsKey("price")
                    ? ((Number) body.get("price")).doubleValue()
                    : 0.0;

            Order order = orderService.placeOrder(type, symbol, quantity, side, price);
            return ResponseEntity.ok(order);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // -------------------------------------------------------------------------
    // Order book queries
    // -------------------------------------------------------------------------

    /**
     * List all orders (any status).
     * GET /orders
     */
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    /**
     * List only pending orders.
     * GET /orders/pending
     */
    @GetMapping("/pending")
    public List<Order> getPendingOrders() {
        return orderService.getPendingOrders();
    }

    // -------------------------------------------------------------------------
    // Matching engine — Strategy pattern is triggered here
    // -------------------------------------------------------------------------

    /**
     * Run the active matching strategy against the order book.
     * Returns only the orders that were matched in this run (status = FILLED).
     *
     * POST /orders/match
     */
    @PostMapping("/match")
    public ResponseEntity<?> matchOrders() {
        List<Order> matched = orderService.matchOrders();
        return ResponseEntity.ok(Map.of(
                "strategy", orderService.getActiveStrategyName(),
                "matchedCount", matched.size(),
                "matchedOrders", matched
        ));
    }

    // -------------------------------------------------------------------------
    // Strategy management — swap algorithm at runtime
    // -------------------------------------------------------------------------

    /**
     * See which matching strategy is currently active.
     * GET /orders/strategy
     */
    @GetMapping("/strategy")
    public Map<String, String> getStrategy() {
        return Map.of("activeStrategy", orderService.getActiveStrategyName());
    }

    /**
     * Swap the matching strategy at runtime — no restart needed.
     *
     * PUT /orders/strategy
     * Body (JSON):
     * { "strategy": "FIFO" }       // or "PRICE_TIME"
     */
    @PutMapping("/strategy")
    public ResponseEntity<?> setStrategy(@RequestBody Map<String, String> body) {
        try {
            String name = body.get("strategy");
            orderService.setStrategy(name);
            return ResponseEntity.ok(Map.of(
                    "message",  "Strategy switched successfully",
                    "strategy", orderService.getActiveStrategyName()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
