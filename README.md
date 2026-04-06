# Trading Platform

Simple event-driven trading platform built with Spring Boot, PostgreSQL, and Kafka.

## Modules

- `common`
  Shared Kafka event classes.
- `user-service`
  Owns users and persisted holdings.
- `order-service`
  Owns order placement, execution, cached user state, and real-time price lookup.

## Architecture

The services communicate with Kafka using shared events from `common`.

### Topics

- `user-topic`
  Published by `user-service` after a user is created.
- `order-event`
  Published by `order-service` when an order is placed.
- `order-execute`
  Published by `order-service` after an order is executed with the latest user snapshot.

### Flow

1. User is created in `user-service`.
2. `user-service` publishes `UserEvent` to `user-topic`.
3. `order-service` consumes `UserEvent` and updates `user_cache` and `holding_cache`.
4. Client places an order in `order-service`.
5. `order-service` stores the order, publishes `OrderEvent`, and executes it from Kafka.
6. After execution, `order-service` publishes an updated `UserEvent` to `order-execute`.
7. `user-service` consumes that event and refreshes the user balance and holdings table.

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Data JPA
- Spring Kafka
- PostgreSQL
- Kafka + Zookeeper
- Docker Compose
- Finnhub market data API

## Running the Project

### Prerequisites

- Java 21+
- Maven or Maven Wrapper
- Docker Desktop

### Important build note

The Dockerfiles only copy prebuilt jars from each service `target/` directory. Build the jars before starting Docker.

### Build

Build and test both services:

```powershell
cd order-service
.\mvnw.cmd test

cd ..\user-service
.\mvnw.cmd test
```

If `common` changes and is not already available in your local Maven repository, install it first:

```powershell
cd common
mvn install
```

### Start with Docker Compose

From the project root:

```powershell
docker compose up --build
```

Services:

- `user-service`: `http://localhost:8081`
- `order-service`: `http://localhost:8082`
- PostgreSQL: `localhost:5432`
- Kafka: `localhost:9092`

## API Reference

### User Service

Base URL: `http://localhost:8081`

#### `GET /user/test`

Health check endpoint.

#### `POST /user/add`

Create a new user.

Request:

```json
{
  "name": "nikhil",
  "email": "nikhil@example.com"
}
```

Response:

```json
{
  "id": 1,
  "name": "nikhil",
  "email": "nikhil@example.com",
  "balance": 100000
}
```

#### `GET /user/{id}`

Fetch a user summary.

#### `GET /user/{id}/holdings`

Fetch holdings enriched with live price data from `order-service`.

Response shape:

```json
[
  {
    "id": 1,
    "symbol": "AAPL",
    "quantity": 10,
    "averageCost": 180.50,
    "currentPrice": 192.30,
    "investedValue": 1805.00,
    "marketValue": 1923.00,
    "pnl": 118.00
  }
]
```

### Order Service

Base URL: `http://localhost:8082`

#### `GET /order/test`

Health check endpoint.

#### `GET /order/price/{symbol}`

Fetch live quote data from Finnhub.

#### `POST /order/{userId}`

Place a buy or sell order.

Request:

```json
{
  "symbol": "AAPL",
  "type": "BUY",
  "quantity": 5
}
```

#### `GET /order/{userId}/history`

Fetch order history for a user.

Response shape:

```json
[
  {
    "id": 1,
    "userId": 1,
    "symbol": "AAPL",
    "type": "BUY",
    "quantity": 5,
    "priceAtOrder": 192.30,
    "status": "EXECUTED",
    "createdAt": "2026-04-06T10:49:13.000Z",
    "executedAt": "2026-04-06T10:49:13.700Z"
  }
]
```

## Data Model Summary

### user-service

- `users`
  Canonical user record and balance.
- `holdings`
  Current holdings for each user.

### order-service

- `user_cache`
  Cached user state mirrored from `user-service`.
- `holding_cache`
  Cached holdings mirrored and updated during execution.
- `orders`
  Order history and execution status.

## Notes

- `GET /user/{id}/holdings` depends on `order-service` being available because it reuses `GET /order/price/{symbol}`.
- Kafka startup can log temporary connection warnings while the broker is still becoming ready.
- This project currently uses snapshot-style holdings synchronization after execution rather than incremental position updates.
