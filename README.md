# Trading Order Management System

A Java-based Order Management System (OMS) simulating core stock trading
functionality. Built with Spring Boot and OOP Design Patterns.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Data JPA / H2 (in-memory database)

## Architecture

    OrderController → OrderService → LoggingOrderFactory (Decorator)
                                           ↓
                                   OrderFactoryImpl (Factory)
                                           ↓
                                   Order subclasses (Product)

    OrderService → MatchingStrategy (Strategy)
                → TradeEventListener[] (Observer)
                → MarketDataService (Singleton)

    OrdersDirector → Orders.Builder (Builder)

## Design Patterns

### 1. Factory Pattern — Order Creation

`OrderFactory` interface is implemented by `OrderFactoryImpl`, which creates
the correct order subclass (`MarketOrder`, `LimitOrder`, `StopOrder`) based
on a type string. Callers never reference concrete classes directly.

### 2. Strategy Pattern — Order Matching

`MatchingStrategy` interface allows the matching algorithm to be swapped at
runtime without changing any other class.

Implementations:
- `FifoMatchingStrategy` — match in strict arrival order
- `PriceTimeMatchingStrategy` — match by best price first, then arrival time

### 3. Builder Pattern — Order Construction

`Orders.Builder` constructs order objects step by step with validation.
`OrdersDirector` encapsulates named construction recipes like `buildMarketBuy()`
and `buildLimitSell()` so callers never chain the builder manually.

### 4. Observer Pattern — Trade Event Notifications

`TradeEventListener` interface is implemented by two concrete listeners:
- `TradeLogListener` — prints filled order details to the console
- `TradeHistoryListener` — maintains an in-memory list of all filled orders

Both are automatically notified by `OrderService` whenever `matchOrders()` fills an order.

### 5. Singleton Pattern — Market Data Service

`MarketDataService` is a Spring-managed singleton that holds a single shared
price cache across the entire application. `getInstance()` enforces that only
one instance is ever used regardless of how many classes request it.

### 6. Decorator Pattern — Logging Order Factory

`LoggingOrderFactory` wraps `OrderFactoryImpl` and adds console logging before
and after every order creation without modifying the original factory code.
Marked `@Primary` so Spring injects it automatically wherever `OrderFactory` is needed.

## API Endpoints

### Orders

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/orders` | Place a new order (MARKET, LIMIT, or STOP) |
| `GET` | `/orders` | List all orders |
| `GET` | `/orders/pending` | List pending orders only |
| `POST` | `/orders/match` | Run the active matching strategy |
| `GET` | `/orders/strategy` | Get the active matching strategy |
| `PUT` | `/orders/strategy` | Swap the matching strategy at runtime |

### Market Data

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/market/prices` | Get all cached symbol prices |
| `GET` | `/market/price/{symbol}` | Get price for a specific symbol |
| `PUT` | `/market/price/{symbol}` | Update price for a specific symbol |

## Example Requests

### Place a Market Order

```json
POST /orders
{
  "type":     "MARKET",
  "symbol":   "AAPL",
  "quantity": 100,
  "side":     "BUY"
}
```

### Place a Limit Order

```json
POST /orders
{
  "type":     "LIMIT",
  "symbol":   "AAPL",
  "quantity": 100,
  "side":     "BUY",
  "price":    185.50
}
```

### Switch Matching Strategy

```json
PUT /orders/strategy
{
  "strategy": "PRICE_TIME"
}
```

### Update a Market Price

```json
PUT /market/price/AAPL
{
  "price": 192.75
}
```

## How to Run

```
mvn spring-boot:run
```

Requires Java 21. No external database setup needed — uses H2 in-memory database.

## Final Submission Plan

The demo will walk through:
1. Placing a market order and a limit order via the API
2. Running the matching engine and showing filled orders in the console log (Observer + Decorator output visible)
3. Switching the matching strategy at runtime from FIFO to PRICE_TIME
4. Querying live market prices from the MarketDataService

## Known Issues

- Docker not included. Run locally with `mvn spring-boot:run` and Java 21.
- `OrdersDirector` and `Orders.Builder` operate independently of the main order flow — they demonstrate the Builder pattern as a standalone construction mechanism.
- H2 database resets on every restart — order book is in-memory only.
- Market prices are seeded with static values; no live external feed is connected.