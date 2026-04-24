# Java Kafka OMS (Learning Project)

Production-style modular monolith using Java 21 + Spring Boot + Kafka + PostgreSQL + Redis with clean architecture boundaries.

## Modules
- `oms-bootstrap`: executable Spring Boot application and wiring.
- `oms-shared-kernel`: event envelope + shared primitives.
- `oms-order`: order domain, API, persistence, and outbox scaffolding.
- `oms-testing`: shared Testcontainers support.

## Run locally
```bash
make up
```

## Build
```bash
make build
```

## Notes
This scaffold includes initial Flyway migration (`orders`, `outbox_events`, `processed_messages`) and security baseline.
