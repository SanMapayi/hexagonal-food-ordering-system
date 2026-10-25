# Hexagonal Food Ordering System

A Java/Spring project exploring domain-driven design and ports-and-adapters architecture for food ordering, with Kafka messaging infrastructure and an extension point for interpreting order notes.

## Architecture

~~~mermaid
flowchart LR
    HTTP[REST adapter] --> App[Order application use cases]
    App --> Domain[Order domain]
    App --> Ports[Output ports]
    Ports --> DB[Persistence adapters]
    Ports --> Kafka[Kafka publishers]
    App --> Notes[OrderNoteInterpreter port]
~~~

- Domain: orders, customers, restaurants, value objects, validation, and domain events.
- Application: create/track use cases, DTOs, orchestration, and repository interfaces.
- Adapters: REST endpoints, JPA mappings, and Kafka message mappings/publishers.
- Infrastructure: Kafka producer/consumer configuration, Avro schemas, and Docker Compose definitions.

## Current status

This is a work in progress, not a complete production deployment. The AI capability is an OrderNoteInterpreter interface called by the application layer; a production AI adapter is not implemented here. Order creation falls back to empty preferences when interpretation fails. Payment and restaurant integrations are represented by messaging contracts; the complete distributed workflow is not demonstrated by this repository alone.

## Build and test

Use a JDK compatible with the Spring Boot parent in pom.xml, plus Maven. The current compiler release setting is 17; dependency compatibility must also be satisfied.

~~~sh
mvn -B test
~~~

The Maven reactor builds the common, infrastructure, and order-service modules. Tests include order application-service cases. A successful unit-test build does not establish that the database, broker, or distributed integrations are working.

## API entry points

| Operation | Endpoint |
|---|---|
| Create an order | POST /orders |
| Track an order | GET /orders/{trackingId} |

The controller produces application/vnd.api.v1+json. See CreateOrderCommand for request fields. The REST adapter needs a configured application container, persistence, and supporting services; starting Kafka alone is not sufficient.

## Repository map

| Folder | Responsibility |
|---|---|
| common | Shared domain types and exception handling |
| order-service/order-domain | Business rules and use cases |
| order-service/order-application | REST adapter |
| order-service/order-dataaccess | JPA entities, repositories, and mappers |
| order-service/order-messaging | Kafka publishing and message mapping |
| infrastructure | Kafka configuration, Avro models, Docker Compose |

See [architecture notes](docs/architecture.md) for design decisions and remaining work.
## More projects

Browse the [20-project portfolio](docs/portfolio.md) for architecture, ML, automation, concurrency, desktop, and Java learning projects.
