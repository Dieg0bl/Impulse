# IMPULSE - ROADMAP COMPLETO AL 100%

## 🎯 **ESTADO ACTUAL vs OBJETIVO FINAL**

### ✅ **LO QUE YA TENEMOS (25%)**
- Arquitectura hexagonal sólida (Spring Boot 3.3.3 + Java 17)
- Entidades de dominio base: User, Challenge, Evidence, ChallengeParticipation, EvidenceValidation
- JWT RS256 implementado y operativo
- RBAC con 6 roles (GUEST → SUPER_ADMIN)
- UserController con 15+ endpoints básicos
- SecurityConfig básico

### ❌ **LO QUE FALTA IMPLEMENTAR (75%)**

---

## 📅 **PLAN DE 12 FASES PARA 100% ABSOLUTO**

### **FASE 1: INFRAESTRUCTURA COMPLETA** (5-7 días)
**Objetivo**: Base técnica robusta según especificaciones

#### Entregables:
- ✅ **Docker Compose completo**
  - MySQL 8 con `impulse_master.sql`
  - MinIO (S3-compatible para media)
  - Mailhog (testing emails)
  - Redis (caché y rate-limiting)

- ✅ **Configuración avanzada**
  - `.env.template` con TODOS los toggles (BILLING_ON, COACH_MARKET_ON, ADS_ON, CMP_ON)
  - `Makefile` operativo (init, up, db-load, test, build, deploy)
  - Headers de seguridad (CSP, HSTS, X-Content-Type-Options)
  - Rate-limiting con Bucket4j (100 req/10m, login 10/10m)

- ✅ **Observabilidad base**
  - Logs JSON estructurados sin PII + X-Correlation-Id
  - Health checks (/actuator/health con DB, S3, AV, Stripe)
  - Integración Sentry (errores FE/BE)
  - Métricas básicas con Micrometer

#### Archivos a crear:
```
infra/
├── docker-compose.yml (MySQL8 + MinIO + Mailhog + Redis)
├── .env.template (todos los toggles)
├── Makefile (init, up, test, build)
└── mysql/
    └── impulse_master.sql (schema completo)

backend/
├── application-prod.yml (configuración producción)
└── src/main/java/com/impulse/lean/infrastructure/
    ├── config/SecurityHeadersConfig.java
    ├── config/RateLimitConfig.java
    └── monitoring/HealthIndicators.java
```

---

### **FASE 2: ENTIDADES Y DOMINIO COMPLETO** (7-10 días)
**Objetivo**: Todas las entidades según especificación + máquinas de estado

#### Entregables:
- ✅ **Entidades faltantes**
  - `Coach` (marketplace coaches)
  - `CoachStatsWeekly` (métricas semanales)
  - `Invitation` (invitaciones de validadores)
  - `Report` (moderación UGC)
  - `Appeal` (apelaciones DSA)
  - `DataExportRequest` (GDPR exports)
  - `ReferralCode` (sistema referidos)

- ✅ **Máquinas de estado avanzadas**
  - Challenge: DRAFT → ACTIVE → COMPLETED/CANCELLED/EXPIRED
  - Evidence: PENDING → APPROVED/REJECTED/FLAGGED
  - Validation: PENDING → COMPLETED
  - Coach: STARTER → RISING → CHAMPION
  - DataExport: REQUESTED → PROCESSING → READY → EXPIRED

- ✅ **Algoritmos de scoring**
  - UCI (User Consistency Index): disciplina 0-100
  - CPS (Challenge Priority Score): prioridad 0-1
  - ERSS (Evidence Review SLA Score): calidad validación
  - VTS (Validator Trust Score): confianza validador
  - RQS (Referral Quality Score): calidad referidos
  - FRS (Fraud Risk Score): detección fraude

#### Archivos a crear:
```
backend/src/main/java/com/impulse/lean/
├── domain/model/
│   ├── Coach.java
│   ├── CoachStatsWeekly.java
│   ├── Invitation.java
│   ├── Report.java
│   ├── Appeal.java
│   ├── DataExportRequest.java
│   └── ReferralCode.java
├── domain/service/
│   ├── ScoringAlgorithms.java
│   ├── StateMachines.java
│   └── ValidationRules.java
└── domain/repository/ (repositories para nuevas entidades)
```

---

### **FASE 3: API REST COMPLETA** (8-12 días)
**Objetivo**: Todos los controladores según especificación

#### Entregables:
- ✅ **ChallengesApi completo**
  - CRUD retos + estados
  - Subrutas: evidencias, validaciones, validadores
  - Filtros y paginación avanzada

- ✅ **ValidatorsApi**
  - POST /api/v1/validators/invites (enviar invitaciones)
  - POST /api/v1/validators/accept (aceptar invitaciones)
  - GET /api/v1/validators/dashboard (panel validador)

- ✅ **PrivacyModerationApi**
  - GDPR: POST /api/v1/privacy/{export,delete,visibility}
  - DSA: POST /api/v1/moderation/{reports,actions,appeals}
  - GET /api/v1/dsa/amar (transparency reporting)

- ✅ **BillingApi (toggleable)**
  - GET /api/v1/billing/plans
  - POST /api/v1/subscriptions/{create,cancel,update}
  - POST /api/v1/billing/webhooks (Stripe/PayPal)

- ✅ **Rate-limits específicos por endpoint**
  - Login: 10/10min
  - Upload: 20/hour  
  - API general: 100/10min
  - Reports: 5/hour

#### Archivos a crear:
```
backend/src/main/java/com/impulse/lean/application/controller/
├── ChallengeController.java (completo con subrutas)
├── ValidatorController.java
├── PrivacyController.java
├── ModerationController.java
├── BillingController.java (con toggle BILLING_ON)
└── PublicController.java (perfiles públicos)

backend/src/main/java/com/impulse/lean/application/dto/
├── challenge/ (DTOs retos)
├── validator/ (DTOs validadores)
├── privacy/ (DTOs GDPR)
├── moderation/ (DTOs DSA)
└── billing/ (DTOs facturación)
```

---

### **FASE 4: SERVICIOS DE NEGOCIO CORE** (10-15 días)
**Objetivo**: Todos los casos de uso críticos (≤400 SLOC c/u)

#### Entregables:
- ✅ **Servicios principales**
  - `CreateChallengeService`
  - `SubmitEvidenceService`
  - `ValidateEvidenceService`
  - `InviteValidatorService`
  - `AcceptValidatorService`
  - `ChangeVisibilityService`
  - `RequestExportService`
  - `RequestDeleteService`
  - `ReportContentService`
  - `ActOnReportService`
  - `AppealService`

- ✅ **Jobs programados**
  - `ComputeScoresJob` (semanal: UCI, CPS, ERSS, etc.)
  - `WinBackJob` (usuarios en riesgo UCI≥70)
  - `BetaReminderJob` (avisos D-15/D-7/D-1)
  - `DataRetentionJob` (limpieza según GDPR)

- ✅ **Adaptadores externos**
  - `StorageAdapter` (MinIO/S3 con URLs firmadas)
  - `MailAdapter` (Brevo/Sendinblue)
  - `StripeAdapter` (pagos y webhooks)
  - `AntivirusAdapter` (ClamAV/external)

#### Archivos a crear:
```
backend/src/main/java/com/impulse/lean/application/service/
├── challenge/
│   ├── CreateChallengeService.java
│   ├── SubmitEvidenceService.java
│   └── ValidateEvidenceService.java
├── validator/
│   ├── InviteValidatorService.java
│   └── AcceptValidatorService.java
├── privacy/
│   ├── ChangeVisibilityService.java
│   ├── RequestExportService.java
│   └── RequestDeleteService.java
├── moderation/
│   ├── ReportContentService.java
│   ├── ActOnReportService.java
│   └── AppealService.java
└── jobs/
    ├── ComputeScoresJob.java
    ├── WinBackJob.java
    ├── BetaReminderJob.java
    └── DataRetentionJob.java

backend/src/main/java/com/impulse/lean/infrastructure/adapter/
├── StorageAdapter.java
├── MailAdapter.java
├── StripeAdapter.java
└── AntivirusAdapter.java
```

---

### **FASE 5: INTEGRACIÓN STRIPE/PAYPAL** (6-8 días)
**Objetivo**: Monetización completa con toggles

#### Entregables:
- ✅ **Stripe integration**
  - Customer Portal habilitado
  - Stripe Tax ON (IVA/OSS UE)
  - Webhooks: subscription_created, payment_succeeded, payment_failed
  - Dunning automático (reintentos D+3/D+7)

- ✅ **PayPal Subscriptions**
  - PayPal Checkout integration
  - Webhook handling
  - Plan management

- ✅ **Billing logic**
  - Toggle BILLING_ON (oculta checkout hasta fiscal ready)
  - Planes: Basic (gratis), Pro (12,99€/mes), Teams (39,99€/mes)
  - Beta 90 días gratuitos sin tarjeta
  - Garantía 30 días

#### Archivos a crear:
```
backend/src/main/java/com/impulse/lean/infrastructure/billing/
├── StripeService.java
├── PayPalService.java
├── BillingEventHandler.java
├── DunningService.java
└── PlanLimitationService.java
```

---

### **FASE 6: FRONTEND PWA BASE** (10-12 días)
**Objetivo**: Las 6 páginas específicas con componentes reutilizables

#### Entregables:
- ✅ **6 páginas principales**
  - Dashboard (saludo, CTA, reto principal, progreso)
  - Challenges (filtros, grid, crear/editar)
  - Validators (panel validador, invitaciones)
  - PrivacyModeration (GDPR self-service, reportes)
  - Billing (planes, Customer Portal si BILLING_ON)
  - Account (perfil, preferencias, export/delete)

- ✅ **Componentes reutilizables**
  - `ChallengeForm` (crear/editar retos)
  - `EvidenceUpload` (subida multimedia)
  - `InviteAcceptCard` (aceptar invitaciones)
  - `PlanCards` (comparación planes)
  - `ProgressBar` (progreso retos)

- ✅ **PWA completa**
  - Manifest (192/512px icons)
  - Service Worker (cache shell + SWR)
  - Instalable (display: standalone)

#### Archivos a crear:
```
frontend/src/
├── pages/
│   ├── Dashboard.tsx
│   ├── Challenges.tsx
│   ├── Validators.tsx
│   ├── PrivacyModeration.tsx
│   ├── Billing.tsx
│   └── Account.tsx
├── components/
│   ├── ChallengeForm.tsx
│   ├── EvidenceUpload.tsx
│   ├── InviteAcceptCard.tsx
│   ├── PlanCards.tsx
│   └── ProgressBar.tsx
├── services/
│   ├── api.ts (todas las llamadas REST)
│   └── store.ts (estado global)
├── public/
│   ├── manifest.json
│   └── sw.js
└── utils/
    └── pwa.ts
```

---

### **FASE 7: INTEGRACIÓN FRONTEND-BACKEND** (5-7 días)
**Objetivo**: Conexión completa FE-BE con autenticación

#### Entregables:
- ✅ **API client completo**
  - Todas las llamadas REST implementadas
  - JWT token management
  - Error handling y retry logic
  - Interceptores para auth

- ✅ **State management**
  - User state (perfil, roles, permisos)
  - Challenge state (lista, filtros, estado)
  - UI state (loading, errors, notifications)

- ✅ **Routing y navegación**
  - Protected routes por rol
  - Deep links (perfiles públicos, invitaciones)
  - 404 y error boundaries

---

### **FASE 8: SISTEMA DE MEDIA Y STORAGE** (4-6 días)
**Objetivo**: Upload seguro con antivirus y URLs firmadas

#### Entregables:
- ✅ **Pipeline de media**
  - Presigned URLs (MinIO/S3)
  - Antivirus on-upload (ClamAV)
  - EXIF strip (privacidad)
  - Límites: jpeg/png/mp4, ≤25MB

- ✅ **Almacenamiento**
  - Basic: texto + 1 imagen
  - Pro: multimedia completa
  - Cloudflare R2 integration

---

### **FASE 9: COMPLIANCE GDPR/DSA** (7-10 días)
**Objetivo**: Legal compliance completo

#### Entregables:
- ✅ **GDPR compliance**
  - Consentimientos versionados
  - Data export (JSON/ZIP)
  - Right to deletion (14 días reversión)
  - Data retention automático
  - Audit trail completo

- ✅ **DSA compliance**
  - Notice & action (reportes)
  - Appeals system
  - Transparency reports (AMAR)
  - Content moderation SLA ≤72h

- ✅ **Cookie management**
  - CMP condicional (si GA4/Ads)
  - Sin tracking cookies por defecto
  - Plausible analytics (UE, sin cookies)

---

### **FASE 10: COACH MARKETPLACE** (8-10 días)
**Objetivo**: Marketplace con Stripe Connect (toggleable)

#### Entregables:
- ✅ **Stripe Connect Standard**
  - Coach onboarding
  - Split payments (50% max)
  - Payouts y reservas
  - KYC handling

- ✅ **Coach levels**
  - Starter → Rising → Champion
  - Coach Score algorithm
  - Level benefits y restrictions

- ✅ **Toggle COACH_MARKET_ON**
  - Deshabilitado en beta
  - Waitlist y "interest" forms

---

### **FASE 11: OBSERVABILIDAD Y MONITOREO** (5-7 días)
**Objetivo**: Métricas, alertas y dashboard operativo

#### Entregables:
- ✅ **Métricas críticas**
  - Business: conversión, retención 30/90, MRR
  - Technical: UCI, CPS, ERSS scores
  - Operational: 5xx, p95, SLA

- ✅ **Alertas automáticas**
  - Errores críticos (Sentry)
  - Performance degradation
  - DSAR pendientes >72h
  - Webhook failures (Stripe)
  - High fraud score alerts

- ✅ **Dashboard interno**
  - Real-time metrics
  - User health scores
  - Financial KPIs (cuando BILLING_ON)

---

### **FASE 12: TESTING Y DEPLOYMENT** (6-8 días)
**Objetivo**: ≥80% cobertura, CI/CD, producción ready

#### Entregables:
- ✅ **Testing completo**
  - Unit tests: ≥80% cobertura
  - Integration tests: API endpoints
  - E2E tests: user journeys críticos
  - Security tests: vulnerabilidades

- ✅ **CI/CD pipeline**
  - GitHub Actions
  - Automated testing
  - Security scans
  - Deployment automático

- ✅ **Production setup**
  - Kubernetes/Docker Swarm
  - Load balancing
  - SSL/TLS certificates
  - Backup strategy
  - Disaster recovery

---

## 📊 **RESUMEN EJECUTIVO**

### **Fases necesarias: 12 FASES**
### **Tiempo estimado: 18-24 semanas (4.5-6 meses)**
### **Complejidad: ALTA** 

### **Componentes totales a implementar:**
- **32 nuevas entidades/servicios**
- **45+ endpoints API adicionales**
- **6 páginas frontend completas**
- **15+ componentes React reutilizables**
- **8 adaptadores externos**
- **12 jobs programados**
- **Compliance GDPR/DSA completo**
- **Stripe/PayPal integration**
- **Coach marketplace**
- **PWA con service worker**
- **Observabilidad completa**

### **Archivos estimados a crear/modificar: 150-200**

---

## 🎯 **RECOMENDACIÓN ESTRATÉGICA**

Dado el scope masivo, sugiero:

1. **ENFOQUE POR MVPs**: Implementar versiones mínimas viables de cada fase
2. **PRIORIZACIÓN**: Empezar por FASES 1-4 (core business value)
3. **PARALELIZACIÓN**: Frontend + Backend en paralelo después de FASE 3
4. **TESTING CONTINUO**: No dejar testing para el final

¿Quieres que empecemos con alguna fase específica o prefieres un roadmap más detallado de alguna en particular?
