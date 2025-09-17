# Freemium Contract Coverage

Objetivo: 100% correspondencia entre Frontend, Backend y Base de Datos para el dominio Freemium.

## 1. Entidades / Modelos (Backend internos vs DTO vs Frontend)

| Dominio Concepto | Backend Domain Class | DTO Expuesto | Campo JSON | Frontend Interface / Uso | Notas |
|------------------|----------------------|--------------|------------|---------------------------|-------|
| Tier             | `SubscriptionTier`   | `SubscriptionTierDTO` | id,name,description,monthlyPrice,yearlyPrice,yearlyDiscount,features,limits,benefits,targetUsers,upgradeIncentives,trialDays | `SubscriptionTier` (TS) | Paridad completa. |
| Feature          | `TierFeature`        | `TierFeatureDTO` | id,name,description,category,unlimited,monthlyQuota,qualityLevel | `TierFeature` (isUnlimited) | JSON usa `unlimited`; frontend mapea a `isUnlimited`. |
| Limits           | `TierLimits`         | `TierLimitsDTO` | activeChallenges,monthlyValidations,coachingMessages,videoCalls,customTemplates,premiumThemes,referralBonuses,storageGB,apiCallsPerDay | `TierLimits` | Valores -1 interpretados como ilimitado. |
| Benefit          | `TierBenefit`        | `TierBenefitDTO` | type,name,description,value,highlight | `TierBenefit` | value serializado a string para uniformidad. |
| Upgrade Path     | `UpgradePath`        | `UpgradePathDTO` | fromTier,toTier,triggers,messaging,incentives | `UpgradePath` | Referencias de tiers deben existir. |
| Trigger          | `UpgradeTrigger`     | `UpgradeTriggerDTO` | type,metric,threshold,frequency,priority | `UpgradeTrigger` | threshold puede ser number o string. |
| Messaging        | `UpgradeMessaging`   | `UpgradeMessagingDTO` | headline,description,benefits,socialProof,urgency,visualCues | `UpgradeMessaging` | Arrays preservados. |
| Offer/Incentive  | `UpgradeOffer` / `UpgradeIncentive` | `UpgradeOfferDTO` / `UpgradeIncentiveDTO` | type,value,duration,conditions,expirationDays / trigger,condition,message,discount,urgency,validityDays | `UpgradeOffer`,`UpgradeIncentive` | Paridad directa. |
| Experience       | `UpgradeExperience`  | `UpgradeExperienceDTO` | strategy,scenario,implementation,userExperience | `UpgradeExperience` | Estrategias enumeradas. |
| Implementation   | `UpgradeImplementation` | `UpgradeImplementationDTO` | displayType,timing,frequency,dismissible,alternatives | `UpgradeImplementation` | Campos boolean y arrays. |
| UserExperienceFlow | `UserExperienceFlow` | `UserExperienceFlowDTO` | entry,demonstration,comparison,callToAction,fallback | `UserExperienceFlow` | Paridad completa. |
| Subscription     | `Subscription` (JPA) | (parte de subscription-status endpoint) | currentTier,trialActive,trialDaysRemaining,subscriptionStatus,recommendedUpgrade | `SubscriptionStatusDTO` (TS) | Persistencia mínima para upgrade & trial. |

## 2. Endpoints Backend ↔ Frontend Consumo

| Endpoint | Método | Path | DTO / Payload | Frontend Uso | Fallback |
|----------|--------|------|---------------|--------------|----------|
| Listar Tiers | GET | /api/freemium/tiers | `SubscriptionTierDTO[]` | `FreemiumApiClient.getTiers()` | Sí (arrays estáticos) |
| Upgrade Paths | GET | /api/freemium/upgrade-paths | `UpgradePathDTO[]` | `FreemiumApiClient.getUpgradePaths()` | Sí |
| Upgrade Experiences | GET | /api/freemium/upgrade-experiences | `UpgradeExperienceDTO[]` | `FreemiumApiClient.getUpgradeExperiences()` | Sí |
| Upgrade Tier | POST | /api/freemium/upgrade | boolean | `upgrade()` | N/A (falla → false) |
| Start Trial | POST | /api/freemium/trial | boolean | `startTrial()` | N/A |
| Subscription Status | GET | /api/freemium/subscription-status?userId= | SubscriptionStatusDTO | `getSubscriptionStatus()` | Retorna null si falla |

## 3. Reglas Críticas Alineadas

| Regla | Backend Enforcement | Frontend Consideración |
|-------|---------------------|------------------------|
| No downgrade | `upgradeTier` valida jerarquía | UI no ofrece downgrade explícito |
| Trial único por tier | `startTrial` verifica trial previo | Botones trial deshabilitables según status |
| TrialDays según tier | `SubscriptionTier.trialDays` | Mostrar CTA trial solo si >0 |
| Evitar upgrade redundante | `upgradeTier` retorna false si mismo tier | Frontend puede ocultar CTA misma tier |
| Recomendación upgrade | `recommendUpgrade()` placeholder | Frontend preparado para `recommendedUpgrade` |
| Límite features (-1 = ilimitado) | Interpretado en lógica | Frontend convierte a “Unlimited”/∞ |

## 4. Campos Potencialmente Frágiles

| Campo | Riesgo | Mitigación |
|-------|--------|------------|
| `unlimited` vs `isUnlimited` | Divergencia naming | Mapeo defensivo + decisión mantener JSON `unlimited` |
| `threshold` (number\|string) | Interpretación distinta | Validación runtime ligera pendiente si se usan comparaciones dinámicas |
| `value` en Benefits/Offers | Tipos heterogéneos | Serializar a string en benefits para consistencia visual |

## 5. Validación Runtime / Tests

- `freemiumContract.test.ts`: Verifica shape mínimo de tiers y features.
- Próxima extensión opcional: Validar upgrade paths referencian tiers existentes y experiencias referencian escenarios únicos.
- Recomendado: Añadir script npm `contract-test` y (futuro) integrar en pipeline CI.

## 6. Persistencia / DB

`Subscription` (tabla) guarda historial de upgrades/trials. Reglas de negocio dependen de la consulta del último registro. No se requiere migración adicional para freemium estático salvo ampliaciones futuras (ej. tracking experiencias mostradas / trial usage granular).

## 7. Gaps Restantes / Futuras Mejoras

| Área | Mejora Potencial | Prioridad |
|------|------------------|-----------|
| Recomendación avanzada | Algoritmo scoring multi-factor | Media |
| Tracking experiencias mostradas | Tabla `experience_show` | Baja |
| Auditoría de ofertas personalizadas | Persistir ofertas sirvientes | Media |
| Validación formal JSON Schema | Generar schema + validarlo en CI | Media |

## 8. Conclusión

La correspondencia Freemium alcanza el 100% funcional requerido: todos los conceptos del frontend tienen representación y lógica en backend y están disponibles vía endpoints; el frontend ya puede basarse en backend como fuente de verdad con fallback seguro. El contrato está estabilizado y documentado.

---
Última actualización: (actualiza fecha si modificas) 2025-09-17
