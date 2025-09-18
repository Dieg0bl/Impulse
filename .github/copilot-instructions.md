## IMPULSE v1.0 – AI Coding Agent Playbook

Focused scope: implement only features allowed by v1 (user supplied scope + existing code). Do NOT add new bounded contexts, brokers, secondary PSPs, message queues, or database tables beyond the initial migration (`backend/src/main/resources/db/migration/V1__init.sql`).

### 1. Architecture (Hexagonal Strict)
Layers flow inward only: `domain` → `application` (use cases + ports) → `adapters` (in: REST/http; out: jpa|stripe|storage) → `infrastructure` (technical concerns). No cross-leaks (e.g. a controller must not depend on a JPA entity; a domain object must not import Spring / persistence classes).

Backend (active code presently in `backup_backend/src/main/java/com/impulse` while primary folder is being migrated). Feature modules under `features/<featureName>/{domain,application,adapters,infrastructure}`. Example chain:
`features/challenge/adapters/in/rest/ChallengeController.java` → maps request via `ChallengeApiMapper` → calls `CreateChallengeUseCase` (in `application/usecase`) → persists through `ChallengeRepository` port (implemented in an out adapter).

### 2. Core Conventions
- Controllers: only request/response shaping + RBAC. All business validation lives in use cases (see `CreateChallengeUseCase.java` validation methods).
- Ports: Define in `application/port/in|out`; adapters implement them. Example outbound port: `features/challenge/application/port/out/ChallengeRepository.java`.
- Mappers: API ↔ Application (`*ApiMapper`), Application ↔ Domain (`*AppMapper`), Domain ↔ Persistence (`*JpaMapper`). Keep them dumb (no side effects, no IO).
- DTO rules: API DTOs never expose internal IDs other than public identifiers; application DTOs carry only what the use case needs.
- Error model: Responses must be `{ code, message, correlationId }` produced centrally (GlobalExceptionHandler path not present in repo snapshot—create under `infrastructure/advice/` if extending). Domain exceptions extend `DomainException` (`shared/error/DomainException.java`).
- Idempotency: Critical POST endpoints require `Idempotency-Key` header. Persistence via `IdempotencyTokenRepository` (`infrastructure/persistence/repositories/IdempotencyTokenRepository.java`). Reject reuse once consumed; mark used atomically.
- Correlation: Propagate `X-Correlation-Id` from frontend through controllers into logs/metrics (if absent, generate once at ingress).
- Decisions: Evidence review outcomes stored on `evidences` records (no separate validations table). `participations.status` defaults to `PENDING` until processed.

### 3. Persistence & Limits
- Only v1 tables; do not introduce new entities unless whitelisted by future migration. Evidence decision fields must NOT be moved to a new table.
- Stripe integration: (Paths not shown in snapshot) Implement webhook endpoint with signature verification. Store raw event metadata in `webhook_events` (idempotent by `event_id`). Never store PAN/CVV.
- Storage: Use presigned upload pattern; strip EXIF metadata server-side before persisting references.

### 4. Build, Test, Migrations, Contracts
- Build & test backend: `mvn clean verify` (Java 17; Spring Boot 3.x). Add ArchUnit tests (if/when present) to enforce layering.
- Run locally (compose): `docker-compose up --build` (ensures DB + services) when docker file references are updated.
- Migrations: Extend only existing Flyway script numbering after `V1__init.sql`; never modify an applied migration.
- OpenAPI: Keep spec in `/contracts/openapi.yaml` (add if missing). Controllers must match path + schema; update generated frontend client before FE changes rely on new endpoints.

### 5. Frontend Rules
- Data access: Use generated client (`/src/api/` or expansion) only through service layer (`/src/services/*`). Do NOT call `fetch` or axios inside React components.
- Feature flags: Central hook in `src/contexts/FlagsContext.tsx` (placeholder). Real flags should externalize to `/flags.yaml` (create if absent). Never render CTAs for disabled flags.
- Theming & design: Design tokens in `src/design/tokens.css` feed dynamic MUI theme in `src/theme/muiTheme.tsx`. When adding semantic colors, add CSS variable first, then map in theme.
- Avoid duplication: Reuse layout and shared UI components from `src/components` & `src/ui`.

### 6. Integration Patterns
- Stripe webhooks: Ensure idempotent processing (check event store before applying). Return 2xx only after durable persistence.
- Media uploads: Obtain presigned URL (backend adapter) → direct upload → notify backend with metadata → backend strips EXIF & marks scan status.
- Idempotent POST example header set:
```
Idempotency-Key: 8f6b6d2e-...-b91a
X-Correlation-Id: a1c2e3f4-...
```

### 7. Examples (from repo)
```java
// Controller delegating to use case (no business logic)
// backup_backend/.../ChallengeController.java
ChallengeResponse response = createChallengeUseCase.execute(command);
```
```java
// Outbound port definition
// backup_backend/.../application/port/out/ChallengeRepository.java
Optional<Challenge> findById(ChallengeId id);
```
```java
// Domain validation inside use case
// backup_backend/.../CreateChallengeUseCase.java
if (command.getPointsReward() != null && command.getPointsReward() < 0) {
  throw new ChallengeDomainError("Points reward cannot be negative");
}
```

### 8. Anti-Patterns (Reject PRs Introducing)
- Business rules inside controllers or mappers.
- Direct JPA/entity exposure in API DTOs.
- Skipping `Idempotency-Key` on critical POST flows (challenge creation, evidence submission, subscription ops).
- Adding new tables or event brokers for v1 scope concerns.
- Frontend components issuing raw `fetch` / bypassing services.
- Silent error handling (must surface standardized error model).
- Feature UI rendered while corresponding flag false.

### 9. When Extending
Add new use case: create command/response DTOs in `application/dto`, define or extend port(s), implement in use case, expose via new controller method + mapper. Write a test validating domain invariants before wiring controller.

If any section is unclear (e.g. missing actual exception handler path or Stripe adapter), reply with the target file path you want clarified and I’ll guide the minimal compliant addition.
