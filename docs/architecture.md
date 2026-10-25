# Architecture and review guide

## Follow an order

Start at OrderController, then follow OrderApplicationUseCase and OrderCreateHelper. The helper checks customer and restaurant information, maps the request, interprets optional notes, invokes the domain service, and persists the order within a transaction.

Order-domain-core holds business entities and rules. Output repository interfaces let the application depend on abstractions rather than JPA. Messaging adapters map domain information to Avro messages.

## Why an AI port?

OrderNoteInterpreter keeps the use case independent of a model provider or SDK. A future adapter should handle timeouts and validate responses before mapping them to OrderPreferences. The current fallback is deliberately documented: failed interpretation does not reject the order.

## Verification boundaries

Application-service tests validate selected business behavior. Broker delivery, database migrations, transaction-to-message consistency, idempotency, and complete payment/restaurant flows need integration verification. Publishing an event and committing a database transaction are not automatically atomic; an outbox is a potential next step.

## Next improvements

- Supply and integration-test the application container and external service configuration.
- Implement an optional note-interpreter adapter with explicit timeouts and response validation.
- Add broker/database integration tests and a reproducible local demo.
- Define idempotency and delivery behavior before claiming production readiness.