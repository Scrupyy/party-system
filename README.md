# Multi-Bungee Party System

A high scalable party system for unlimited amount of proxies. Parties handled on a standalone party-server which is the
single source of truth.

## 🚀 Features

* **Multi-Proxy Support:** Works seamlessly across an unlimited number of BungeeCord/Velocity instances.
* **Centralized Logic:** A dedicated standalone server acts as the single source of truth for all party states.
* **Real-time Communication:** Low-latency event distribution via Redis Pub/Sub.

## 🏗️ Architecture & Data Flow

1. **Client Action:** A player executes `/party invite <Player>` on `Proxy A`.
2. **Publish:** `Proxy A` sends a request packet with the payload over the Redis Pub/Sub.
3. **Validation & Processing:** The party server validates the request.
4. **Broadcast:** The party server updates its state and responds to the proxies via Redis Pub/Sub response packet.
5. **Delivery:** The proxies receive the packets and send the result message to the players.