# Impulse Frontend

## Calidad y mantenibilidad
- Sin ciclos ni violaciones de capas (CI: dependency-cruiser, madge)
- Cobertura de tests y política clara (Vitest, E2E)
- Logging estructurado/contextual (logger.ts, correlationId)
- PR checklist, codeowners, onboarding documentado

## Testing
- Ejecuta `npm run test` para tests unitarios
- Ejecuta `npm run coverage` para cobertura
- Política: ver `docs/testing-policy.md`

## CI/CD
- Lint de dependencias: `npm run lint:deps` (ver workflows)
- Renovate/Dependabot para deps

## Logging
- Usa `log('info', 'msg', { correlationId })` (ver `src/lib/logger.ts`)
- Adopción gradual, ver `docs/logging.md`

## Estructura de carpetas
- `src/pages/`: vistas y rutas
- `src/hooks/`: lógica de aplicación
- `src/services/`: acceso a datos y APIs
- `src/lib/`: utilidades, logger, correlation
- `docs/`: políticas, ADRs, onboarding

## Política de PR
- Ver `CONTRIBUTING.md` y checklist de PR
- Actualiza docs y tests en cada cambio

## Bootstrap y entorno
- `Makefile` con comando `bootstrap`
- Devcontainer listo para VSCode

## Changelog
- Ver `CHANGELOG.md` para cambios de políticas y baseline de mantenibilidad

---

> Este README fue actualizado como parte de la rama `chore/mantenibilidad-baseline` siguiendo el informe de auditoría estática.
