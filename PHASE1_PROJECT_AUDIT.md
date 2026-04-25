# Phase 1 — Senior Project Audit (Java + Spring Boot + Kafka OMS)

## 1) High-level summary of current project

This repository is a **modular monolith scaffold** for an Order Management System (OMS) with these modules:

- `oms-bootstrap`: application entrypoint, infrastructure wiring, security, configuration.
- `oms-order`: order API/domain + persistence entities + outbox entity.
- `oms-shared-kernel`: shared event envelope abstraction.
- `oms-testing`: shared Testcontainers dependencies.

The stack and intent are strong for senior-level learning:

- Java 21 and Spring Boot 3.x parent setup.
- PostgreSQL + Flyway migration baseline.
- Kafka and outbox/processed_messages tables present.
- Security baseline with OAuth2 Resource Server + method security.
- Actuator + Prometheus dependencies and endpoint exposure.

However, the project is currently at **early scaffold maturity**: some abstractions exist but are not yet fully wired into executable business flow (especially use-case implementation, persistence adapters, and Kafka runtime flow).

---

## 2) Current architecture analysis

### What is good

1. **Module boundaries exist** and are meaningful.
2. **Domain model exists** (`Order`, `OrderItem`, `OrderStatus`) with core invariants in constructors/records.
3. **Outbox and idempotency table schema exists** (good production intent).
4. **Security defaults are not permissive by default** (`anyRequest().authenticated()`).
5. **Migration-first mindset** (Flyway + `ddl-auto: validate`).

### Where architecture is currently weak

1. **Use case boundary leaks transport DTO into application layer** (`PlaceOrderUseCase` accepts `PlaceOrderRequest`).
2. **API currently computes business total in controller**, duplicating domain logic and risking divergence.
3. **No visible implementation of use-case orchestration** (ports/adapters not yet complete).
4. **Infrastructure entities are present but incomplete** (fields only, no repository/mapper/service wiring).
5. **Hexagonal direction is partial**: currently mixed concerns in module packages and not fully enforced via ports.

### Architectural maturity verdict

- Current state: **good scaffold, not production-grade yet**.
- Recommended target: **modular monolith with explicit hexagonal boundaries** before considering microservices.

---

## 3) Strengths to keep

1. **Java/Spring baseline versions are modern** and aligned with senior backend expectations.
2. **Schema includes optimistic locking and outbox/idempotency primitives**.
3. **Security + observability dependencies already included** (good readiness).
4. **Initial domain tests exist** and validate critical invariants.
5. **Docker compose and make targets** improve reproducibility for local environments.

---

## 4) Weaknesses and senior-level gaps (prioritized)

## A. Architecture & code quality

### A1) Application use-case depends on API DTO
- **Issue:** `PlaceOrderUseCase#execute(PlaceOrderRequest)` couples application layer to web DTO.
- **Why problem:** Breaks Clean/Hexagonal boundary; blocks reuse for other adapters (Kafka/CLI/batch).
- **Risk:** Medium.
- **Impact:** Expensive refactor later when adding new input adapters.
- **Fix:** Introduce application command object (`PlaceOrderCommand`) and mapper at controller boundary.

### A2) Business calculation in controller
- **Issue:** `OrderController` calculates `totalAmount` from request body.
- **Why problem:** Business rule duplication and trust on client payload path.
- **Risk:** High.
- **Impact:** Inconsistent totals between persisted domain and API response.
- **Fix:** Return total from domain/application result (`PlaceOrderResult`) instead.

### A3) Persistence entities without full adapter implementation
- **Issue:** JPA entities exist, but no repositories/mappers/adapters visible for transactional flow.
- **Why problem:** Architecture appears complete but runtime use-case likely incomplete.
- **Risk:** High.
- **Impact:** Inability to prove production flow under tests.
- **Fix:** Implement outgoing ports and adapters (`OrderRepositoryPort`, `JpaOrderRepositoryAdapter`, mapper).

## B. Kafka/event-driven design

### B1) Outbox table exists but no visible publisher pipeline
- **Issue:** Schema includes `outbox_events` but no scheduler/transactional publisher/relay code visible.
- **Why problem:** Event delivery reliability pattern is only theoretical.
- **Risk:** High.
- **Impact:** Lost integration events or non-deterministic consistency across systems.
- **Fix:** Implement transactional outbox write + separate publisher with retry and status transitions.

### B2) No dead-letter/retry handling policy in code
- **Issue:** Kafka producer properties exist, but no consumer retry/DLT setup shown.
- **Why problem:** Poison messages can block partitions or be dropped inconsistently.
- **Risk:** High.
- **Impact:** Operational incidents, manual offset intervention.
- **Fix:** Add `DefaultErrorHandler`, backoff, DLT topic naming convention, and replay procedure.

### B3) Event schema/versioning governance not yet enforced
- **Issue:** `event_version` field exists in DB, but no event contract strategy in code.
- **Why problem:** Event consumers will break when payload evolves.
- **Risk:** Medium.
- **Impact:** Cross-service deployment coupling.
- **Fix:** Introduce explicit versioned event classes + compatibility policy.

## C. Database

### C1) Missing order_items table in migration
- **Issue:** Domain has order items, but migration only defines `orders` table.
- **Why problem:** Cannot persist item-level data or reconstruct aggregate correctly.
- **Risk:** High.
- **Impact:** Data model does not match domain model.
- **Fix:** Add `order_items` table with FK, quantity/price constraints, and indexes.

### C2) Audit model incomplete in entities
- **Issue:** Migration has `updated_at`, but `OrderEntity` does not map it.
- **Why problem:** Partial audit trail and drift between schema/entity.
- **Risk:** Medium.
- **Impact:** Harder incident forensics.
- **Fix:** Add entity field + auditing strategy (`@PreUpdate`/Spring Data auditing).

## D. Security

### D1) Resource server is configured but JWT converter/issuer validation details absent
- **Issue:** Security config is minimal; no explicit claim-to-authority mapping shown.
- **Why problem:** Role extraction mismatch is common in real IdP integrations.
- **Risk:** Medium.
- **Impact:** Unexpected 403/authorization bypass due to mis-mapped claims.
- **Fix:** Add `JwtAuthenticationConverter` and explicit issuer/audience validation.

### D2) No centralized API error model for security/validation/business exceptions
- **Issue:** No `@ControllerAdvice` visible.
- **Why problem:** Leaks inconsistent errors and potentially sensitive detail.
- **Risk:** Medium.
- **Impact:** Poor client integration and security posture.
- **Fix:** Add global exception handler with safe error codes and correlation IDs.

## E. Testing

### E1) Test pyramid is too thin (mostly domain unit tests)
- **Issue:** No controller/repository/integration/Kafka tests visible.
- **Why problem:** Critical flows (security, DB, kafka, outbox) are unverified.
- **Risk:** High.
- **Impact:** Regressions hit runtime environment first.
- **Fix:** Add WebMvc tests, DataJpa tests, and Testcontainers integration suites.

### E2) `oms-testing` dependency module exists but no shared fixtures/utilities yet
- **Issue:** Good intention but currently underused.
- **Why problem:** Duplicated test setup likely later.
- **Risk:** Low.
- **Impact:** Slower test maintenance as codebase grows.
- **Fix:** Add reusable container lifecycle and base test support classes.

## F. Observability & operations

### F1) Metrics dependencies exist but no domain/Kafka custom metrics
- **Issue:** Only base actuator exposure is configured.
- **Why problem:** Cannot detect domain throughput, outbox lag, publish failures quickly.
- **Risk:** Medium.
- **Impact:** Slow MTTR during incidents.
- **Fix:** Add counters/timers/gauges for order placement and outbox backlog.

### F2) Structured logging/correlation strategy not visible
- **Issue:** No MDC correlation-id filter/interceptor visible.
- **Why problem:** Hard to trace request->DB->Kafka path.
- **Risk:** Medium.
- **Impact:** Incident triage time increases significantly.
- **Fix:** Add request correlation filter + structured JSON logging pattern.

---

## 5) Missing senior-level concepts (explicit)

1. **Explicit inbound/outbound ports** in application layer.
2. **Transactional application service** with proper transaction boundary.
3. **Outbox relay + retry + DLT operational strategy**.
4. **Contract-first event and API versioning discipline**.
5. **Centralized error handling + problem details format**.
6. **Comprehensive test pyramid with Testcontainers integration tests**.
7. **Operational SLO-oriented metrics/logging/tracing hooks**.
8. **Config hardening for environments (dev/staging/prod profiles and secrets strategy)**.

---

## 6) Top risks (executive view)

1. **High:** Domain-to-persistence mismatch (`OrderItem` not persisted yet).
2. **High:** Event-driven reliability not implemented end-to-end despite outbox schema.
3. **High:** Thin automated test coverage outside pure domain logic.
4. **Medium:** Layering violations can compound future refactor cost.
5. **Medium:** Security mapping/config may fail in real IdP integration.

---

## 7) Prioritized improvement roadmap (practical)

### Priority 0 — Stabilize core correctness (immediate)
1. Decouple use-case from API DTO.
2. Move total amount ownership to domain/application result.
3. Add missing persistence model for order items.
4. Implement transactional order placement service.

### Priority 1 — Make event flow real and safe
1. Write outbox record in same transaction as order write.
2. Implement outbox publisher job with batching + lock/claim pattern.
3. Add retry/backoff + DLT for consumers.
4. Add idempotency guard usage with `processed_messages`.

### Priority 2 — Raise confidence via tests
1. Controller tests for validation + auth behavior.
2. Repository tests for schema/query assumptions.
3. End-to-end integration test with Postgres + Kafka Testcontainers.
4. Failure-path tests for outbox retry and duplicate consumption.

### Priority 3 — Production readiness
1. Global exception handling + problem details.
2. Structured logs + correlation id.
3. Custom Micrometer metrics (orders, outbox lag, publish error rate).
4. Health indicators for Kafka/Postgres/Redis critical readiness.

### Priority 4 — Advanced (after fundamentals)
1. Resilience4j policies around external integrations only where needed.
2. Redis caching for read-heavy endpoints (when those endpoints exist).
3. CI pipeline gates: unit/integration split, quality checks, container image scan.

---

## 8) Recommendation on architecture style (microservices vs modular monolith)

For this project stage, **keep modular monolith**.

- You already have strong module separation potential.
- Current priority is **internal boundaries + reliability + tests**, not service decomposition.
- Splitting to microservices now would multiply operational complexity before core quality is stable.

Move to microservices only after:
- bounded context ownership is clear,
- event contracts are stable,
- observability and test maturity are proven.

---

## 9) Suggested next step (Phase 2 trigger)

Proceed to **Phase 2: production-grade checklist gap analysis** using this audit as baseline, then pick **one P0 refactor** (use-case decoupling + transactional service) as first implementation slice.
